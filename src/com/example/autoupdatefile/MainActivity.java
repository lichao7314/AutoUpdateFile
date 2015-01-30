package com.example.autoupdatefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.autoupdatefile.Log.SystemLog;
import com.example.autoupdatefile.file.FileCommonLib;
import com.example.autoupdatefile.file.FileCompare;
import com.example.autoupdatefile.ui.MessageDialog;
import com.example.autoupdatefile.vnc.VNCManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	CheckTaskThread updateThread = new CheckTaskThread();

	TextView serverState = null;

	Button startBtn = null;

	Button stopBtn = null;

	int count = 0;

	boolean isRun = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		try {
			Process process = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MessageDialog.Alert("当前程序没有root权限，请提升root权限后重启程序", this);
			return;
		}

		serverState = (TextView) this.findViewById(R.id.textView2);

		startBtn = (Button) this.findViewById(R.id.button1);
		stopBtn = (Button) this.findViewById(R.id.button2);

		Init();

		Start();

		this.findViewById(R.id.btnCopy).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							AddLog("手动强制替换libauthjni_old和libauthjni文件", true);
							MainActivity.this.isRun = true;
							CopyLibauthjniFile();
							Restart12306();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							AddLog("ROOT权限失败", true);
							e1.printStackTrace();
						}
					}
				});

		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Start();
			}
		});

		stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Stop();
			}
		});

		this.findViewById(R.id.btnReplace).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MainActivity.this.isRun = true;
						AddLog("正在生成servcfg.txt文件", true);
						BulidServcfgFile();
						AddLog("生成servcfg.txt成功", true);
						try {
							FileCommonLib.CopyFile(FileCommonLib.GetSDRoot()
									+ "/fangbian/servcfg.txt",
									FileCommonLib.GetServcfgPath());

							AddLog("替换servcfg.txt成功", true);
							try {
								Runtime.getRuntime().exec(
										"chmod 666 "
												+ FileCommonLib.GetServcfgPath());
								AddLog("提升servcfg.txt权限成功", true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Restart12306();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
	}

	/**
	 * 销毁主界面后需要停止更新线程
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Stop();
		super.onDestroy();
	}

	/**
	 * 初始化
	 */
	private void Init() {
		String path = FileCommonLib.GetSDRoot() + "/fangbian/";
		File fi = new File(path);
		if (fi.exists() && fi.isFile()) {
			fi.delete();
		}

		path = FileCommonLib.GetSDRoot() + "/fangbian";
		fi = new File(path);
		if (!fi.exists()) {
			fi.mkdirs();
		}

		path = FileCommonLib.GetFilesDir(this) + "/smartvncserver";
		fi = new File(path);
		if (fi.exists() && fi.isDirectory()) {
			fi.delete();
		}

		fi = new File(path);

		if (!fi.exists()) {
			String tempFile = FileCommonLib.GetSDRoot()
					+ "/fangbian/smartvncserver";
			FileCommonLib.CopyBinary(R.raw.smartvncserver, tempFile, this);
			try {
				FileCommonLib.CopyFile(tempFile, path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		VNCManager vnc = new VNCManager();
		vnc.Start(this);

		if (!FileCommonLib.ExistFile(path)) {
			File from = new File(path);
			from.mkdir();
		}

		path = FileCommonLib.GetSDRoot() + "/fangbian/log/";
		if (!FileCommonLib.ExistFile(path)) {
			File from = new File(path);
			from.mkdir();
		}
		path = FileCommonLib.GetSDRoot() + "/fangbian/servcfg.txt";

		if (!FileCommonLib.ExistFile(path)) {
			try {
				BulidServcfgFile();
				FileCommonLib.CopyFile(FileCommonLib.GetSDRoot()
						+ "/fangbian/servcfg.txt", FileCommonLib.GetServcfgPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// 读取servcfg并且给值
			ArrayList<String> lines = new ArrayList<String>();
			try {
				InputStream instream = new FileInputStream(path);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						lines.add(line);
					}
					instream.close();

					EditText ip = (EditText) this.findViewById(R.id.tbIP);
					ip.setText(lines.get(0));
					EditText port1 = (EditText) this.findViewById(R.id.tpPort);
					port1.setText(lines.get(1));
					EditText port2 = (EditText) this.findViewById(R.id.tpPort2);
					port2.setText(lines.get(2));
				}
			} catch (java.io.FileNotFoundException e) {
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e) {
				Log.d("TestFile", e.getMessage());
			}
		}
	}

	/**
	 * 生成servcfg配置文件
	 */
	private void BulidServcfgFile() {
		String path = FileCommonLib.GetSDRoot() + "/fangbian/servcfg.txt";
		File from = new File(path);
		try {
			from.createNewFile();

			OutputStream outstream = new FileOutputStream(from);

			OutputStreamWriter out = new OutputStreamWriter(outstream);

			EditText ip = (EditText) this.findViewById(R.id.tbIP);
			EditText port1 = (EditText) this.findViewById(R.id.tpPort);
			EditText port2 = (EditText) this.findViewById(R.id.tpPort2);

			out.write(ip.getText().toString() + "\r\n");
			out.write(port1.getText().toString() + "\r\n");
			out.write(port1.getText().toString());
			out.flush();
			out.close();
			outstream.flush();
			outstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 复制替换Libauthjni文件（核心文件为）
	 * 
	 * @throws IOException
	 */
	private void CopyLibauthjniFile() throws IOException {

		try {
			Runtime.getRuntime().exec(
					"chmod 777 " + FileCommonLib.GetOldFileName());
			AddLog("提升libauthjni.so权限成功", true);

			AddLog("更新libauthjni.so文件完成", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileCommonLib.CopyFile(FileCommonLib.Getlibauthjni_oldName(),
				FileCommonLib.GetCopyFileName());
		AddLog("备份libauthjni.so文件副本到libauthjni_old.so完成", true);

		FileCommonLib.CopyFile(FileCommonLib.GetlibauthjniName(),
				FileCommonLib.GetOldFileName());

	}

	/**
	 * 添加日志不记录文件中
	 * @param log 日志信息
	 */
	public void AddLog(String log) {
		AddLog(log, false);
	}

	/**
	 * 添加日志
	 * @param log 日志信息
	 * @param isWrite 是否写入文件
	 */
	@SuppressLint("ResourceAsColor")
	public void AddLog(String log, boolean isWrite) {
		LinearLayout panel = (LinearLayout) this.findViewById(R.id.panel);
		if (count > 20) {
			count = 0;
			panel.removeAllViews();
		}
		count++;
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日    HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		TextView view = new TextView(this);
		view.setText(str + ":" + log);
		view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList csl = (ColorStateList) resource
				.getColorStateList(R.color.white);
		if (csl != null) {
			view.setTextColor(csl);
		}

		panel.addView(view);
		if (isWrite) {
			SystemLog.w("监控", log);
		}
	}

	/**
	 * 启动更新服务线程
	 */
	private void Start() {
		serverState.setText("服务状态:启动");
		AddLog("正在扫描安装包中的libauthjni文件");
		FileCommonLib.CopyBinary(R.raw.libauthjni, FileCommonLib.GetlibauthjniName(),
				this);
		FileCommonLib.CopyBinary(R.raw.libauthjni_old,
				FileCommonLib.Getlibauthjni_oldName(), this);
		AddLog("libauthjni文件替换成功");
		AddLog("执行服务");

		Restart12306();

		EditText text = (EditText) MainActivity.this
				.findViewById(R.id.editText1);
		updateThread.setSleepTime(Integer.parseInt(text.getText().toString()));
		updateThread.setRun(true);
		updateThread.setCurrentHander(excuteUpdateHandler);
		updateThread.Start();
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);
	}

	/**
	 * 停止更新服务线程
	 */
	private void Stop() {
		serverState.setText("服务状态:正在停止....");
		updateThread.setRun(false);
	}

	/**
	 * 根据不同的消息执行具体的方法
	 */
	public Handler excuteUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1102) {
				serverState.setText("服务状态:停止");
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}

			if (msg.what == 1103) {
				AddLog("正在启动12306程序");
				RunManager.Open12306(MainActivity.this);
				AddLog("启动12306程序完成");
			}
			if (msg.what == 1104) {
				RunManager.Close12306(MainActivity.this);
			}

			if (msg.what == 1101) {
				AddLog("匹配libauthjni.so文件大小"); 
				if (FileCompare.Compare() == false) {
					AddLog("当前出现文件不匹配", true);
					try {
						AddLog("正在替换文件", true);
						CopyLibauthjniFile();
						Restart12306();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						AddLog("ROOT权限失败", true);
						e1.printStackTrace();
					}
				} else {
					AddLog("文件大小一致");
					if (new ProcessManager().Is12306Process(MainActivity.this) == false) {
						AddLog("发现12306已经关闭，正在重启", true);
						Restart12306();
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 重启12306程序
	 */
	private void Restart12306() {
		AddLog("关闭12306程序", true);
		moveTaskToFront();
		Thread th = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (MainActivity.this.updateThread.isRun()
						|| MainActivity.this.isRun) {
					Message msg1 = new Message();
					msg1.what = 1104;
					MainActivity.this.excuteUpdateHandler.sendMessage(msg1);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (MainActivity.this.updateThread.isRun()
						|| MainActivity.this.isRun) {
					Message msg = new Message();
					msg.what = 1103;
					MainActivity.this.excuteUpdateHandler.sendMessage(msg);
				}
				MainActivity.this.isRun = false;
			}
		};

		th.start();

	}

	/**
	 * 判断一个进程是否在前段显示
	 * @param packageName
	 * @return
	 */
	private boolean IsForeground(String packageName) {
		ActivityManager.RunningAppProcessInfo processInfo = new ProcessManager()
				.GetProcess(packageName, this);
		if (processInfo != null) {
			return ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == processInfo.importance;
		}
		return false;
	}

	/**
	 * 将指定的进程显示到前端
	 * @return
	 */
	@SuppressLint("NewApi")
	private boolean moveTaskToFront() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT >= 11) {
			am.moveTaskToFront(getTaskId(), 0);
		} else {

		}
		return IsForeground(getPackageName());
	}
}
