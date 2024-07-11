package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;
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

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //查询菜品是否正在起售中
        for (Long id : ids) {
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //菜品是否被套餐关联
        List<Long> setMealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setMealIds!=null && setMealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //可删除的菜品
       /* for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除对应口味表
            dishFlavorMapper.deleteByDishId(id);
        }*/
        //代码优化：减少sql语句的生成
        //删除对应菜品表
        dishMapper.deleteByIds(ids);
        //删除对应口味表
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 修改菜品数据：根据id回显菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //查询菜品数据
        Dish dish = dishMapper.getById(id);
        //查询对应口味
        List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(id);
        //拷贝属性
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        //修改菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //删除口味数据
        dishFlavorMapper.deleteByDishId(dish.getId());
        //修改口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&& !flavors.isEmpty()){
            //设置菜品口味联系
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            //批量插入口味数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品状态修改
     * @param status
     * @param id
     */
    @Override
    public void statusChange(Integer status, Long id) {
        Dish dish=new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }
}
