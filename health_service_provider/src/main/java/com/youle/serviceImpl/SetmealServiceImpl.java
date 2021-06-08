package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youle.constant.RedisConstant;
import com.youle.dao.SetmealDao;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.Setmeal;
import com.youle.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private SetmealDao setmealDao;

    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String outputpath;

    @Override
    public void add(Integer[] ids, Setmeal setmeal) {
        //添加套餐
        setmealDao.add(setmeal);
        //把检查组id和套餐关联
        if (ids != null && ids.length > 0) {
            for (Integer id : ids) {
                Map<String, Integer> map = new HashMap<>();
                map.put("setmeal_id", setmeal.getId());
                map.put("checkgroup_id", id);
                setmealDao.addSetmealidAndCheckGroupId(map);
            }
        }
        //将实际添加到数据库的图片名称存入redis中
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        Page<Setmeal> page = setmealDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    @Override
    public List<Integer> findCheckGroupIdBySetmealId(Integer id) {
        return setmealDao.findCheckGroupIdBySetmealId(id);
    }

    @Override
    public void update(Integer[] ids, Setmeal setmeal) {
        //需要之前的图片名称 先查询数据库拿到图片名称
        Setmeal oldsetmeal = setmealDao.findById(setmeal.getId());
        String oldimg = oldsetmeal.getImg();
        //修改套餐数据
        setmealDao.update(setmeal);
        //删除该套餐与检查组的关联 操作中间表
        setmealDao.deleteSetmealIdAndCheckgroupId(setmeal.getId());
        //把最新的关联存入中间表
        if (ids != null && ids.length > 0) {
            for (Integer id : ids) {
                Map<String, Integer> map = new HashMap<>();
                map.put("setmeal_id", setmeal.getId());
                map.put("checkgroup_id", id);
                setmealDao.addSetmealidAndCheckGroupId(map);
            }
        }
        if (setmeal.getImg() != null && setmeal.getImg().length() > 0) {
            //删除REDIS中setmealPicDbResources的图片数据
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, oldimg);
            //将实际添加到数据库的图片名称存入redis中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());
        }
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    //生成静态页面
    public void generateMobileStaticHtml() {
        //准备模板文件中所需的数据
        List<Setmeal> setmealList = this.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面（多个）
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("setmealList", setmealList);
        this.generateHtml("mobile_setmeal.ftl", "m_setmeal.html", dataMap);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("setmeal", this.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl", "setmeal_detail_" + setmeal.getId() + ".html", dataMap);
        }
    }

    public void generateHtml(String templateName, String htmlPageName, Map<String, Object> dataMap) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out = null;
        try {
            // 加载模版文件
            Template template = configuration.getTemplate(templateName);
            // 生成数据
            File docFile = new File(outputpath + "\\" + htmlPageName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // 输出文件
            template.process(dataMap, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
