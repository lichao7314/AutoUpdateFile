package com.example.autoupdatefile;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * ���̹���
 * 
 * @author lichao
 * 
 */
public class ProcessManager {
	/**
	 * �Ƿ����12306����
	 * 
	 * @param a
	 * @return
	 */
	public boolean Is12306Process(Activity a) {
		return GetProcess("com.MobileTicket", a) != null;
	}

	/**
	 * ��ȡһ�����̸��ݽ�������
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
