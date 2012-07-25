package com.hongtaili.helicopter.myView;

import com.hongtaili.helicopter.tool.MathUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class Control extends SurfaceView implements Runnable, Callback {

	private SurfaceHolder mHolder;
	private boolean isStop = false;
	private Thread mThread;
	private Paint mPaint;
	private Point mRockerPosition; // 摇杆位置
	private Point mCtrlPoint = new Point(400, 80);// 摇杆起始位置
	private int mControlRadius = 20;// 摇杆半径
	private int mWheelRadius = 60;// 摇杆活动范围半径
	private ControlListener listener = null; // 事件回调接口
	private RectF powerRect = new RectF(30,0,70,150);//能量棒区域
	private float power;//能量大小
	private int target;//触控区域
	
	public static final int ACTION_RUDDER = 1, ACTION_POWER = 2; // 1：摇杆  2：能量棒事件

	public Control(Context context) {
		super(context);
	}

	public Control(Context context, AttributeSet as) {
		super(context, as);
		this.setKeepScreenOn(true);
		power = powerRect.bottom;
		target = 0;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mThread = new Thread(this);
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		mPaint.setAntiAlias(true);// 抗锯齿
		mRockerPosition = new Point(mCtrlPoint);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSPARENT);// 设置背景透明
	}

	// 设置回调接口
	public void setControlListener(ControlListener rockerListener) {
		listener = rockerListener;
	}

	//@Override
	public void run() {
		Canvas canvas = null;
		while (!isStop) {
			try {
				canvas = mHolder.lockCanvas();
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);// 清除屏幕
				mPaint.setColor(Color.CYAN);
				canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius,
						mPaint);// 绘制范围
				mPaint.setColor(Color.RED);
				canvas.drawCircle(mRockerPosition.x, mRockerPosition.y,
						mControlRadius, mPaint);// 绘制摇杆
				mPaint.setColor(Color.GRAY);
				canvas.drawRect(powerRect,mPaint);//绘制能量棒范围
				mPaint.setColor(Color.GREEN);
				canvas.drawRect(powerRect.right, powerRect.bottom,powerRect.left,power,mPaint);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread.start();
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isStop = true;
	}

	//@Override
	@SuppressLint({ "ParserError", "ParserError" })
	public boolean onTouchEvent(MotionEvent event) {
		int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(),
				event.getY());
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 如果屏幕接触点在能量棒举行范围内
			if (powerRect.contains(event.getX(), event.getY())) {
				power=event.getY();
				target = 1;
				if(listener != null){
					listener.onControllerChanged(ACTION_POWER, (int)(powerRect.top - power));
				}
			}
			//即不再摇杆区，也不再能量棒区
			else if(len > mWheelRadius){
				target = 0;
				return true;
			}
			//摇杆区
			else{
				target = 2;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if(target == 1){
				if(!powerRect.contains(event.getX(),event.getY())){
					//如果拖到能量棒外面去了
					power = 0;
				}
				else{
					power=event.getY();
				}
				if(listener != null){
					listener.onControllerChanged(ACTION_POWER, (int)(powerRect.top - power));
				}
			}
			else if(target == 2){
				if (len <= mWheelRadius) {
					// 如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
					mRockerPosition.set((int) event.getX(), (int) event.getY());
	
				} else {
					// 设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
					mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint,
							new Point((int) event.getX(), (int) event.getY()),
							mWheelRadius);
				}
				if (listener != null) {
					float radian = MathUtils.getRadian(mCtrlPoint, new Point(
							(int) event.getX(), (int) event.getY()));
					listener.onControllerChanged(ACTION_RUDDER,
							Control.this.getAngleCouvert(radian));
				}
			}
		}
		// 如果手指离开屏幕，则摇杆返回初始位置
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mRockerPosition = new Point(mCtrlPoint);
		}
		return true;
	}

	// 获取摇杆偏移角度 0-360°
	@SuppressLint("ParserError")
	private int getAngleCouvert(float radian) {
		int tmp = (int) Math.round(radian / Math.PI * 180);
		if (tmp < 0)
			return -tmp;
		else
			return 180 + (180 - tmp);
	}
	// 回调接口
	public interface ControlListener {
		void onControllerChanged(int action, int change);
	}
}