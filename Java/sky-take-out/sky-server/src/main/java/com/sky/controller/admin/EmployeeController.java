package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工相关接口")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param dto
     * @return
     */
    @ApiOperation("新增员工")
    @PostMapping
    public Result addEmp(@RequestBody EmployeeDTO dto){
        log.info("EmployeeController:线程id={}", Thread.currentThread().getId());
        log.info("新增员工：{}", dto);
        employeeService.addEmp(dto);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param dto
     * @return
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO dto){
        log.info("员工分页查询：{}", dto);
        PageResult pageResult = employeeService.page(dto);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用员工")
    @PostMapping("/status/{status}")
    public Result enableOrDisable(@PathVariable Integer status, Long id){
        log.info("启用禁用员工：status={}, id={}", status, id);
        employeeService.enableOrDisable(status, id);
        return Result.success();
    }

    /**
     * 回显员工
     * @param id
     * @return
     */
    @ApiOperation("回显员工")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("回显员工：id={}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工
     * @param dto
     * @return
     */
    @ApiOperation("编辑员工")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO dto){
        log.info("编辑员工：{}", dto);
        employeeService.update(dto);
        return Result.success();
    }
}
