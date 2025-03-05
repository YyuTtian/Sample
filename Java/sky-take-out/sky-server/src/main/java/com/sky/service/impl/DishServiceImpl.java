package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Transactional  //0.开启事务（涉及到多张表的增删改操作需要开事务）
    public void addDish(DishDTO dto) {
        // 1.构造菜品基本信息数据，将其存入dish表中
        Dish dish = new Dish();
        // 拷贝属性值
        BeanUtils.copyProperties(dto, dish);
        // 调用mapper保存方法
        dishMapper.insert(dish);
        log.info("dishId={}", dish.getId());

        // 2.构造菜品口味列表数据，将其存入dish_flavor表中
        List<DishFlavor> dishFlavorList = dto.getFlavors();
        if (dishFlavorList != null && dishFlavorList.size() > 0) {
            // 2.1关联菜品id
            dishFlavorList.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            // 2.2 调用mapper保存方法,批量插入口味列表数据
            dishFlavorMapper.insertBatch(dishFlavorList);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dto) {
        // 1.设置分页参数
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 2.调用mapper的列表查询方法，强转成Page
        Page<DishVO> page = dishMapper.list(dto);

        // 3.封装PageResult对象并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional  //0.开启事务
    public void delete(List<Long> ids) {
        // 1.删除菜品之前，需要判断菜品是否启售，启售中不允许删除
        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        // 2.需要判断菜品是否被套餐关联，关联了也不允许删除
        Integer count = setmealDishMapper.countByDishId(ids);
        if(count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 3.删除菜品基本信息 dish表
        dishMapper.deleteBatch(ids);

        // 4.删除菜品口味列表信息 dish_flavor表
        dishFlavorMapper.deleteBatch(ids);
    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();

        // 1.根据菜品id查询菜品基本信息,封装到dishVO中
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish, dishVO);

        // 2.根据菜品id查询口味列表数据,封装到dishVO中
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(flavors);

        // 3.返回DishVO对象
        return dishVO;
    }

    @Transactional  //0.开启事务
    public void update(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        // 1.修改菜品的基本信息，dish表
        dishMapper.update(dish);

        // 2.修改口味列表信息，dish_flavor表
        // 由于口味数据可能增加、可能删除、还可能修改口味的值，涉及到增删改操作，所以先全部删除旧数据，再添加新数据
        dishFlavorMapper.deleteByDishId(dto.getId());

        List<DishFlavor> flavors = dto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 关联菜品id
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品启售、停售
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 如果是停售操作，还需要将包含当前菜品的套餐也停售
        if (status.equals(StatusConstant.DISABLE)) {
            // 联表查询setmeal、dish、setmeal_dish
            List<Setmeal> setmealList = setmealMapper.getByDishId(id);
            for (Setmeal setmeal : setmealList) {
                if (setmeal != null) {  //有关联的套餐才需要执行update操作，注意判空处理，否则NPE
                    setmeal.setStatus(StatusConstant.DISABLE);
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId
     * @param name
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId, String name) {
        return dishMapper.getByCategoryId(categoryId, name);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.listBy(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
