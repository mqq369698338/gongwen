package cn.edu.nuc.article.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.edu.nuc.article.service.LOGGER;

public class MD5Checksum {
	
	  
	    private static byte[] createChecksum(String filename) {  
	        InputStream fis = null;  
	        try {  
	            fis = new FileInputStream(filename);  
	            byte[] buffer = new byte[1024];  
	            MessageDigest complete = MessageDigest.getInstance("MD5");  
	            int numRead = -1;  
	  
	            while ((numRead = fis.read(buffer)) != -1) {  
	                complete.update(buffer, 0, numRead);  
	            }  
	            return complete.digest();  
	        } catch (FileNotFoundException e) {  
	            LOGGER.error(e.getMessage(), e);  
	        } catch (NoSuchAlgorithmException e) {  
	            LOGGER.error(e.getMessage(), e);  
	        } catch (IOException e) {  
	            LOGGER.error(e.getMessage(), e);  
	        } finally {  
	            try {  
	                if (null != fis) {  
	                    fis.close();  
	                }  
	            } catch (IOException e) {  
	                LOGGER.error(e.getMessage(), e);  
	            }  
	        }  
	        return null;  
	  
	    }  
	  
	    // see this How-to for a faster way to convert  
	    // a byte array to a HEX string  
	    public static String getMD5Checksum(String filename) {  
	      
	            if (!new File(filename).isFile()) {  
	                return null;  
	            }  
	            byte[] b = createChecksum(filename);  
	            if(null == b){  
	                return null;  
	            }  
	            StringBuilder result = new StringBuilder();  
	  
	            for (int i = 0; i < b.length; i++) {  
	                result.append(Integer.toString((b[i] & 0xff) + 0x100, 16)  
	                        .substring(1));  
	            }  
	            return result.toString();  
	  
	    }  
	  
	    public static void main(String args[]) {  
	        try {  
	            long beforeTime = System.currentTimeMillis();  
	            String path = "C:\\Users\\sufei01\\Desktop\\接口文档.docx";  
	            String md5 = getMD5Checksum(path);  
	            System.out.println(md5);  
	              
	            File file = new File(path);  
	              
	            System.out.println(path+ "'s size is : " +file.length()+" bytes, it consumes " + (System.currentTimeMillis() - beforeTime) + " ms.");  
	        } catch (Exception e) {  
	            LOGGER.error(e.getMessage(), e);  
	        }  
	    }  

}
