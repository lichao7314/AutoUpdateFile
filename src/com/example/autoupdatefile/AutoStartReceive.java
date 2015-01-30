package com.example.autoupdatefile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 开机启动广播
 * 
 * @author lichao
 * 
 */
public class AutoStartReceive extends BroadcastReceiver {

	public final static String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.v("OnOffServices", "FBBroadcastReceive receive action:" + action);
		if (action != null) {
			if (action.equalsIgnoreCase(BOOT_COMPLETED)) {
				Intent i = new Intent(context, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				Log.v("OnOffServices", "FBBroadcastReceive startactivity");
			}
		}
	}
}
