package com.hongtaili.helicopter.activity;

import com.hongtaili.helicopter.myView.Control;
import com.hongtaili.helicopter.myView.Control.ControlListener;

import android.os.Bundle;
import android.app.Activity;

public class ControlActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);
        final Control Control = (Control) findViewById(R.id.control);
        Control.setControlListener(new ControlListener() {
        	
        //@Override
        	public void onControllerChanged(int action, int change) {
        		if(action == Control.ACTION_POWER) {
        			//TODO:能量棒时间的实现   change为能量的长度
        			}
        		else if(action == Control.ACTION_RUDDER){
        			//TODO:摇杆事件的实现   change为摇杆的弧度0-360
        		}
        		}
        });
    }
   
}
