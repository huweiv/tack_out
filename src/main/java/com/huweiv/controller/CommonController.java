package com.huweiv.controller;

import com.huweiv.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName CommonController
 * @Description 文件通用controller
 * @CreateTime 2022/7/16 20:43
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${take-out.file-base-path}")
    private String fileBasePath;

    /**
     * @title upload
     * @description 文件上传
     * @author HUWEIV
     * @date 2022/7/16 20:55
     * @return com.huweiv.common.R<java.lang.String>
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名重名造成覆盖
        String filename = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象，判断当前保存目录是否已存在，不存在则创建
        File dir = new File(fileBasePath);
        if (!dir.exists())
            dir.mkdirs();
        file.transferTo(new File(fileBasePath + filename));
        return R.success(filename);
    }

    /**
     * @title download
     * @description 文件下载
     * @author HUWEIV
     * @date 2022/7/16 21:23
     * @return void
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(fileBasePath + name));

        //输出流，通过输出流将文件写回游览器，在游览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }

}
