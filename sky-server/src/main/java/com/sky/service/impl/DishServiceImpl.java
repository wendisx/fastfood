package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    //注入mapper
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Transactional
    public void addDishWithFlavor(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //调用Mapper中的插入菜品
        dishMapper.insert(dish);
        //获取存入菜品的主键值id作为逻辑上的外键dish_id
        Long dishId = dish.getId();
        //插入口味数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors!=null&& !dishFlavors.isEmpty()){
            //设置菜品口味联系
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //批量插入口味数据
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page= dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

}
