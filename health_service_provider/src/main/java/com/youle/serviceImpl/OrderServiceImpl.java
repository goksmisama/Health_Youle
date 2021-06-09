package com.youle.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youle.constant.MessageConstant;
import com.youle.dao.MemberDao;
import com.youle.dao.OrderDao;
import com.youle.dao.OrderSettingDao;
import com.youle.entity.Result;
import com.youle.pojo.Member;
import com.youle.pojo.Order;
import com.youle.pojo.OrderSetting;
import com.youle.service.OrderService;
import com.youle.utils.DateUtils;
import com.youle.utils.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DistributedLock distributedLock;

    @Override
    public Result order(Map map) throws Exception {
        //检查当前日期是否进行了预约设置 能否进行预约
        distributedLock.getLock();
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
        if (orderSetting == null){
            distributedLock.releaseLock();
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //检查预约日期是否预约满了
        int number = orderSetting.getNumber();//可预约人数
        int reservations = orderSetting.getReservations();//已经预约的人数
        if (reservations >=number){
            distributedLock.releaseLock();
            //预约已满，不能预约了
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //检查当前用户是否是会员，根据手机号判断
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        //防止重复预约
        if (member != null){
            Integer memberId = member.getId();
            int setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId,date,null,null,setmealId);
            List<Order> list = orderDao.findByCondition(order);
            if (list != null && list.size()>0){
                distributedLock.releaseLock();
                //已经完成了预约了，不能重复预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }
        //可以预约 设置预约人数加一
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        if (member == null){
            //当前用户不是会员，添加至会员
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);
        }
        //保存预约信息到预约表
        Order order = new Order(member.getId(),date, (String) map.get("orderType"),Order.ORDERSTATUS_NO,Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        distributedLock.releaseLock();
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if (map != null){
            //设置日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}
