package org.whut.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitmapUtils {
	
	
	
    public static Bitmap loadBitmap(String imgpath) {  
        
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(imgpath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		
		opts.inTempStorage = new byte[100 * 1024];
		
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		
		opts.inPurgeable = true;
		
		opts.inSampleSize = 4;
		
		opts.inInputShareable = true; 
		
		Bitmap btp =BitmapFactory.decodeStream(inStream,null, opts);
		
		return btp;
    }  
    

	public static Bitmap loadBitmap(String imgPath,boolean adjustOrientation){
        if (!adjustOrientation) {  
            return loadBitmap(imgPath);  
        } else {  
            Bitmap bm = loadBitmap(imgPath);  
            int digree = 0;  
            ExifInterface exif = null;  
            try {  
                exif = new ExifInterface(imgPath);  
            } catch (IOException e) {  
                e.printStackTrace();  
                exif = null;  
            }  
            if (exif != null) {  
                // 读取图片中相机方向信息  
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                        ExifInterface.ORIENTATION_UNDEFINED);  
                // 计算旋转角度  
                switch (ori) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    digree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    digree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    digree = 270;  
                    break;  
                default:  
                    digree = 0;  
                    break;  
                }  
            }  
            if (digree != 0) {  
                // 旋转图片  
                Matrix m = new Matrix();  
                m.postRotate(digree);  
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                        bm.getHeight(), m, true);  
            }  
            return bm;  
        }  
	}
	
	public static Bitmap changeBitmap(String filePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap =BitmapFactory.decodeFile(filePath,options);
		options.inJustDecodeBounds = false; 
		int be = options.outHeight/20; 
		  if (be <= 0) { 
			  be = 10; 
		  }
		  options.inSampleSize = 20; 
		  bitmap = BitmapFactory.decodeFile(filePath,options); 
          int digree = 0;  
          ExifInterface exif = null;  
          try {  
              exif = new ExifInterface(filePath);  
          } catch (IOException e) {  
              e.printStackTrace();  
              exif = null;  
          }  
          if (exif != null) {  
              // 读取图片中相机方向信息  
              int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                      ExifInterface.ORIENTATION_UNDEFINED);  
              // 计算旋转角度  
              switch (ori) {  
              case ExifInterface.ORIENTATION_ROTATE_90:  
                  digree = 90;  
                  break;  
              case ExifInterface.ORIENTATION_ROTATE_180:  
                  digree = 180;  
                  break;  
              case ExifInterface.ORIENTATION_ROTATE_270:  
                  digree = 270;  
                  break;  
              default:  
                  digree = 0;  
                  break;  
              }  
          }  
          if (digree != 0) {  
              // 旋转图片  
              Matrix m = new Matrix();  
              m.postRotate(digree);  
              bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),  
                      bitmap.getHeight(), m, true);  
          }  
          return bitmap;  
	}
}
