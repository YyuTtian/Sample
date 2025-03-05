package com.example.demo.mapper;

import com.example.demo.entity.TbUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbUserMapper extends BaseMapper<TbUser> {

    List<UserVO> filterUser(String userName, String deptName, String hobbyName);
}
