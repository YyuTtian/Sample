package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Value("${sky.shop.address}")
    private String shopAddress;     //店铺地址
    @Value("${sky.shop.ak}")
    private String ak;              //百度地图应用服务ak
    @Value("${sky.shop.distance}")
    private Integer distance;       //配送距离上限
    //地理编码接口，获取地址的经纬度坐标
    private static final String GEO_CODING_URL = "https://api.map.baidu.com/geocoding/v3/";
    //路线规划接口，骑行路线规划，（可以视业务情况选择骑行、公交、驾车等路线，接口不一样，可以配置在yml中）
    private static final String DIRECTION_URL = "https://api.map.baidu.com/directionlite/v1/riding";


    /**
     * 用户下单--最终目的，将订单数据存入表中（orders、order_detail）
     * @param dto
     * @return
     */
    @Transactional  //开启事务
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        // 查询地址表，获取收货人信息
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        if (addressBook == null) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 查询用户表，获取用户相关信息
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new OrderBusinessException(MessageConstant.USER_NOT_LOGIN);
        }

        // 查询购物车列表数据---只查询自己名下的购物车数据
        List<ShoppingCart> cartList = shoppingCartMapper.list(userId);
        if (cartList == null || cartList.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //优化下单逻辑：需要检验配送范围（如果超过5公里，就抛出自定义异常）
        if (!checkOutOfRange(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail())) {
            throw new OrderBusinessException(MessageConstant.OUT_OF_RANGE);
        }

        // 1.构造订单数据，存入orders表中
        Orders orders = new Orders();
        // 拷贝属性值
        BeanUtils.copyProperties(dto, orders);
        // 补充缺失的属性值
        orders.setNumber(System.currentTimeMillis()+"");    // 订单编号
        orders.setStatus(Orders.PENDING_PAYMENT);           // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        orders.setUserId(userId);                           // 下单人id
        orders.setOrderTime(LocalDateTime.now());           // 下单时间
        orders.setPayStatus(Orders.UN_PAID);                // 支付状态 0未支付 1已支付 2退款
        orders.setPhone(addressBook.getPhone());            // 收货人手机号
        orders.setAddress(addressBook.getDetail());         // 收货地址
        orders.setConsignee(addressBook.getConsignee());    // 收货人
        orders.setUserName(user.getName());                 // 下单人姓名
        // 新增订单数据
        ordersMapper.insert(orders);
        log.info("订单id:{}",orders.getId());

        // 2.构造订单明细数据，存入order_detail表中
        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 循环遍历购物车列表数据，构造订单明细
        cartList.forEach(cart->{
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail, "id");
            // 关联订单id
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });
        // 批量插入订单明细数据
        orderDetailMapper.insertBatch(orderDetailList);

        // 3.清空购物车--删除自己名下的购物车列表数据
        shoppingCartMapper.delete(userId);

        // 4.构造OrderSubmitVO对象，并返回
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // // 当前登录用户id
        // Long userId = BaseContext.getCurrentId();
        // User user = userMapper.selectById(userId);
        //
        // //调用微信支付接口，生成预支付交易单
        // JSONObject jsonObject = weChatPayUtil.pay(
        //         ordersPaymentDTO.getOrderNumber(), //商户订单号
        //         new BigDecimal(0.01), //支付金额，单位 元
        //         "苍穹外卖订单", //商品描述
        //         user.getOpenid() //微信用户的openid
        // );
        //
        // if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
        //     throw new OrderBusinessException("该订单已支付");
        // }
        //
        // OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        // vo.setPackageStr(jsonObject.getString("package"));
        //
        // return vo;

        /////////////////////////////////////////////////////////////////////
        // 模拟支付成功---修改订单状态
        // 业务处理，修改订单状态、来单提醒
        paySuccess(ordersPaymentDTO.getOrderNumber());
        // 返回一个空对象即可
        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = ordersMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);

        // 来单提醒----》订单支付成功后需要主动往客户端推送消息
        Map map = new HashMap();
        map.put("type", 1);                 //1-代表来单提醒
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + ordersDB.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 校验配送范围
     */
    private boolean checkOutOfRange(String address) {
        //1.获取店铺的地址坐标
        //https://api.map.baidu.com/geocoding/v3/?address=北京市海淀区上地十街10号&output=json&ak=您的ak
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address", this.shopAddress);
        paramMap.put("output", "json");
        paramMap.put("ak", this.ak);
        String jsonStr = HttpClientUtil.doGet(GEO_CODING_URL, paramMap);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if (!jsonObject.get("status").equals(0)) { //地址解析失败
            throw new OrderBusinessException(MessageConstant.ADDRESS_PARSE_FAILED);
        }
        JSONObject jsonObject1 = jsonObject.getJSONObject("result").getJSONObject("location");
        Object lng = jsonObject1.get("lng");   //经度
        Object lat = jsonObject1.get("lat");   //维度
        String origin = lat + "," + lng;
        log.info("店铺的坐标：{}", origin);

        //2.获取收货地址的坐标
        paramMap.put("address", address);
        jsonStr = HttpClientUtil.doGet(GEO_CODING_URL, paramMap);
        jsonObject = JSON.parseObject(jsonStr);
        if (!jsonObject.get("status").equals(0)) { //地址解析失败
            throw new OrderBusinessException(MessageConstant.ADDRESS_PARSE_FAILED);
        }
        jsonObject1 = jsonObject.getJSONObject("result").getJSONObject("location");
        lng = jsonObject1.get("lng");   //经度
        lat = jsonObject1.get("lat");   //维度
        String destination = lat + "," + lng;
        log.info("收货地址的坐标：{}", destination);

        //3.根据店铺和收货地址坐标，进行距离计算，如果超出5公里，就返回false
        //https://api.map.baidu.com/directionlite/v1/riding?origin=40.01116,116.339303&destination=39.936404,116.452562&ak=您的AK
        paramMap.put("origin", origin);
        paramMap.put("destination", destination);
        jsonStr = HttpClientUtil.doGet(DIRECTION_URL, paramMap);
        jsonObject = JSON.parseObject(jsonStr);
        if (!jsonObject.get("status").equals(0)) { //地址解析失败
            throw new OrderBusinessException(MessageConstant.ADDRESS_PARSE_FAILED);
        }
        JSONArray routes = jsonObject.getJSONObject("result").getJSONArray("routes");
        Integer distance = (Integer) routes.getJSONObject(0).get("distance");
        log.info("配送距离：{}米", distance);

        return distance <= this.distance;
    }

    /**
     * 订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult page(Integer page, Integer pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(page, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> p = ordersMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();
        // 查询出订单明细，并封装入OrderVO进行响应
        if (p != null && p.getTotal() > 0) {
            for (Orders orders : p) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
            return new PageResult(p.getTotal(), list);
        }
        return null;
    }


    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = ordersMapper.getById(id);

        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        // 用户端取消订单，只能取消还未接单的订单，其他情况取消订单，需要联系商家在后台取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 需要判断订单支付状态，订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            //调用微信支付退款接口 --由于没有可用的商户id，下面的退款操作执行会失败，直接注释即可，假设退款成功了，直接修改状态
            // weChatPayUtil.refund(
            //         ordersDB.getNumber(), //商户订单号
            //         ordersDB.getNumber(), //商户退款单号
            //         new BigDecimal(0.01),//退款金额，单位 元
            //         new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }


    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = ordersMapper.pageQuery(ordersPageQueryDTO);
        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);
        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();
        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);
                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = ordersMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = ordersMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = ordersMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        ordersMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 需要判断订单支付状态，订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            //用户已支付，需要退款
            // String refund = weChatPayUtil.refund(
            //         ordersDB.getNumber(),
            //         ordersDB.getNumber(),
            //         new BigDecimal("0.01"),
            //         new BigDecimal("0.01"));
            // log.info("申请退款：{}", refund);
            log.info("申请退款,修改支付状态");
            ordersDB.setPayStatus(Orders.REFUND);
        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        ordersDB.setStatus(Orders.CANCELLED);
        ordersDB.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        ordersDB.setCancelTime(LocalDateTime.now());

        ordersMapper.update(ordersDB);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.getById(ordersCancelDTO.getId());
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //只需判断支付状态，支付状态如果为已支付，则需要退款并修改支付状态
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            //用户已支付，需要退款---商户id不存在无法回调，所以
            // String refund = weChatPayUtil.refund(
            //         ordersDB.getNumber(),
            //         ordersDB.getNumber(),
            //         new BigDecimal("0.01"),
            //         new BigDecimal("0.01"));
            // log.info("申请退款：{}", refund);
            // 支付状态修改为退款
            log.info("支付状态修改为退款");
            ordersDB.setPayStatus(Orders.REFUND);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        ordersDB.setStatus(Orders.CANCELLED);
        ordersDB.setCancelReason(ordersCancelDTO.getCancelReason());
        ordersDB.setCancelTime(LocalDateTime.now());
        ordersMapper.update(ordersDB);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    public void delivery(Long id) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        ordersMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        ordersMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        // 催单----》用户点击催单按钮，需要服务端给客户端推送消息
        Orders orders = ordersMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map = new HashMap();
        map.put("type", 2);         //2-代表催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
