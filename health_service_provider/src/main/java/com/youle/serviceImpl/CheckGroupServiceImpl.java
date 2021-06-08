package com.youle.serviceImpl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youle.dao.CheckGroupDao;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.CheckGroup;
import com.youle.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<CheckGroup>page=checkGroupDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Integer[] ids, CheckGroup checkGroup) {
        //添加检查组
        checkGroupDao.add(checkGroup);
        //中间表添加检查组对应的检查项
        if (ids!=null && ids.length>0){
            for (Integer id : ids) {
                Map<String,Integer> map = new HashMap();
                map.put("checkgroup_id",checkGroup.getId());
                map.put("checkitem_id",id);
                checkGroupDao.addCheckgroupAndCheckItem(map);
            }
        }
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public void update(Integer[] ids, CheckGroup checkGroup) {
        //修改检查项的信息
        checkGroupDao.update(checkGroup);
        //删除检查组关联检查项的中间表信息
        checkGroupDao.deleteCheckgroupAndCheckItem(checkGroup.getId());
        //重新添加中间表信息
        if (ids!=null && ids.length>0){
            for (Integer id : ids) {
                Map<String,Integer> map = new HashMap();
                map.put("checkgroup_id",checkGroup.getId());
                map.put("checkitem_id",id);
                checkGroupDao.addCheckgroupAndCheckItem(map);
            }
        }
    }

    @Override
    public void delete(Integer id) {
        //删除中间表检查组与检查项之间的关联  必须先删除中间表的 因为有外键 不能直接先删除检查组表
        checkGroupDao.deleteCheckgroupAndCheckItem(id);
        //删除检查组
        checkGroupDao.deleteById(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }
}
