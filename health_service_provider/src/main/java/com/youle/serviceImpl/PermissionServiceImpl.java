package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youle.dao.PermissionDao;
import com.youle.entity.PageResult;
import com.youle.entity.QueryPageBean;
import com.youle.pojo.Permission;
import com.youle.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = PermissionService.class)
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public PageResult findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        String queryString = queryPageBean.getQueryString();
        Page<Permission> page = null;
        if (queryString != null && queryString.length() > 0 && !"".equals(queryString)) {
            page = permissionDao.selectByCondition("%" + queryString + "%");
        } else {
            page = permissionDao.selectByCondition("");
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    public void add(Permission permission) {
        permissionDao.add(permission);
    }

    public void edit(Permission permission) {
        permissionDao.edit(permission);
    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Permission findById(Integer id) {
        return permissionDao.findById(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List findAll() {
        return permissionDao.findAll();
    }

    public void delete(Integer[] ids) {
        List<Integer> list = Arrays.asList(ids);
        permissionDao.delete(list);
    }
}
