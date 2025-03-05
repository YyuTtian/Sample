package com.example.demo;

import com.example.demo.config.TestInfo;
import com.example.demo.entity.TbDept;
import com.example.demo.entity.TbHobby;
import com.example.demo.entity.TbUser;
import com.example.demo.service.impl.TbDeptServiceImpl;
import com.example.demo.service.impl.TbHobbyServiceImpl;
import com.example.demo.service.impl.TbUserServiceImpl;
import com.example.demo.vo.UserVO;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    TbUserServiceImpl tbUserServiceImpl;

    @Test
    void contextLoads() {

        for (int i = 1; i <= 1; i++) {

            TbUser tbUser = new TbUser();
            tbUser.setDeptId(i);
            tbUser.setUsername("用户" + i);
            tbUser.setPassword("123456");
            tbUser.setHobbyId(i);
            tbUserServiceImpl.save(tbUser);
            System.out.println("新保存的id=" + tbUser.getId());
        }

//        List<UserVO> filter = tbUserServiceImpl.filter("1", "1", "1", 1, 1);
//        filter.forEach(System.out::println);

    }

    // 获取application.yml中配置的my-data.db-name的值
    @Value("${my-data.db-name}")
    private String dbName;

    /**
     * 在resources下新建1.xml 内容如下
     * <?xml version="1.0" encoding="UTF-8"?>
     * <emp>
     *     <name>Tom</name>
     *     <age>18</age>
     * </emp>
     */

    @Autowired
    private TestInfo testInfo;

    @Autowired
    private SAXReader saxReader;

    @Test
    void test() throws DocumentException {
        Document document = saxReader.read(this.getClass().getClassLoader().getResource("1.xml"));
        Element rootElement = document.getRootElement();
        String name = rootElement.element("name").getText();
        String age = rootElement.element("age").getText();
        System.out.println("使用第三方bean " + name + " : " + age);
    }

}
