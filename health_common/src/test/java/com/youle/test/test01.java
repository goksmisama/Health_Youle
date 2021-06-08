package com.youle.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class test01 {

    //@Test
    public void test001() throws IOException {
        //加载指定的文件 创建一个excel对象
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream("D:\\hello.xlsx"));
        //读取Excel文件中第一个sheet标签页
        XSSFSheet sheetAt = excel.getSheetAt(0);
        //遍历标签 获取每一行
        for (Row row : sheetAt) {
            for (Cell cell : row) {
                System.out.println(cell.getStringCellValue());
            }
        }
        excel.close();
    }

    //@Test
    public void test02() throws IOException {
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream("D:\\hello.xlsx"));
        XSSFSheet sheetAt = excel.getSheetAt(0);
        int lastRowNum = sheetAt.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            //根据行号获取每一行
            XSSFRow row = sheetAt.getRow(i);
            //获取当前行最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
                XSSFCell cell = row.getCell(j);//根据单元格索引获取单元格对象
                if (cell.getCellType() == cell.CELL_TYPE_NUMERIC){
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    System.out.println(cell.getStringCellValue());
                }else {
                    System.out.println(cell.getStringCellValue());
                }
            }
        }
        excel.close();
    }

    //@Test
    public void test03() throws IOException {
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream("D:\\hello.xlsx"));
        XSSFSheet sheetAt = excel.getSheetAt(0);
        int lastRowNum = sheetAt.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            //根据行号获取每一行
            XSSFRow row = sheetAt.getRow(i);
            //获取当前行最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j <lastCellNum ; j++) {
                XSSFCell cell = row.getCell(j);//获取单元格
                if(cell.getCellType() == cell.CELL_TYPE_NUMERIC){
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    System.out.println(cell.getStringCellValue()+"===");
                }else{
                    System.out.println(cell.getStringCellValue()+"***");
                }
            }
        }
    }

    //使用Apache的poi向Excel中写入数据 并且通过输出流将创建的Excel文件保存在本地磁盘
    //@Test
    public void test4() throws IOException {
        //在内存中创建一个Excel工作簿对象
        XSSFWorkbook excel = new XSSFWorkbook();
        //创建一个工作表对象
        XSSFSheet sheet = excel.createSheet("youle");
        //在工作表中创建行对象
        XSSFRow title1 = sheet.createRow(0);
        //在行中创建单元格对象
        title1.createCell(0).setCellValue("姓名");
        title1.createCell(1).setCellValue("地址");
        title1.createCell(2).setCellValue("年龄");

        XSSFRow title2 = sheet.createRow(1);
        //在行中创建单元格对象
        title2.createCell(0).setCellValue("张二狗");
        title2.createCell(1).setCellValue("成都");
        title2.createCell(2).setCellValue("18");

        //创建一个输出流 通过输出流将内存中Excel文件写到磁盘
        FileOutputStream fos = new FileOutputStream(new File("d:\\hello.xlsx"));
        excel.write(fos);
        fos.flush();
        fos.close();
        excel.close();
    }
}
