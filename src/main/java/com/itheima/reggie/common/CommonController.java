package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上转和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${shouzhou.path}")
    private String bashPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
//1.获取file的原始名
        String originalFilename = file.getOriginalFilename();
//2.获取file后缀名
        String suffx = originalFilename.substring(originalFilename.lastIndexOf("."));
//3.使用UUID重新生成文件名，防止文件名称重复造成文件覆盖+后缀名
        String fileName = UUID.randomUUID().toString() + suffx;
// 4.创建一个目录存放照片文件
        File  dir = new File(bashPath);
// 5.先判断目录是否存在，不存在就新建目录，
        if(!dir.exists()){
            dir.mkdir();
        }
// 6.调用file.transferTo()将临时存放到指定位置
        try {
            file.transferTo(new File(bashPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
//        输入流，获取文件
            FileInputStream fileInputStream = new FileInputStream(new File(bashPath + name));
//        输出流，将文件通过HttpServletResponse响应给浏览器(要selvet的输出流)
            ServletOutputStream outputStream = response.getOutputStream();
//        设置响应类型response.setContentType("image/jpeg");
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
