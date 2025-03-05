package com.sky;

import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.utils.AliOssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.UUID;

/**
 * 功能：自动更新所有菜品的图片
 * 前置要求：需要完成修改菜品的接口，且参数接收为类型为dish对象
 * 需要自己配置的地方：
 *              1、PICTURE_PATH修改为自己的图片文件夹路径（第29行）
 *              2、将“修改菜品”的接口名修改为自己的（第47行）
 */
@SpringBootTest
public class UploadImageTest {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private DishMapper dishMapper;

    // 替换为你的图片文件夹路径（使用正斜杠，最后末尾别忘记再加一个斜杠）
    private static final String PICTURE_PATH = "D:\\Work\\java147\\项目一苍穹外卖\\day03\\资料\\图片资源\\";
    private static final Long DISH_ID_START_WITH = 46l;

    @Test
    public void uploadImage() {
        int j = 1;
        for (Long i = DISH_ID_START_WITH; i < DISH_ID_START_WITH + 24; i++, j++) {
            //拿到图片每个的bytes[]
            byte[] bytes = getImageByte(PICTURE_PATH + j + ".png");
            //生成UUID图片名
            String name = UUID.randomUUID() + ".png";
            //图片上传到阿里OSS，拿到图片的链接
            String url = aliOssUtil.upload(bytes, name);
            //调用菜品修改方法，存入数据库，从46开始
            Dish dish = Dish.builder()
                    .id(i)
                    .image(url)
                    .build();
            dishMapper.update(dish);//这里要换成自己修改菜品的方法名
        }
    }

    private byte[] getImageByte(String pathName) {
        File file = new File(pathName);
        byte[] imageData = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            imageData = new byte[(int) file.length()];
            fis.read(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageData;
    }
}
