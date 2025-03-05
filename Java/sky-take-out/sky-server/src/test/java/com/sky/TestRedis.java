package com.sky;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//如果用了webSocket，测试环境需要声明webEnvironment，否则报错
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRedis {
    @Autowired
    private RedisTemplate redisTemplate;

    // 测试String类型操作
    @Test
    public void testString(){
        // 1.获取ValueOperations对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 2.通过ValueOperations对象，操作string类型数据
        valueOperations.set("name", "kunkun");      //对应的是set命令
        Object name = valueOperations.get("name");  //对应的是get命令
        System.out.println("name = " + name);

        // 需求：存储一个验证码，并且设置过期时间为10s
        valueOperations.set("code", "1234", 10, TimeUnit.SECONDS);
        Object code = valueOperations.get("code");
        System.out.println("code = " + code);
    }

    // 测试Hash类型操作
    @Test
    public void testHash(){
        // 1.获取HashOperations对象
        HashOperations hashOperations = redisTemplate.opsForHash();

        // 2.通过HashOperations对象，操作hash类型数据
        hashOperations.put("user", "name", "zhangsan");
        hashOperations.put("user", "age", 18);
        hashOperations.put("user", "gender", "男");

        // 获取hash中单个属性值
        Object name = hashOperations.get("user", "name");
        System.out.println("name = " + name);

        // 获取hash中所有的属性
        Set keys = hashOperations.keys("user");
        System.out.println("keys = " + keys);
        // 获取hash中所有的值
        List values = hashOperations.values("user");
        System.out.println("values = " + values);

        // 获取整个user对象
        Map user = hashOperations.entries("user");
        System.out.println("user = " + user);
    }

    // 测试ZSet类型操作
    @Test
    public void testZSet(){
        // 1.获取HashOperations对象
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        // 2.通过zSetOperations对象，操作ZSet类型数据
        zSetOperations.add("zset01", "zhangsan", 88);
        zSetOperations.add("zset01", "lisi", 79);
        zSetOperations.add("zset01", "wangwu", 99.9);

        Set zset01 = zSetOperations.range("zset01", 0, -1);
        System.out.println("zset01 = " + zset01);

        Set<DefaultTypedTuple> zset011 = zSetOperations.rangeWithScores("zset01", 0, -1);
        System.out.println("zset011 = " + zset011);
        for (DefaultTypedTuple tuple : zset011) {
            System.out.println(tuple.getValue() + "--" + tuple.getScore());
        }
    }


    // 测试Set类型操作
    @Test
    public void testSet(){
        // 1.获取SetOperations对象
        SetOperations setOperations = redisTemplate.opsForSet();

        // 2.通过SetOperations对象，操作Set类型数据
        setOperations.add("set01", "java", "go", "python", "C");
        setOperations.add("set02", "java", "go", "sing", "dance");
        Set set01 = setOperations.members("set01");
        Set set02 = setOperations.members("set02");
        System.out.println("set01 = " + set01);
        System.out.println("set02 = " + set02);

        // 求交集
        Set intersect = setOperations.intersect("set01", "set02");
        System.out.println("交集："+intersect);
        // 求并集
        Set union = setOperations.union("set01", "set02");
        System.out.println("并集："+union);
        // 求差集，set01 - set02
        Set difference = setOperations.difference("set01", "set02");
        System.out.println("差集："+difference);
    }

    // 测试List类型操作
    @Test
    public void testList(){
        // 1.获取ListOperations对象
        ListOperations listOperations = redisTemplate.opsForList();

        // 2.通过ListOperations对象，操作List类型数据
        listOperations.leftPushAll("list01", "aaa", "bbb", "ccc");
        listOperations.rightPushAll("list01", "111", "222", "333");

        List list01 = listOperations.range("list01", 0, -1);
        System.out.println("list01 = " + list01);

        System.out.println("=====================================");
        Object object = listOperations.rightPop("list01");
        System.out.println("删除的元素 = " + object);
        list01 = listOperations.range("list01", 0, -1);
        System.out.println("list01 = " + list01);

        // 理解：从左边开始删除， 删除两个
        // System.out.println("=====================================");
        // List list011 = listOperations.leftPop("list01", 2L);
        // System.out.println(list011);
        // list01 = listOperations.range("list01", 0, -1);
        // System.out.println("list01 = " + list01);
    }

}
