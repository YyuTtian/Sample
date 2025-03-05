package com.example.demo.service.impl;

import com.example.demo.entity.TbDept;
import com.example.demo.mapper.TbDeptMapper;
import com.example.demo.service.ITbDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Lazy // 延迟加载（第一次使用bean对象时，才会创建bean对象并交给ioc容器管理）
@Scope("prototype") // 默认singleton 单例   prototype表示每次都创建一个新的实例
@Service
public class TbDeptServiceImpl extends ServiceImpl<TbDeptMapper, TbDept> implements ITbDeptService {

}
