package com.peregi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.entity.ShoppingCart;
import com.peregi.mapper.ShoppingCartMapper;
import com.peregi.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
