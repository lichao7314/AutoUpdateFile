package com.example.autoupdatefile;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 进程管理
 * 
 * @author lichao
 * 
 */
public class ProcessManager {
	/**
	 * 是否包含12306进程
	 * 
	 * @param a
	 * @return
	 */
	public boolean Is12306Process(Activity a) {
		return GetProcess("com.MobileTicket", a) != null;
	}

	/**
	 * 获取一个进程根据进程名称
	 * 
	 * @param process
	 * @param a
	 * @return
	 */
	public ActivityManager.RunningAppProcessInfo GetProcess(String process,
			Activity a) {
		ActivityManager am = (ActivityManager) a
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> processList = am
				.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo p : processList) {
			if (p.processName.equals(process)) {
				return p;
			}
		}

		return null;
	}
}
