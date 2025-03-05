package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "店铺相关接口")
@Slf4j
@RequestMapping("/user/shop")
@RestController("userShopController")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    private final String SHOP_STATUS = "SHOP_STATUS";

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
