package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youle.dao.PermissionDao;
import com.youle.dao.RoleDao;
import com.youle.dao.UserDao;
import com.youle.pojo.Permission;
import com.youle.pojo.Role;
import com.youle.pojo.User;
import com.youle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public User findByUserName(String username) {
        User user =userDao.findByUserName(username);
        if (user == null){
            //如果没有这个用户直接返回Null
            return null;
        }
        Integer userId = user.getId();
        //根据userid查询对应胡角色
        Set<Role> roles=roleDao.findByUserId(userId);
        if (roles!=null && roles.size()>0){
            for (Role role : roles) {
                Integer roleId = role.getId();
                //根据角色id查询对应的权限
                Set<Permission>permissions=permissionDao.findByRoleId(roleId);
                if (permissions !=null && permissions.size()>0){
                    //有权限 赋值
                    role.setPermissions(permissions);
                }
            }
            user.setRoles(roles);
        }
        return user;
    }
}

