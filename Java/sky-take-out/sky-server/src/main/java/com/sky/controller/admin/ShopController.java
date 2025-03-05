package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Api(tags = "店铺相关接口")
@Slf4j
@RequestMapping("/admin/shop")
@RestController("adminShopController")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    private final String SHOP_STATUS = "SHOP_STATUS";

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result changeStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态：{}", status);
        // 1.调用redisTemplate，将店铺状态存入到redis中
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(SHOP_STATUS, status);

        // 2.返回成功结果
        return Result.success();
    }

    /**
     * 查询店铺营业状态
     * @return
     */
    @ApiOperation("查询店铺营业状态")
    @GetMapping("/status")
    public Result getStatus(){
        //调用redisTemplate，从redis中查询指定的key，获取营业状态
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("查询店铺营业状态：{}", status);
        //返回结果
        return Result.success(status);
    }
}
