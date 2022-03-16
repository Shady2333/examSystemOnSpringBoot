//package com.exam.controller.common;
//
//
//import com.alibaba.fastjson.JSONObject;
//import org.json.JSONException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import javax.servlet.http.HttpSession;
//import java.io.*;
//import java.util.UUID;
//
///**
// * @author Zuhai Chen
// * @version 1.0
// * @date 2020/12/26 14:13
// */
//@Controller
//public class FileController {
//
//    /**
//     * 文件所要上传的路径
//     */
//    @Value("${image.location}")
//    private String filePath;
//
//    @PostMapping("/image/upload")
//    @ResponseBody
//    public JSONObject imageUpload(HttpSession session, @RequestParam(value = "editormd-image-file") MultipartFile file) throws JSONException, FileNotFoundException {
//        if(file.isEmpty()){
//            System.out.println("文件为空");
//            return null;
//        }
//        JSONObject jsonObject = new JSONObject();
//        //获取文件原始名字
//        String fileName  = file.getOriginalFilename();
//        //获取后缀名
//        String suffixName = fileName.substring(fileName.lastIndexOf("."));
//        //新文件名
//        fileName = UUID.randomUUID() + suffixName;
//        //计算出文件存储位置+名字
//        File dest = new File(filePath+fileName);
//        //如果没有这个文件夹就创建一个
//        if(!dest.getParentFile().exists()){
//            dest.getParentFile().mkdirs();
//        }
//        try {
//            //写入
//            file.transferTo(dest);
//            jsonObject.put("success ",1);
//            jsonObject.put("message  ","成功");
//            jsonObject.put("url","/image/"+fileName);
//
//        }catch (IOException e){
//            jsonObject.put("success ",0);
//            jsonObject.put("message  ","失败");
//            jsonObject.put("url",null);
//            e.printStackTrace();
//            return jsonObject;
//        }
//        return jsonObject;
//    }
//
//
//}
