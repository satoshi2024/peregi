package com.peregi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.entity.Employee;
import com.peregi.mapper.EmployeeMapper;
import com.peregi.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/16 19:57
 * @Version 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>implements EmployeeService {
}
