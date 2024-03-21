package com.peregi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.entity.OrderDetail;
import com.peregi.mapper.OrderDetailMapper;
import com.peregi.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}