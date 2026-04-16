package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.add(setmeal);
        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish1 : setmealDish) {
            setmealDish1.setSetmealId(setmeal.getId());
            setmealDishMapper.addSetmealDish(setmealDish1);

        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> pageResult = setmealMapper.pageQuery(setmealPageQueryDTO);
        Long total = (Long) pageResult.getTotal();
        List<SetmealVO> records = pageResult.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void delete(List<Long> ids) {
        setmealMapper.deleteBatch(ids);
    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setmealMapper.getById(id);
        return setmealVO;
    }

//    @Override
//    public void update(SetmealDTO setmealDTO) {
//        Setmeal setmeal = new Setmeal();
//        BeanUtils.copyProperties(setmealDTO, setmeal);
//        setmealMapper.update(setmeal);
//        setmealDishMapper.deleteBySetmealId((setmealDTO.getId()));
//        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();
//        for (SetmealDish setmealDish1 : setmealDish) {
//            setmealDish1.setSetmealId(setmealDTO.getId());
//            setmealDishMapper.addSetmealDish(setmealDish1);
//        }
//    }

    @Transactional(rollbackFor = Exception.class) // 事务管理：所有异常都回滚
    public void update(SetmealDTO setmealDTO) {
        // 1. 关键参数非空校验

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // 2. 转换并更新套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // 3. 删除旧的套餐菜品关联
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        // 4. 批量插入新的套餐菜品关联（非空判断避免无效插入）
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            // 批量设置套餐ID，使用Stream简化代码
            setmealDishes.forEach(dish -> dish.setSetmealId(setmealDTO.getId()));
            // 批量插入（替代循环单条插入）
            setmealDishMapper.batchInsert(setmealDishes);
        }
    }

//    @Override
//    public void startOrStop(Integer status, Long id) {
//        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
//        if(status == StatusConstant.ENABLE){
//            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
//            List<Dish> dishList = dishMapper.getBySetmealId(id);
//            if(dishList != null && dishList.size() > 0){
//                dishList.forEach(dish -> {
//                    if(StatusConstant.DISABLE == dish.getStatus()){
//                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
//                    }
//                });
//            }
//        }
//
//        Setmeal setmeal = Setmeal.builder()
//                .id(id)
//                .status(status)
//                .build();
//        setmealMapper.update(setmeal);
//    }

    public void startOrStop(Integer status, Long id) {
        // 1. 启售套餐时，校验套餐内是否有停售菜品
        if (StatusConstant.ENABLE.equals(status)) {
            // 查询套餐内的菜品状态（仅查询id和status，优化性能）
            List<Dish> dishList = dishMapper.getBySetmealId(id);

            // 若套餐内有菜品，遍历校验状态
            if (!CollectionUtils.isEmpty(dishList)) {
                for (Dish dish : dishList) {
                    // 空指针防护：跳过null的菜品对象
                    if (dish == null) {
                        continue;
                    }
                    // 发现停售菜品，立即抛出异常，终止逻辑
                    if (StatusConstant.DISABLE.equals(dish.getStatus())) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        // 2. 构建套餐对象，仅更新状态和ID
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        // 3. 更新套餐状态
        setmealMapper.update(setmeal);
    }

    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
