package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addCart(ShoppingCartDTO dto) {
        // 创建ShoppingCart对象
        ShoppingCart shoppingCart = new ShoppingCart();
        // 拷贝属性值
        BeanUtils.copyProperties(dto, shoppingCart);

        // 1.判断该商品是否已经存在购物车--条件：dishId+dishFlavor+userId
        // 只查当前用户自己的购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);

        if (cart == null) { //代表购物车没有该商品数据
            // 2.补充缺失的属性值
            // 判断是新增套餐还是新增菜品
            if (dto.getDishId() != null) {  //代表新增的是菜品
                // 根据菜品的id查询菜品表，获取菜品相关信息
                Dish dish = dishMapper.selectById(dto.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            } else{                         //代表新增的是套餐
                Setmeal setmeal = setmealMapper.getById(dto.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setNumber(1);   //数量---》到底是1还是加1？判断该商品是否已经存在购物车
            shoppingCart.setCreateTime(LocalDateTime.now());

            // 3.将商品数据存入到shopping_cart表中
            shoppingCartMapper.insert(shoppingCart);
        }else { //代表购物车有该商品数据
            // 4.将原来的购物车商品数量+1,调用mapper更新方法
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        }

        // 最终目的：将用户添加的商品，存入到购物车表中shopping_cart表
    }

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        // 注意：只能查看自己名下的购物车
        return shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    @Override
    public void clean() {
        // 注意：只能清空自己名下的购物车
        shoppingCartMapper.delete(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> cartDBList = shoppingCartMapper.listBy(shoppingCart);

        if(cartDBList != null && !cartDBList.isEmpty()){
            ShoppingCart cartDB = cartDBList.get(0);
            Integer number = cartDB.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(cartDB.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                cartDB.setNumber(cartDB.getNumber() - 1);
                shoppingCartMapper.update(cartDB);
            }
        }
    }
}
