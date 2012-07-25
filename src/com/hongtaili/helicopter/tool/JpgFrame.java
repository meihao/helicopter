package com.hongtaili.helicopter.tool;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class JpgFrame {

	 /**
	  * 保存视频传输过来的JPG的帧向量
	  */
  // public static Vector<Bitmap> bitMaps = new Vector<Bitmap>(1);
	  
	public static List<Bitmap> bitMaps = new ArrayList<Bitmap>();
	
	/**
	 * 当前播放帧的索引
	 */
   public static int index = -1;
	public JpgFrame(){
		/*bitMaps = new Vector<Bitmap>(1);
       index = -1;	*/	
	}
	
	/**
	 * 添加一帧
	 */
	public   void addImage(Bitmap image){
		
		//bitMaps.addElement(image);
		bitMaps.add(image);
	}
	
	/**
	 * 返回帧数
	 */
	
	public int size(){
		
		return bitMaps.size();
	}
	/**
	 * 得到当前帧的图片
	 */
	
	public Bitmap getImage(){
		
		if(size() == 0){
			return null;
		}
		if(index >= size()|| index == -1){
			return null;
		}
		else{
         //  return  bitMaps.elementAt(index);			
	      return bitMaps.get(index);
		}
	}
	
	public void nextImage(){
		if(index + 1 < size()){
			index ++;
		}
		else {
			index = size()-1;
		}
	}
	
	public static JpgFrame createJPGImage(byte abyte[]){
		
		JpgFrame frame = new JpgFrame();
		/**
		 * 根据字节流生成位图
		 */
       Bitmap bitmap = BitmapFactory.decodeByteArray(abyte, 0, abyte.length);
       frame.addImage(bitmap);
       System.out.println(bitMaps.size());
		return frame;
	}
}
