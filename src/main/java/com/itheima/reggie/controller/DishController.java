package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;


    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
//创建dish分页对象
//创建dishDto分页对象
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
//        拼接sql语句
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(name != null ,Dish::getName,name);
//        结果排序
        queryWrapper.orderByDesc(Dish::getCreateTime);
//        调用service.page方法
        dishService.page(dishPage,queryWrapper);
//对象拷贝把dishpage属性传到dishDtopage中，除了records属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
//  <dish> List 遍历
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 对象拷贝 将dish对象拷贝到dishDto中
            BeanUtils.copyProperties(item,dishDto);
//        封装dishDto对象
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category != null){
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }

        return dishDto;
        }).collect(Collectors.toList());
//        将<dishDto> List 设置到DishDtoPage.setrecords中
//        最后返回结果DishDtoPage
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 删除菜品
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){

        dishService.deleteWithFlavor(ids);
        return  R.success("删除菜单成功!");
    }

}
