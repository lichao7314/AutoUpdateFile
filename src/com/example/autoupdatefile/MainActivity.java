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
			MessageDialog.Alert("��ǰ����û��rootȨ�ޣ�������rootȨ�޺���������", this);
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
							AddLog("�ֶ�ǿ���滻libauthjni_old��libauthjni�ļ�", true);
							MainActivity.this.isRun = true;
							CopyLibauthjniFile();
							Restart12306();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							AddLog("ROOTȨ��ʧ��", true);
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
						AddLog("��������servcfg.txt�ļ�", true);
						BulidServcfgFile();
						AddLog("����servcfg.txt�ɹ�", true);
						try {
							FileCommonLib.CopyFile(FileCommonLib.GetSDRoot()
									+ "/fangbian/servcfg.txt",
									FileCommonLib.GetServcfgPath());

							AddLog("�滻servcfg.txt�ɹ�", true);
							try {
								Runtime.getRuntime().exec(
										"chmod 666 "
												+ FileCommonLib.GetServcfgPath());
								AddLog("����servcfg.txtȨ�޳ɹ�", true);
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
	 * �������������Ҫֹͣ�����߳�
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Stop();
		super.onDestroy();
	}

	/**
	 * ��ʼ��
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
			// ��ȡservcfg���Ҹ�ֵ
			ArrayList<String> lines = new ArrayList<String>();
			try {
				InputStream instream = new FileInputStream(path);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// ���ж�ȡ
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
	 * ����servcfg�����ļ�
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
	 * �����滻Libauthjni�ļ��������ļ�Ϊ��
	 * 
	 * @throws IOException
	 */
	private void CopyLibauthjniFile() throws IOException {

		try {
			Runtime.getRuntime().exec(
					"chmod 777 " + FileCommonLib.GetOldFileName());
			AddLog("����libauthjni.soȨ�޳ɹ�", true);

			AddLog("����libauthjni.so�ļ����", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileCommonLib.CopyFile(FileCommonLib.Getlibauthjni_oldName(),
				FileCommonLib.GetCopyFileName());
		AddLog("����libauthjni.so�ļ�������libauthjni_old.so���", true);

		FileCommonLib.CopyFile(FileCommonLib.GetlibauthjniName(),
				FileCommonLib.GetOldFileName());

	}

	/**
	 * �����־����¼�ļ���
	 * @param log ��־��Ϣ
	 */
	public void AddLog(String log) {
		AddLog(log, false);
	}

	/**
	 * �����־
	 * @param log ��־��Ϣ
	 * @param isWrite �Ƿ�д���ļ�
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
				"yyyy��MM��dd��    HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
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
			SystemLog.w("���", log);
		}
	}

	/**
	 * �������·����߳�
	 */
	private void Start() {
		serverState.setText("����״̬:����");
		AddLog("����ɨ�谲װ���е�libauthjni�ļ�");
		FileCommonLib.CopyBinary(R.raw.libauthjni, FileCommonLib.GetlibauthjniName(),
				this);
		FileCommonLib.CopyBinary(R.raw.libauthjni_old,
				FileCommonLib.Getlibauthjni_oldName(), this);
		AddLog("libauthjni�ļ��滻�ɹ�");
		AddLog("ִ�з���");

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
	 * ֹͣ���·����߳�
	 */
	private void Stop() {
		serverState.setText("����״̬:����ֹͣ....");
		updateThread.setRun(false);
	}

	/**
	 * ���ݲ�ͬ����Ϣִ�о���ķ���
	 */
	public Handler excuteUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1102) {
				serverState.setText("����״̬:ֹͣ");
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}

			if (msg.what == 1103) {
				AddLog("��������12306����");
				RunManager.Open12306(MainActivity.this);
				AddLog("����12306�������");
			}
			if (msg.what == 1104) {
				RunManager.Close12306(MainActivity.this);
			}

			if (msg.what == 1101) {
				AddLog("ƥ��libauthjni.so�ļ���С"); 
				if (FileCompare.Compare() == false) {
					AddLog("��ǰ�����ļ���ƥ��", true);
					try {
						AddLog("�����滻�ļ�", true);
						CopyLibauthjniFile();
						Restart12306();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						AddLog("ROOTȨ��ʧ��", true);
						e1.printStackTrace();
					}
				} else {
					AddLog("�ļ���Сһ��");
					if (new ProcessManager().Is12306Process(MainActivity.this) == false) {
						AddLog("����12306�Ѿ��رգ���������", true);
						Restart12306();
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * ����12306����
	 */
	private void Restart12306() {
		AddLog("�ر�12306����", true);
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
	 * �ж�һ�������Ƿ���ǰ����ʾ
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
	 * ��ָ���Ľ�����ʾ��ǰ��
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
