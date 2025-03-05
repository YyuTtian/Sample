package com.example.demo.service.impl;

import com.example.demo.entity.TbUser;
import com.example.demo.mapper.TbUserMapper;
import com.example.demo.service.ITbUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.vo.UserVO;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements ITbUserService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public List<UserVO> filter(String userName, String deptName, String hobbyName, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        return tbUserMapper.filterUser(userName, deptName, hobbyName);
    }

    public void test() {
        int a = 1 / 0;
    }
}
