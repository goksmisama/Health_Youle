package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youle.dao.CheckItemDao;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.CheckItem;
import com.youle.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CheckItemService.class)
@Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
public class checkItemServiceimpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;


    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<CheckItem> page=checkItemDao.findPage(queryPageBean.getQueryString());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteById(Integer id) {
        //查询检查项和检查组中间表   检查项是否和检查组有关联 如果有不能删除检查项
        long count=checkItemDao.findCountByCheckItemId(id);
        if (count>0){
            throw new RuntimeException("当前检查项和检查组有关联不能删除");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }

    @Override
    public void deleteByIds(Integer[] ids) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Integer id : ids) {
            long count=checkItemDao.findCountByCheckItemId(id);
            if (count>0){
                throw new RuntimeException(id+"当前检查项和检查组有关联不能删除已经从数组移除");
            }else {
                list.add(id);
            }
        }
        for (Integer integer : list) {
            System.out.println(integer);
        }
        checkItemDao.deleteByIds(list);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
