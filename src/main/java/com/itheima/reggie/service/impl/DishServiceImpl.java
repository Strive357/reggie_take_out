package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {



    @Autowired
    public DishFlavorService dishFlavorService;


    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
//        获得菜品id
        Long dishId = dishDto.getId();
//        将封装flavors
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    public void deleteWithFlavor(Long ids) {
     this.removeById(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
    }
    @Transactional
    public DishDto getByIdWithFlavor(Long ids) {
        Dish dish = this.getById(ids);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(DishFlavor::getDishId,ids);
        List<DishFlavor> flavor = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavor);
        return dishDto;
    }

    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.saveOrUpdate(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> dishList = dishDto.getFlavors();

        dishList = dishList.stream().map((item) ->{
               item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveOrUpdateBatch(dishList);

    }


}
