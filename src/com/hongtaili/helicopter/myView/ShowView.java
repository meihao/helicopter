package com.hongtaili.helicopter.myView;

import com.hongtaili.helicopter.tool.JpgFrame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShowView  extends SurfaceView implements SurfaceHolder.Callback,Runnable{
	   
		private SurfaceHolder mSurfaceHolder = null;
	  
		private int screenWidth;
		private int screenHeight;
		
		
		boolean flag = true;
		JpgFrame mJpgFrame = new JpgFrame();
		public ShowView(Context context ,int x ,int y ) {
			super(context);
			// TODO Auto-generated constructor stub
			mSurfaceHolder = this.getHolder();
			mSurfaceHolder.addCallback(this);
			
			this.screenHeight = y;
			this.screenWidth = x;
			this.setFocusable(true);
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			new Thread(this).start();
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			flag = false;
		 	mJpgFrame.bitMaps.clear();
		    mJpgFrame.index = -1;
		}

		public void run() {
			// TODO Auto-generated method stub
			while(flag == true){
	           			
	 			try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 		   synchronized (mSurfaceHolder) {
	 				Draw();
				} 
			}
		}
		
		public void Draw(){
			Canvas canvas = mSurfaceHolder.lockCanvas();
			if(mSurfaceHolder == null || canvas == null){
				return ;
			}
			System.out.println("kaishhuatu");
	        mJpgFrame.nextImage();
	        Bitmap bitmap = mJpgFrame.getImage();
	        System.out.println(bitmap);
	        Matrix matrix = new Matrix();
	        if(bitmap != null){
	        	 /**
	             * 获得图片的尺寸
	             */
	        	//System.out.println(bitmap.getWidth() + " :  "+bitmap.getHeight());
	        	//获取缩放比例
	        	matrix.postScale(screenWidth/bitmap.getWidth(),screenHeight/bitmap.getHeight());
	        	
	        	Bitmap dstmap = Bitmap.createBitmap(bitmap, 0,0 ,bitmap.getWidth(), bitmap.getHeight(),matrix,true);
				canvas.drawBitmap(dstmap, 0, 0, null);
			}
	        if(bitmap == null){
	        	//flag = false;
	        }
			mSurfaceHolder.unlockCanvasAndPost(canvas);
		}
	}