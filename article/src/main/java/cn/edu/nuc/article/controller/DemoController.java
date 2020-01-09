package cn.edu.nuc.article.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.aspectj.apache.bcel.classfile.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DemoController {
	@Value("${tempdir}")
	private  String tempdir;
	
	@RequestMapping("/demo/upload")
	public void upload(String name,MultipartFile doc){
		if(name == null || doc == null) {
			return;
		}
		
		try {
			//本地的文件放在哪里
			File file = new File(tempdir + name);
			
			//你上传的文件
			InputStream is = doc.getInputStream();
			
			//文件输出流
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] data;
			//加密算法
			
			//循环读 用输出流写出去
			if(is.read(data = new byte[1024]) != -1) {
				fos.write(data);
			}
			
			//最后flush 关闭流
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

}
