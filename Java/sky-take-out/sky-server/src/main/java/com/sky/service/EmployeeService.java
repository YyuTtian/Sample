package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param dto
     */
    void addEmp(EmployeeDTO dto);

    /**
     * 员工分页查询
     * @param dto
     * @return
     */
    PageResult page(EmployeePageQueryDTO dto);

    /**
     * 启用禁用员工
     * @param status
     * @param id
     */
    void enableOrDisable(Integer status, Long id);

    /**
     * 回显员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工
     * @param dto
     */
    void update(EmployeeDTO dto);

}
