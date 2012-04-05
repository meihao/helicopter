package com.hongtaili.helicopter.activity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hongtaili.helicopter.myView.ShowView;
import com.hongtaili.helicopter.tool.JpgFrame;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class HelicopterActivity extends Activity {

    private Socket socket ;
	
	private InputStream is;
    
    private ShowView mShowView;
   
    byte[] imageByte = new byte[65536];
    byte[] imageByte2 = new byte[65536];
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 获得屏幕的属性 然后传递到surfeceview中
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        /**
         * 窗口的宽度和高度
         */
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        
        mShowView = new ShowView(this,screenWidth,screenHeight);
        
        System.out.println("wwekjwhekjwh");
        /**
         * 设置为无标题栏
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**
         * 设置为全屏模式
         */
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(mShowView);
        
        createSocket();
        new ReadJpgStreamThread(is).start();
    }
    
    public void createSocket(){
    	String serverIp = this.getResources().getString(R.string.server_ip);
    	String serverPort = this.getResources().getString(R.string.server_port);
    	
    	try {
			socket = new Socket(serverIp, Integer.parseInt(serverPort));
			is = new DataInputStream(socket.getInputStream());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    class ReadJpgStreamThread extends Thread{
        
        boolean  flag = true;
        InputStream is ;
        
        public ReadJpgStreamThread(InputStream is){
        	this.is = is;
        }
		@Override
		public void run() {
			 while(flag == true){
				   
				   int startByteLocation = 0 , endByteLocation = 0 , i =0;
	        	   /**
	        	    * 每张JPG图片不会超过30kb 所以不会超过33000字节
	        	    */
				   for(i = 0 ; i < 33000; i++){
	        		   int temp = -1;
					   try {
						   /**
						    * 依次读取数据流中的每一个字节
						    */
					       temp = is.read();
					    } catch (IOException e) {
					       e.printStackTrace();
					    }
	        		   /**
	        		    * 将读取的每一个字节依次存到imageByte字节数组中
	        		    */
					   imageByte[i] = (byte)temp;
	        		   
	        		   if(temp == -1){
	        			   /**
	        			    * 表示已经读到末尾 没有数据可以读了
	        			    */
	        			   flag = false;
	        			   break;
	        		   }
	        		   /**
	        		    *  表示JPG数据的开始的地方
	        		    */
	        		   if(imageByte[i] == (byte)0xd8){
	             		  if(i > 0){
	             			   if(imageByte[i-1] == (byte)0xff){
	             				  startByteLocation = i-1;
	             			   } 
	             		  }
	             	  }
	        		   /**
	        		    * 表示JPG数据结束的地方
	        		    */
	             	  if(imageByte[i] == (byte)0xd9){
	             		  if(i>0){
	             			  if(imageByte[i-1] == (byte)0xff){
	             				  endByteLocation = i;
	             				  System.arraycopy(imageByte, startByteLocation, imageByte2, 0, endByteLocation - startByteLocation +1);
	             				  /**
	             				   * 将得到的字节数据转换成位图形式
	             				   */
	             				  JpgFrame.createJPGImage(imageByte2);
	             				  i = 0 ;
	             				  break;
	             			  }
	             		  }
	             	  }
	        	   }
	          }
		}
    }
}
