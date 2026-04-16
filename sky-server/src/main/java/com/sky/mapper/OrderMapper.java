package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{checkOutTime} where number = #{orderNumber}")
    void updateStatus(String orderNumber, Integer orderPaidStatus, Integer orderStatus, LocalDateTime checkOutTime);

    Page<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(id) from orders where status = #{status}")
    Integer statistics(Integer status);

    OrderVO details(Long id);

    Page<OrderVO> serchhistory(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}


