package com.example.autoupdatefile.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageDialog {

	/**
	 * ����һ���Ի���
	 * 
	 * @param message
	 * @param a
	 */
	public static void Alert(String message, Activity a) {
		new AlertDialog.Builder(a).setTitle("��ʾ").setMessage(message)
				.setNegativeButton("�ر�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing - it will close on its own
						dialog.cancel();
					}
				}).show();
	}

	public static void Alert(String message, Activity a,
			DialogInterface.OnClickListener click) {
		new AlertDialog.Builder(a).setTitle("��ʾ").setMessage(message)
				.setNegativeButton("�ر�", click).show();
	}

	public static AlertDialog Confirm(String message, Context context,
			DialogInterface.OnClickListener click) {
		return new AlertDialog.Builder(context).setTitle("ȷ��")
				.setMessage(message).setPositiveButton("ȷ��", click)
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.cancel();
					}
				}).show();
	}

	public static AlertDialog Confirm(String message, Context context,
			DialogInterface.OnClickListener click,
			DialogInterface.OnClickListener cancle) {
		return new AlertDialog.Builder(context).setTitle("ȷ��")
				.setMessage(message).setPositiveButton("ȷ��", click)
				.setNegativeButton("ȡ��", cancle).show();
	}

}
