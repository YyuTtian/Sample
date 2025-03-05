package com.sky;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * POI读写excel测试
 */
public class TestPOI {
    /**
     * Excel的组成：
     *     Excel文件--->工作簿（文件）对象
     *     Excel里面可以有很多个Sheet表 ----> 工作表对象 Sheet
     *     Sheet表里有很多行 ---> 行对象 Row
     *     行里面有很多个单元格 ---> 单元格对象 Cell
     *     单元格里存的就是数据了
     */

    // 需求：通过POI写入数据到指定的excel文件中
    @Test
    public void testWrite() throws Exception {
        // 1.通过POI创建工作簿（文件）对象-- excel对象
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 2.通过 XSSFWorkbook 创建 Sheet对象
        XSSFSheet sheet = workbook.createSheet("itcast");

        // 3.通过 XSSFSheet 创建行对象 Row ,注意：下标从0开始，创建第一行就写0
        XSSFRow row = sheet.createRow(0);

        // 4.通过 XSSFRow 创建单元格, 并往格子中填充数据
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("爱好");

        // 第二行
        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("张三");
        row1.createCell(1).setCellValue("篮球");

        // 第三行
        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("李四");
        row2.createCell(1).setCellValue("足球");

        // 5.将excel文件写出到指定的文件中
        FileOutputStream fos = new FileOutputStream("D:\\demo.xlsx");
        workbook.write(fos);
        System.out.println("写入成功！！");

        // 6.释放资源
        fos.close();
        workbook.close();
    }

    // 需求：通过POI写入数据到指定的excel文件中---基于模板写入
    @Test
    public void testWrite2() throws Exception {
        // 1.基于Excel模板文件--》通过POI创建工作簿（文件）对象-- excel对象
        FileInputStream fis = new FileInputStream("D:\\demo.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        // 2.通过 XSSFWorkbook 获取 Sheet对象
        XSSFSheet sheet = workbook.getSheet("itcast");

        // 3.通过 XSSFSheet 获取行对象 Row ,注意：下标从0开始，创建第一行就写0
        XSSFRow row = sheet.getRow(0);

        // 4.通过 XSSFRow 创建单元格, 并往格子中填充数据
        row.getCell(0).setCellValue("姓名");
        row.getCell(1).setCellValue("爱好");

        // 第二行
        XSSFRow row1 = sheet.getRow(1);
        row1.getCell(0).setCellValue("张三");
        row1.getCell(1).setCellValue("篮球");

        // 第三行
        XSSFRow row2 = sheet.getRow(2);
        row2.getCell(0).setCellValue("李四");
        row2.getCell(1).setCellValue("足球");

        // 5.将excel文件写出到指定的文件中
        FileOutputStream fos = new FileOutputStream("D:\\demo1.xlsx");
        workbook.write(fos);
        System.out.println("写入成功！！");

        // 6.释放资源
        fos.close();
        workbook.close();
    }

    // 需求：通过POI读磁盘中指定的excel文件到Java内存中，并输出到控制台
    @Test
    public void testRead() throws Exception {
        // 1.基于Excel模板文件--》通过POI创建工作簿（文件）对象-- excel对象
        FileInputStream fis = new FileInputStream("D:\\demo1.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        // 2.通过 XSSFWorkbook 获取 Sheet对象
        XSSFSheet sheet = workbook.getSheet("itcast");

        // 3.通过 XSSFSheet 获取行对象 Row ,注意：下标从0开始，创建第一行就写0
        // XSSFRow row = sheet.getRow(0);

        // 4.通过 XSSFRow 创建单元格, 并往格子中填充数据
        // String name = row.getCell(0).getStringCellValue();
        // String hobby = row.getCell(1).getStringCellValue();
        //
        // // 5.将获取到的内容输出到控制台
        // System.out.println(name+"|"+hobby);

        // // 读第二行
        // row = sheet.getRow(1);
        // name = row.getCell(0).getStringCellValue();
        // hobby = row.getCell(1).getStringCellValue();
        // System.out.println(name+"|"+hobby);
        // // 读第三行
        // row = sheet.getRow(2);
        // name = row.getCell(0).getStringCellValue();
        // hobby = row.getCell(1).getStringCellValue();
        // System.out.println(name+"|"+hobby);

        // 循环读取表中的每一行数据
        int firstRowNum = sheet.getFirstRowNum();   //?
        int lastRowNum = sheet.getLastRowNum();     //?
        System.out.println("firstRowNum = " + firstRowNum);
        System.out.println("lastRowNum = " + lastRowNum);
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            String name = row.getCell(0).getStringCellValue();
            String hobby = row.getCell(1).getStringCellValue();
            System.out.println(name+"|"+hobby);
        }

        // 6.释放资源
        fis.close();
        workbook.close();
    }
}
