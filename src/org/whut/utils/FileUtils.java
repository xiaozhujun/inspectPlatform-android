package org.whut.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class FileUtils {
	
	private static String inspectDir;
	
	//判断SD卡是否插入
	public static boolean isMounted(){
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}
	
	/**
	 * @param inputStream 文件输入流
	 * @param filename 文件名
	 * @param filepath 文件保存路径
	 * @return true保存成功
	 */
	public static boolean SaveCofigFiles(InputStream inputStream,String fileName,String filePath) throws Exception{
	
		if(isMounted()){
			OutputStream os;
			File file = new File(filePath);
			//如果文件夹不存在，则创建
			if(!file.exists()){
				file.mkdirs();
			}
			
			file = new File(filePath+"/"+fileName);
			
			//如果文件存在，则删除
			if(file.exists()){
				file.delete();
			}
			
			byte[] temp = new byte[1024];
			int len;
			os = new FileOutputStream(file);
			while((len = inputStream.read(temp)) != -1){
				os.write(temp,0,len);
			}
			os.close();
			inputStream.close();
			//文件保存成功
			Log.i("file", filePath+"/"+fileName+"下载成功");
			return true;
		}
		//内存卡没有插入，下载失败
		return false;
	}
	
	public static String GetCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	public static String GetCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}

	public static void setInspectDir(String inspectdir) {
		// TODO Auto-generated method stub
		FileUtils.inspectDir = inspectdir;
	}

	public static String getInspectDir() {
		// TODO Auto-generated method stub
		String rootDir = Environment.getExternalStorageDirectory().toString();
		String inspectPath = null;
		if(rootDir.endsWith("/")){
			inspectPath = rootDir+inspectDir;
		}else{
			inspectPath = rootDir+"/"+inspectDir;
		}
		return inspectPath;
	}

	public static boolean prepareInspectFile(String tempFile, String newFileName) {
		// TODO Auto-generated method stub
		String inspectPath = buildInspectDir();
		InputStream inStream = null;
        FileOutputStream fs = null;
		try {   
	           int byteread = 0;   
	           File oldfile = new File(tempFile);   
	           if (oldfile.exists()) { 
	               inStream = new FileInputStream(oldfile);  
	               File newFile =  new File(inspectPath+"/"+newFileName);
	               if(!newFile.exists()){
	            	   newFile.createNewFile();
	               }
	               fs = new FileOutputStream(newFile);   
	               byte[] buffer = new byte[1444];   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   fs.write(buffer, 0, byteread);   
	               }   
	           }
	           return true;
	       }   
	       catch (Exception e) {   
	           e.printStackTrace();   
	  
	       }finally{
	    	   try {
		    		  if(fs!=null){
		    			  fs.close();
						  inStream.close();  
		    		  }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	       }  
		return false;
	}

	private static String buildInspectDir() {
		// TODO Auto-generated method stub
		String inspectPath = null;
		if(makeDir(inspectDir)){
			inspectPath =  getInspectDir();
		}
		return inspectPath;
	}
	
	public static boolean makeDir(String dir){
		if(isMounted()){
			String rootDir = Environment.getExternalStorageDirectory().toString();
			String dirPath = null;
			if(rootDir.endsWith("/")){
				dirPath = rootDir + dir;
			}else{
				dirPath = rootDir + "/" +dir;
			}
			
			File dirFile = new File(dirPath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			return true;
		}
		return false;
	}

	public static void deleteFile(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}

	public static void prepareImageDir(String imagePath) {
		// TODO Auto-generated method stub
		File dirFile = new File(imagePath);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		
	}

	public static void deleteImages(List<String> images_inspect) {
		// TODO Auto-generated method stub
		for(String path:images_inspect){
			File file = new File(path);
			if(file.exists()){
				file.delete();
			}
		}
	}

	@SuppressLint("SdCardPath")
	public static void createUpdateDirectory() {
		// TODO Auto-generated method stub
		File dirFile = new File("/sdcard/inspect/update");
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
	}	
}
