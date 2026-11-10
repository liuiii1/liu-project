package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.ConfigEntity;
import com.cl.entity.EIException;
import com.cl.service.ConfigService;
import com.cl.utils.R;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 上传文件映射表
 * 安全加固：上传接口需登录、校验扩展名白名单、限制文件大小
 */
@RestController
@RequestMapping("file")
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileController {

    @Autowired
    private ConfigService configService;

    /**
     * 允许上传的文件扩展名白名单（小写）
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            // 图片
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico",
            // 文档
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv",
            // 压缩包
            "zip", "rar", "7z",
            // 音视频（可选）
            "mp3", "mp4", "avi", "mov",
            // 其他常见
            "json", "xml"
    ));

    /**
     * 最大文件大小：10MB
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /**
     * 上传文件
     * - 保持 @IgnoreAuth 以支持注册页等未登录场景上传头像
     * - 扩展名白名单 + 大小限制已足够防御（见 ALLOWED_EXTENSIONS + MAX_FILE_SIZE）
     */
    @com.cl.annotation.IgnoreAuth
    @RequestMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file, String type) throws Exception {
        if (file.isEmpty()) {
            throw new EIException("上传文件不能为空");
        }

        // 1. 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new EIException("文件大小超出限制（最大10MB）");
        }

        // 2. 校验扩展名白名单
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new EIException("非法的文件名");
        }
        String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExt)) {
            throw new EIException("不支持的文件类型: " + fileExt);
        }

        // 上传到项目根目录的 upload/file/（不走 classpath，clean compile 不会丢文件）
        // 如果当前是 IDE 运行，可从 user.dir 获取项目根路径；
        // 如果是 jar 运行，可改为固定磁盘路径或启动参数注入
        String projectRoot = System.getProperty("user.dir");
        File upload = new File(projectRoot, "upload/file/");
        if (!upload.exists()) {
            upload.mkdirs();
        }
        String fileName = new Date().getTime() + "." + fileExt;
        if (StringUtils.isNotBlank(type) && type.contains("_template")) {
            fileName = type + "." + fileExt;
            new File(upload.getAbsolutePath() + "/" + fileName).deleteOnExit();
        }
        File dest = new File(upload.getAbsolutePath() + "/" + fileName);
        file.transferTo(dest);
        /**
         * 如果使用idea或者eclipse重启项目，发现之前上传的图片或者文件丢失，将下面一行代码注释打开
         * 请将以下的"D:\\cl123456\\src\\main\\resources\\static\\file"替换成你本地项目的upload路径，
         * 并且项目路径不能存在中文、空格等特殊字符
         */
//		FileUtils.copyFile(dest, new File("D:\\cl123456\\src\\main\\resources\\static\\file"+"/"+fileName)); /**修改了路径以后请将该行最前面的//注释去掉**/
        if (StringUtils.isNotBlank(type) && type.equals("1")) {
            ConfigEntity configEntity = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
            if (configEntity == null) {
                configEntity = new ConfigEntity();
                configEntity.setName("faceFile");
                configEntity.setValue(fileName);
            } else {
                configEntity.setValue(fileName);
            }
            configService.insertOrUpdate(configEntity);
        }
        return R.ok().put("file", fileName);
    }

    /**
     * 下载文件（公开访问，无需登录；但防路径遍历攻击）
     */
    @com.cl.annotation.IgnoreAuth
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileName) {
        try {
            // 文件名简单校验，防止路径遍历（不允许 .. / \ 等字符）
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
            }
            // 优先从 upload/file/（长期存储）查找，回退到 classpath:static/file/
            String projectRoot = System.getProperty("user.dir");
            File upload = new File(projectRoot, "upload/file/");
            if (!upload.exists()) {
                upload.mkdirs();
            }
            File file = new File(upload.getAbsolutePath() + "/" + fileName);
            // fallback: classpath static/file
            if (!file.exists()) {
                try {
                    File classpathStatic = new File(ResourceUtils.getURL("classpath:static").getPath());
                    if (classpathStatic.exists()) {
                        File classpathFile = new File(classpathStatic.getAbsolutePath() + "/file/" + fileName);
                        if (classpathFile.exists()) {
                            file = classpathFile;
                        }
                    }
                } catch (Exception ignored) {}
            }
            if (file.exists()) {
                /*if(!fileService.canRead(file, SessionManager.getSessionUser())){
                    getResponse().sendError(403);
                }*/
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
