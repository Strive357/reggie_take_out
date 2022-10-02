package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(Long ids);

    public void updateWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long ids);
}
