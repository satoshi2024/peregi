package com.peregi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peregi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/16 19:53
 * @Version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
