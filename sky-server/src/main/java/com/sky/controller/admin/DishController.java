package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);

    }
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除菜品", ids);
        dishService.deleteBatch(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品信息");
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("编辑菜品信息", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.listDishByCategoryId(categoryId);
        log.info("查询菜品列表结果", list);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用或禁用菜品信息，id：{}", id);
        dishService.startOrStop(status, id);
        cleanCache("dish_*");
        return Result.success();
    }

}
