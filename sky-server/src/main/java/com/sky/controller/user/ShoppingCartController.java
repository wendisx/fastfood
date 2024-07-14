package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "购物车相关接口")
public class ShoppingCartController {
    //注入bean
    @Autowired
    private ShoppingCartService shoppingCartService;

    //具体业务处理
    /**
     * 添加物品至购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加物品")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加物品到购物车：{}",shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 展示购物车数据
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "查看购物车")
    public Result<List<ShoppingCart>> show(){
        List<ShoppingCart> shoppingCarts=shoppingCartService.show();
        log.info("查看购物车商品信息：{}",shoppingCarts);
        return Result.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空购物车")
    public Result clean(){
        log.info("删除所有购物车物品");
        shoppingCartService.deleteAll();
        return Result.success();
    }

    /**
     * 删除购物车
     * 一次
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation(value = "修改购物车")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删减菜品信息：{}",shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
