package com.example.autoupdatefile;

import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * 12306运行情况管理
 * 
 * @author lichao
 * 
 */
public class RunManager {

	/**
	 * 打开12306
	 * 
	 * @param a
	 */
	public static void Open12306(Activity a) {

		ComponentName name = new ComponentName("com.MobileTicket",
				"com.MobileTicket.MobileTicket");
		try {
			Intent intent = new Intent();
			intent.setComponent(name);
			a.startActivity(intent);
		} catch (Exception e) {

		}
	}

	/**
	 * 关闭12306
	 * 
	 * @param a
	 */
	public static void Close12306(Activity a) {
		try {
			ComponentName componetName = new ComponentName("com.MobileTicket",
					"com.MobileTicket.MobileTicket");

			final ActivityManager am = (ActivityManager) a
					.getSystemService(Context.ACTIVITY_SERVICE);

			if (android.os.Build.VERSION.SDK_INT < 8) {
				am.restartPackage(componetName.getPackageName());
			} else {
				am.killBackgroundProcesses(componetName.getPackageName());
			}
		} catch (Exception e) {
			// install();
		}
	}

	/**
	 * 打开控制12306程序
	 * 
	 * @param a
	 */
	public static void OpenControl12306(Activity a) {
		ComponentName name = new ComponentName("com.fangbian.Control12306Box",
				"com.fangbian.Control12306Box.MainActivity");
		try {
			Intent intent = new Intent();
			intent.setComponent(name);
			a.startActivity(intent);
		} catch (Exception e) {

		}
	}

	/**
	 * 关闭控制12306
	 * 
	 * @param a
	 */
	public static void CloseControl12306(Activity a) {

		try {
			ComponentName componetName = new ComponentName(
					"com.fangbian.Control12306Box",
					"com.fangbian.Control12306Box.MainActivity");
			final ActivityManager am = (ActivityManager) a
					.getSystemService(Context.ACTIVITY_SERVICE);

			if (android.os.Build.VERSION.SDK_INT < 8) {
				am.restartPackage(componetName.getPackageName());
			} else {
				am.killBackgroundProcesses(componetName.getPackageName());
			}
		} catch (Exception e) {
			// install();
		}
	}
}
