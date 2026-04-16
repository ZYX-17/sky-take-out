package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单查询条件：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);

    }
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计订单");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) throws Exception{

        log.info("订单详情：{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersDTO ordersDTO) {
        log.info("订单确认");
        orderService.confirm(ordersDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        log.info("订单派送：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        log.info("订单完成：{}", id);
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("订单拒绝：{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("订单取消：{}", ordersCancelDTO);
        orderService.cancelorders(ordersCancelDTO);
        return Result.success();
    }


}
