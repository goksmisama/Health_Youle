package com.youle.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class test01 {
    public static void main(String[] args) throws IOException, TemplateException {
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在的目录
        configuration.setDirectoryForTemplateLoading(new File("D:\\develop\\idea_Code\\health_parent\\freemarkerdemo\\src\\main\\resources"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //获取模板对象
        Template template =configuration.getTemplate("test.ftl");
        //创建一个list
        List goodslist = new ArrayList();
        Map goodsmap1 = new HashMap();
        goodsmap1.put("name","苹果");
        goodsmap1.put("price","123");
        Map goodsmap2 = new HashMap();
        goodsmap2.put("name","bl");
        goodsmap2.put("price","123");
        Map goodsmap3 = new HashMap();
        goodsmap3.put("name","mm");
        goodsmap3.put("price","123");
        goodslist.add(goodsmap1);
        goodslist.add(goodsmap2);
        goodslist.add(goodsmap3);
        //创建数据模型
        Map map = new HashMap();
        map.put("name","123");
        map.put("message","456");
        map.put("success",true);
        map.put("goodslist",goodslist);
        map.put("today",new Date());
        map.put("point",10111111);
        //创建一个输出对象
        Writer out = new FileWriter("D:\\test.html");
        //输出
        template.process(map,out);
        //关闭流
        out.close();
    }
}
