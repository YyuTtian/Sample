package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类：处理超时未支付订单与一直派送中的订单
 */
@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 每分钟检查一次是否存在超时未支付订单（下单超过15分钟未支付就代表超时，需要修改状态为已取消）
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processOutTimeOrder(){
        log.info("查看是否存在【超时未支付】的订单");
        // 1.查询数据库orders表，条件：状态-待付款，下单时间 < 当前时间 - 15分钟
        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> orderList = ordersMapper.selectByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        // 2.如果查询到了数据，代表存在超时未支付订单，需要修改订单的状态为 “status = 6”
        // update ....
        if (orderList != null && orderList.size() > 0) {
            orderList.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            });
        }
    }

    /**
     * 每天凌晨1点检查一次订单表，查看是否存在“派送中”的订单，如果存在修改状态为"已完成"
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("查看是否存在【派送中】的订单");
        // 1.查询数据库orders表，条件：状态-派送中，下单时间 < 当前时间 - 1h
        // select * from orders where status = 4 and order_time < 当前时间- 1小时
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> orderList = ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        // 2.如果查询到了数据，代表存在一直派送中的订单，需要修改订单的状态为 “status = 5”
        if (orderList != null && orderList.size() > 0) {
            orderList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(time);
                ordersMapper.update(orders);
            });
        }
    }
}
