package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加物品至购物车
     * @param shoppingCartDTO
     * @return
     */
    public void add(ShoppingCartDTO shoppingCartDTO);
    /**
     * 展示购物车数据
     * @return
     */
    List<ShoppingCart> show();

    /**
     * 清空购物车
     */
    void deleteAll();

    /**
     * 删除购物车
     * 一次
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
