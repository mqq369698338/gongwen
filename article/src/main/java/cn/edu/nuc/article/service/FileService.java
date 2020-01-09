package cn.edu.nuc.article.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.jcr.GuestCredentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.springframework.stereotype.Service;

import cn.edu.nuc.article.controller.MD5Checksum;
import cn.edu.nuc.article.util.KeyProvider;

/**
 * 文件服务（调用 JackRabbit 管理上传后的文件）
 * 
 * JackRabbit实现了JSR 170规范，其管理文件的方式是通过维护一棵文件树来管理。
 * 
 * 
 * @author
 *
 */
@Service("fileService")
public class FileService {

	/**
	 * RMI地址
	 */
	private final static String RMI = "http://127.0.0.1:7000/rmi";

	/**
	 * 仓库
	 */
	private static Repository repository = null;

	/**
	 * 初始化仓库
	 * 
	 * @return
	 */
	public Repository init() {
		if (null == repository) {
			try {
				repository = new URLRemoteRepository(RMI);
			} catch (MalformedURLException e) {
				System.out.println("----------------------JackRabbit 仓库初始化失败----------------------");
				e.printStackTrace();
			}
		}
		return repository;
	}

	/**
	 * 获取Session
	 * 
	 * @return
	 */
	public Session getSession() {
		Session session = null;

		try {
			session = init().login(new SimpleCredentials("admin", "admin".toCharArray()));
			// session = init().login(new GuestCredentials());
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return session;
	}

	/*
	 * public void save(String filepath,String FileID,String url) {
	 * CloseableHttpClient httpclient = HttpClients.createDefault(); try { FileBody
	 * bin = new FileBody(new File(filepath)); StringBody comment = new
	 * StringBody(FileID, ContentType.TEXT_PLAIN); HttpPost httppost = new
	 * HttpPost(url); // HttpPost httppost = new HttpPost(
	 * "http://localhost:8098/hdsys/dbWeb/dispatchFile"); HttpEntity reqEntity =
	 * MultipartEntityBuilder.create() .addPart("fileContent", bin)
	 * .addPart("FileID", comment) .build(); httppost.setEntity(reqEntity);
	 * CloseableHttpResponse response = httpclient.execute(httppost); try {
	 * System.out.println("----------------------------------------");
	 * System.out.println(response.getStatusLine()); HttpEntity resEntity =
	 * response.getEntity(); if (resEntity != null) { InputStream content =
	 * resEntity.getContent(); String str = ConvertStreamToString(content);
	 * System.out.println("Response:" + "\n" + str); }
	 * EntityUtils.consume(resEntity); } finally { response.close(); } } catch
	 * (ClientProtocolException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } finally { try { httpclient.close(); } catch
	 * (IOException e) { e.printStackTrace(); } } }
	 *//**
		 * 向仓库中存入一个文件
		 * 
		 * @param is 待存入文件的输入流
		 * @return
		 */
//	public String save(InputStream is) {
//		
//		//获取一个UUID作为文件id
//		String fileId = KeyProvider.getPrimaryKey();
//		
//		//获取Session
//		Session session = getSession();
//		try {
//			//获取根节点
//			Node root = session.getRootNode();
//			
//			//给根节点添加一个文件节点
//			Node filenode = root.addNode(fileId, "nt:file");
//			
//			//给文件节点添加一个资源节点
//			Node resourcenode = filenode.addNode("jcr:content", "nt:resource");
//			
//			//设置资源节点的MIME类型
//			resourcenode.setProperty("jcr:mimeType", "application/octest-stream");
//			
//			//设置待存入文件的输入流
//			resourcenode.setProperty("jcr:data", is);
//			
//			//设置编码
//			resourcenode.setProperty("jcr:encoding", "UTF-8");
//			
//			//保存文件
//			session.save();
//			
//			//关闭输入流
//			is.close();
//			
//			//注销Session
//			session.logout();
//		} catch (RepositoryException e) {
//			System.out.println("---------------------上传文件过程中出现异常------------------------");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("---------------------上传文件过程中出现异常------------------------");
//			e.printStackTrace();
//		}
//		return fileId;
//	}

	private String ConvertStreamToString(InputStream content) {
		return null;
	}

//	/**
//	 * 按Fileid取出单个文件（相当于下载）
//	 * @param fileId 保存文件时拿到的FileId
//	 * @return
//	 */
//	public InputStream getByFileId(String fileId) {
//		
//		//用于读取文件的输入流对象
//		InputStream is = null;
//		
//		//获取Session
//		Session session = getSession();
//		
//		try {
//			//获取根节点
//			Node root = session.getRootNode();
//			
//			//按照FileId取出符合条件的节点
//			NodeIterator filenodeite = root.getNodes(fileId);
//			
//			if (filenodeite.hasNext()) {
//				//如果能找到相关记录
//				while (filenodeite.hasNext()) {
//					
//					//取出一个文件节点
//					Node filenode = filenodeite.nextNode();
//					
//					//取出这个文件结点下的资源子节点
//					NodeIterator resourcenodeite = filenode.getNodes();
//					
//					//如果有资源子节点
//					while (resourcenodeite.hasNext()) {
//						
//						//取出一个资源节点
//						Node resourcenode = resourcenodeite.nextNode();
//						
//						//找出数据域
//						if (resourcenode.getName().equals("jcr:content")) {
//							
//							//得到文件输入流
//							is = resourcenode.getProperty("jcr:data").getStream();
//						}
//					}
//				}
//			}
//			
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//		return is;
//	}

	/**
	 * 按Fileid删除文件
	 * 
	 * @param fileId 保存文件时拿到的FileId
	 * @return
	 */
	public void delete(String fileId) {

		// 获取Session
		Session session = getSession();

		try {
			// 获取根节点
			Node root = session.getRootNode();

			// 按照FileId取出符合条件的节点
			NodeIterator filenodeite = root.getNodes(fileId);

			filenodeite.remove();

			session.save();

			// 注销Session
			session.logout();
		} catch (RepositoryException e) {
			System.out.println("----------------------删除过程中出错-------------------------");
			e.printStackTrace();
		}
	}

	public String save(String fileName,InputStream is) {
		try {
			//本地的文件放在哪里
			File file = new File("D://test/"+fileName);
			
			if(file.exists()) {
				file.delete();
				file.createNewFile();
			}
			//文件输出流
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] data;
			

            int size = 1024*1024;
			//声明一个大小为输入流大小的数组
            byte[] buf = new byte[size];
            int len = 0;
            while((len=is.read(buf))!=-1) {
                System.out.println(len);
                int uselen= (len/8) * 8;
                for (int i = 0; i < uselen ; i++) {
                    buf[i] = (byte) (buf[i] ^union.kx[i % 4096]);
                }
                fos.write(buf,0,len);
            }
			//碰碰
			//循环读 用输出流写出去
//			while(is.read(data = new byte[1024]) != -1) {
//				
//				
//				fos.write(data);
//			}
			
			//最后flush 关闭流
			fos.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
		return fileName;
	}

	public InputStream getByFileId(String fileName){
		
		File file = new File("/data/WMWork/OPDF"+fileName);
		
		if(file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				return fis;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}
}

 class  union {
    static String k = "MIIEpAIBAAKCAQEAuW3ehtyu/iDWV/66NvCKlpXvmGiVX0NJ9ewPMEF0j7ghbOR\0" +
                    "FNHuWf4texlblDtHJeRmfcq8bvwgPTcZApDB1U0JmmkiGPQ8xIYRApZZnOumyiK\0" +
                    "jPrRIZG++dDaGzFfyNFKaLYabfRun99LMFfNrK+keaWyD+7ZpxjT+P6/CJLujUE\0" +
                    "0K0sL7d3/TfzQYxQgZGPJy18NZgZmKB71+/DheBJ4zBjIBMex1sv67mlY4XAScg\0" +
                    "xct5VDOR8s6Cm0xGwQcRoBIGUDk0wtAHcD7VAxE3pAPiN9TXP1Z2Gww3rAngyon\0" +
                    "eJV/jftV0Q8FvYQxxlTFDowPQuDzZEZMAoA2DwIDAQABAoIBAE488Vtt2dgX+th\0" +
                    "/gwYSiFnegSQfBYJyjcdNvthYtERG+laLUx/l1YZTQ9Xj7Bnool2aVhCJmedsey\0" +
                    "RscyVNau/pJMMbrdspYpCxxQW9c+Bs3a7EqvuXW0jSPS33QBzSxqQj8UNuqV6+d\0" +
                    "y/TF1145beh15T+bw/IhI683rVrsC0RhcfNVvkXNk4nnNOPpCA3aWc3DeLYGo/2\0" +
                    "FKKu4c+xByXQGYtXwlwSfg+j/ocBIF9D6FzV1dcNgw8EqiBYsqC4glm/G3TtUKr\0" +
                    "ybYKT2nPHDIzPHWWmDPRu82L73g4c3uz7DCDoT0f2Itu+1+wQuWBo6/JhS37was\0" +
                    "RJ8HzkECgYEA8FiDuC+wiwwR8SJRKvIRtvGoeGKk5dP6+j3Z5uvyBug2WySB6Gq\0" +
                    "PmAIDe0k2rTGKeIZgum8dVe6lWlGQtpMnu9O9b3S/lpgrDssmKtLFAkBVYaraZa\0" +
                    "eXudzSrgIJYNiIp0DuSnu0hmwP4cCKf9gAi5GEnGgNRmb7+vh1+40xkCgYEAxYG\0" +
                    "R59SwBCdG7Cybktstpr18S3zcB0cdKx+pV24TDzpKAsosJhJa7fl1SJMOgJVBCB\0" +
                    "yhsabv/isT82Uwm9YNp3ddAWUne+7f7ZMqjFVvQ5hnxyvTSGC64Tr/Geb0HCBce\0" +
                    "+Cj7oyiTFan1oP+k0GxuAPcpICfjNL1KJw3VX2cCgYEAwLm5pdBX+KfNG1cxTwa\0" +
                    "Pd26Ag7Xu5OoBVHjkVBR20gJjjxQD1a5ymKnEsO+ccHTxXIQkvFqtY2LiJPKS4F\0" +
                    "P4EWB+gXcO535qPstt9kbMRr1XEPsrOHtwJxHzvqTTcF0x9ywR6JgZwlCUy6nhZ\0" +
                    "KHvWMKu4CUZIfgNvdVZElsECgYEAm4RmFCtr6TgrvWYA2cAiNhcGmYXpLdp+UZC\0" +
                    "mHyXZdCAdrtyqrr+9Qa0voJnKzYy4zDfthhpVAXA5ngzumbs65qaffX/3afUe2o\0" +
                    "dCNYYAOmWDMd1cXJ1uCXN+81Jb3NnOdhDsKWgf6s8l4Fv2QMzuDSLYhwpZVC3oA\0" +
                    "J0pJPPkCgYADtLFrXeU9Vd4cuA+0OH555IykeF/1AqAt5Uue9sL6pnD4sCW0WiW\0" +
                    "YbCwBsmD0ptiDVtFPZK4bRN+iyX1Gle+GPZbu2Nv/7Mylt28aWkdqJgLSL2/AgL\0" +
                    "lMkePk0lTjY+JO0B08tkISz5jvh8mz7IESS7M5ciVYmh0f8HPMIzMPK3QaGTqKC\0" +
                    "SjBmwQjxpfIZNcWXqaFpvwzdwfUufuKtiJIVKrSmW1QzEy8UNfZzUwLtD0cHm25\0" +
                    "I4bQEzcYDFcxhAVTr28MyuKMMjk7YeMtVCncQP7HGJyYgFLSPY0Gf/wnqcivzM4\0" +
                    "RQgAVTjbyHDy0XPb8STVN1uu0kt4zgJ9Qp/yawIDAQABAoIBAQDIubnBPwc9cak\0" +
                    "mdE7ur8MLf8RqiQsrXsy5eeQXaYU1ZC5eVdtO2TmtnlcDzxo+poAZQ/sU+sgOKJ\0" +
                    "kPtV+4yhK8E0yW0FzAuh+4diS5cywDef1jyuaF+o8jQfMQ7RvSh+OVwL4WyR04j\0" +
                    "HmEkTcJLzSw7W5FDsWytNpCUpd0cr/5A792wtsSFdjvAHCGmdbLe2Fu85lGBIVq\0" +
                    "1HLjx6SXG/Oc4/H5AmDUzAx+/xUVivoa0KfMV+HzhgxjORJqaP2iw1vUVLUUjmp\0" +
                    "4nuGjjpW1U7KxRocetZ3QYwAJdPC9+hux6tK2uPOShb41oC8AusgaVFWOXkznEX\0" +
                    "nGDMn6CBAoGBAPVzoZZQQK8/0L8+N6XF2M7I2CTgjEDzL+Odd1rG6fv9eljH7g9\0" +
                    "ECEV+TEohgShB1XUZzj7eSay9ei03FLwrWZQKCoNvjrXGm63falqA8k+lQS5/8r\0" +
                    "uNfKhzEKQe2TnEWZ9lhtxFuPvuHLZajYGyvJaZ5NlVQL9y1y/AsjamirAoGBANe\0" +
                    "10bfwRYBndtcfyFFPifL7fSOxVbY0jPCiG2p7F3iymeJGPhujwXigh4JQWiioBF\0" +
                    "0dlxdL6yVqsdsUIkrDb70eRu42mMbTQu5i3gnE/2v34Cyvea9ZPTsmTD3vXLHdb\0" +
                    "X/YkjLmDiyS61NKg2oLmOwEfa0jpIDihtfwkQB1BAoGAY8q4VpmYQugKIcbU7xQ\0" +
                    "OsBTiGK3Ay0cOklqHkEjHm2HEZNviVZYA2ugncjg+/7QMagis3siXYaDDjKCJUJ\0" +
                    "7VsHiVl/DwMnXv5BG+DbWzWTdpF8mud8DFfuMRT/iuzpqy6WhotSOvutWrXMRiJ\0" +
                    "oaXIyDC41qvqudK3AHxMpfMCgYEArKmV89QKJWv0HqfxvCBxU4Y9wR2inxkhA5A\0" +
                    "pP9sOCYXiWu90j8XWlqDbDZz7LPi0UH5RV1rbd0GKxylRNyGDaIcGghvK73wOc6\0" +
                    "XQJDlpeP/g2UtVixBNShY7G56mPmKpFRXqkXBwKHNExrc7SieByiBhs6YfVH97Y\0" +
                    "SKptpkECgYEA5cWZJ53fX1lDEqmAFT4P1T7xLto3NT4jhuh0kLKFWQTwd7lL+C9\0" +
                    "uOlbVZhP+4ffqwg5AaKG5E4sXaNd92Szdq3tpvk5d+hsOG9N/2dOUxUwN5W7JWB\0" +
                    "WpeE92X/na/DjeJHnri/45g8+OdXFTALzEjYnXinj+Y2w3VpegITPAjqqfkn/GF\0" +
                    "AvjEUFq4RwiCdWYgbXT0zig4+8GmQGTtx5FdClO7E5m0GkOfIm3kKJU22pBo0LX\0" +
                    "rs7lps+IL8IyjYqm8HJ7a8x/O+4wr2PoCW+ydpelM9OkIEvb0pYVJeNYrz5kMmC\0" +
                    "QeW58wzzFcsVrYQdRUWXEfbDPLXcIxYvQJz3jQIDAQABAoIBAFUuv4LH5wz3wwi\0" +
                    "hVR6tadYXTU6PaEuDAK0KJAcLK11tkW1mYbHcSJl8uyAtD0iZ+MIuBalBRHBYW2\0" +
                    "2P/CuGTL7gb27bwpjKl995ZhBgzZ1qeLUoNd+R8Y9MSEBpXZVJrO/MC925LMthp\0" +
                    "PeiXKH0KGkOYUf3GzcqQjaf7isFc0FCiQV9EiPOEad3oDj+1CfLT1UZZOtQ8pea\0" +
                    "ZrovpscDCl48mw3gTB6MZTGhFIFe9Xd1yEuqGeMovfbcUBt+fLW9fjVLkHw7e+T\0" +
                    "7RLQY9D3nSZDBnTsqKNspkIQnHB1hjcArnxKXXJpn1vYABp2BLA4vxx2yihLj++\0" +
                    "UEkiua0CgYEA1NXFJdlZfstZIsxNbHrgsWn/EqH9PH4kg77o2vPrn9kVZLOTnzD\0" +
                    "zv/PbtZqhtJWR3oy3mynxHCix6gQ93k6vP5yRPZAl9gevedFmh9NOhGizNGKAbi\0" +
                    "Jm9e7UVjeVlEPga09CDfyDgLnNuFwY0dopu3hqRq33lLGu1c0cD0bN8CgYEAz5M\0" +
                    "/weJ4xSrH7CDSwirKznUSwL9LjFgoChFb0khjpNRvyZxpnK6BLcLShaZJVdUIsp\0" +
                    "9tig6L7QxzZccZkpmiBDPfqV2mWvpnBJJg4th1UkNzb1c2XmrABS3TaqMNqun5g\0" +
                    "5sOKO0o+E4LygyNd8m8j+n9WUy32FNLTH8SwfRMCgYBErea8W/LBM6VD0WF6i/x\0" +
                    "w8KHpLYu+xWVXdmOLf/uyQJaSIhuakPE4E9zmMUvVFh7IDeiBrXscdxVSSqQcRI\0" +
                    "QmxlTIFzfk4NQb/4B/nHr0E1jrhV7dmNcYrUTfvXgLLAwTFLRgBwf4VfzIbQ+lS\0" +
                    "0HXcgNnr2xwdSFMXbIhzSQKBgQC2h9FzkA+CrkQko7Vwn/jVFA9IvyIgIq+TZpZ\0";
    public static char[] kx = k.toCharArray();
}
