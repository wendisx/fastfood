package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理接口")
@Slf4j
public class DishController {
    //注入service
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> dishPageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageResult=dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        //打印日志
        log.info("批量删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改菜品数据：根据id回显菜品
     * 点击修改按钮后显示当前菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "回显对应id菜品数据")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("修改菜品数据：根据id回显菜品,{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 菜品状态更改
     * 起售，停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品状态修改")
    public Result statusChange(@PathVariable("status") Integer status,Long id){
        log.info("菜品状态更改：{},{}",status,id);
        dishService.statusChange(status,id);
        return Result.success();
    }
    /**
     * 修改菜品信息
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品信息")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息：{}",dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }
}
