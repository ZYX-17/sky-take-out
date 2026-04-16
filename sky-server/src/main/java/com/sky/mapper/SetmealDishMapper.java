package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) values (#{setmealId}, #{dishId}, #{name}, #{price}, #{copies})")
    void addSetmealDish(SetmealDish setmealDish1);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    void batchInsert(List<SetmealDish> setmealDishes);

    //List<SetmealDish> getSetmealDishBySetmealId(Long id);
}
