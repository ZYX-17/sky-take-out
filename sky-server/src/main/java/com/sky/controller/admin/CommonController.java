package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.impl.DishSerciveImpl;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private DishService dishSercive;


    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("文件删除：{}",ids);
        dishSercive.deleteBatch(ids);
        return Result.success();
    }
}
