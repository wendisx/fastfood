package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    //注入工具类对象
    @Autowired
   private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> uploadFile(MultipartFile file){
        log.info("文件上传:{}",file);
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            assert originalFilename != null;
            String extension = originalFilename.substring(originalFilename.indexOf("."));
            String objectname=UUID.randomUUID().toString()+extension;
            String FilePath = aliOssUtil.upload(file.getBytes(), objectname);
            return Result.success(FilePath);
        } catch (IOException e) {
            //后端打印失败原因
            log.info("上传失败：{}",e.toString());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
