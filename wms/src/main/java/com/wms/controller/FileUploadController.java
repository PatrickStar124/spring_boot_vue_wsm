package com.wms.controller;

import com.wms.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 检查文件
            if (file.isEmpty()) {
                return Result.fail("请选择图片文件");
            }

            // 2. 生成文件名
            String originalFilename = file.getOriginalFilename();
            // 处理中文文件名和空格
            String safeFilename = originalFilename.replaceAll("[\\s\\p{Punct}]", "_");
            String newFileName = System.currentTimeMillis() + "_" + safeFilename;

            // 3. 创建上传目录（使用项目根目录下的 uploads 文件夹）
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator;

            // 或者使用绝对路径（选一个）
            // String uploadDir = "D:/uploads/"; // Windows
            // String uploadDir = "/home/uploads/"; // Linux

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return Result.fail("无法创建上传目录: " + uploadDir);
                }
            }

            // 4. 保存文件
            String filePath = uploadDir + newFileName;
            File dest = new File(filePath);

            // 调试信息
            System.out.println("上传目录: " + uploadDir);
            System.out.println("保存路径: " + filePath);
            System.out.println("目录是否存在: " + dir.exists());
            System.out.println("目录可写: " + dir.canWrite());

            try {
                file.transferTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
                // 尝试另一种保存方式
                try {
                    file.transferTo(dest.toPath());
                } catch (IOException ex) {
                    return Result.fail("文件保存失败: " + ex.getMessage());
                }
            }

            // 5. 返回访问URL
            String imageUrl = "/uploads/" + newFileName;
            System.out.println("生成图片URL: " + imageUrl);

            return Result.suc("上传成功", imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}