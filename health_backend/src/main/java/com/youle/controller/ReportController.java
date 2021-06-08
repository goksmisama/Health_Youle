package com.youle.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youle.constant.MessageConstant;
import com.youle.entity.Result;
import com.youle.service.MemberService;
import com.youle.service.ReportService;
import com.youle.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/report")
@RestController
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    @RequestMapping("/getMemberReport.do")
    public Result getMemberReport(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);//获取当前日期之前的12个月的日期
        List<String> list = new ArrayList();
        for (int i = 0; i <12 ; i++) {
            calendar.add(Calendar.MONTH,1);
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("months",list);

        List<Integer> memberCount =memberService.findMemberCountByMonth(list);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    @RequestMapping("/getSetmealReport.do")
    public Result getSetmealReport(){
        List<Map<String,Object>> list = setmealService.findSetmealCount();
        Map<String,Object> map = new HashMap<>();
        map.put("setmealCount",list);

        List<String> setmealNames = new ArrayList<>();
        for (Map<String, Object> objectMap : list) {
            String name = (String) objectMap.get("name");
            setmealNames.add(name);
        }
        map.put("setmealNames",setmealNames);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
    }

    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String, Object> map = reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @RequestMapping("/exportBusinessReport.do")
    public Result exportBusinessReport(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            //获取报表数据
            Map<String, Object> data = reportService.getBusinessReportData();
            //取出结果数据，准备写入到excel中
            String reportDate = (String) data.get("reportDate");//日期
            Integer todayNewMember = (Integer) data.get("todayNewMember");//新增会员数
            Integer totalMember = (Integer) data.get("totalMember");//总会员数
            Integer thisWeekNewMember = (Integer) data.get("thisWeekNewMember");//本周新增会员数
            Integer thisMonthNewMember = (Integer) data.get("thisMonthNewMember");//本月新增会员数
            Integer todayOrderNumber = (Integer) data.get("todayOrderNumber");//今日预约数
            Integer thisWeekOrderNumber = (Integer) data.get("thisWeekOrderNumber");//本周预约数
            Integer thisMonthOrderNumber = (Integer) data.get("thisMonthOrderNumber");//本月预约数
            Integer todayVisitsNumber = (Integer) data.get("todayVisitsNumber");//今日到诊数
            Integer thisWeekVisitsNumber = (Integer) data.get("thisWeekVisitsNumber");//本周到诊数
            Integer thisMonthVisitsNumber = (Integer) data.get("thisMonthVisitsNumber");//本月到诊数
            List<Map> hotSetmeal = (List<Map>) data.get("hotSetmeal");//热门套餐
            //获取excel模板文件路径
            String temlateRealPath = request.getSession().getServletContext().getRealPath("template")  + File.separator + "report_template.xlsx";
            //读取模板文件创建excel对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));
            //获取excel的第一页
            XSSFSheet sheet =workbook.getSheetAt(0);
            //获取第二行 0 1 2  getrow时获取行 参数是第几行的意思
            XSSFRow row = sheet.getRow(2);
            //往第二行 第五列填充数据
            row.getCell(5).setCellValue(reportDate);//第二行第五列  日期

            row = sheet.getRow(4); //获取第四行
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5); //获取第五行
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7); //获取第七行
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8); //获取第八行
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);  //获取第九行
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            //找到热门套餐的行数
            int rowNum =12;
            //遍历热门套餐 循环向excel中写入数据
            for (Map map : hotSetmeal) {
                String name = (String) map.get("name");//套餐名
                Long setmeal_count = (Long) map.get("setmeal_count");//热门套餐预约数量
                BigDecimal proportion = (BigDecimal) map.get("proportion");//热门套餐占比 BigDecimal是math包中的类数学计算
                row = sheet.getRow(rowNum++);//获取行
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            //通过 输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//告知浏览器数据类型
            response.setHeader("content-Disposition","attachment;filename=report.xlsx");//第一个参数是设置附件下载的 ，第二个是文件名字
            workbook.write(out);

            out.flush();
            out.close();
            workbook.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @RequestMapping("/exportBusinessReport4PDF.do")
    public Result exportBusinessReport4PDF(HttpServletRequest request,HttpServletResponse response){
        try {
            Map<String, Object> result = reportService.getBusinessReportData();
            //取出返回结果数据，准备将报表数据写入到PDF文件中
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");
            //动态获取模板文件绝对磁盘路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";
            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            //填充数据---使用JavaBean数据源方式填充
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath,result, new JRBeanCollectionDataSource(hotSetmeal));
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf"); response.setHeader("content-Disposition", "attachment;filename=report.pdf");
            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
}
