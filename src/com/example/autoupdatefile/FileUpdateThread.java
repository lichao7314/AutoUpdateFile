package com.example.autoupdatefile;

import android.os.Handler;
import android.os.Message;

public class FileUpdateThread {
	private boolean isRun = false;

	private Handler currentHander = null;

	public Handler getCurrentHander() {
		return currentHander;
	}

	public void setCurrentHander(Handler currentHander) {
		this.currentHander = currentHander;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public void Start() 
	{
		Thread thread = new Thread(new Runnable() { 
			@Override
			public void run() {
				while (isRun) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (isRun) {
						Message msg = new Message();
						msg.what = 1101;
						currentHander.sendMessage(msg);
					}
				} 
				Message msg1 = new Message();
				msg1.what = 1102;
				currentHander.sendMessage(msg1); 
			}
		});
		thread.start();
	}

	private int sleepTime;

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

}
