package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

// @Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)     //公共字段自动填充
    // @Options(useGeneratedKeys = true, keyProperty = "id")   //获取主键值，并且赋值给id属性
    // @Insert("insert into dish values (null, #{name}, #{categoryId}, #{price}, #{image}, #{description}, " +
    //         "#{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Dish dish);

    /**
     * 条件查询菜品列表
     * @param dto
     * @return
     */
    Page<DishVO> list(DishPageQueryDTO dto);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    Dish selectById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id和菜品名称查询菜品列表
     * @param categoryId
     * @param name
     * @return
     */
    List<Dish> getByCategoryId(Long categoryId, String name);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);


    List<Dish> listBy(Dish dish);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
