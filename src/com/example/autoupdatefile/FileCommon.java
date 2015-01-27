package com.example.autoupdatefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCommon {

	/**
	 * 获取历史就文件的文件名
	 * 
	 * @return
	 */
	public static String GetOldFileName() {
		String dataRoot = Environment.getDataDirectory().getAbsolutePath();

		for (int i = 1; i <= 10; i++) {
			dataRoot = dataRoot + "/app-lib/com.MobileTicket-" + i + "/";
			if (FileCommon.ExistFile(dataRoot)) {
				return dataRoot + "libauthjni.so";
			}
		}
		// /data/app-lib/com.MobileTicket-1

		return dataRoot + "/data/com.MobileTicket/lib/libauthjni.so";
	}

	/**
	 * 获取复制文件夹的文件名
	 * 
	 * @return
	 */
	public static String GetCopyFileName() {
		String dataRoot = Environment.getDataDirectory().getAbsolutePath();

		for (int i = 1; i <= 10; i++) {
			dataRoot = dataRoot + "/app-lib/com.MobileTicket-" + i + "/";
			if (FileCommon.ExistFile(dataRoot)) {
				return dataRoot + "libauthjni_old.so";
			}
		}
		return dataRoot + "/data/com.MobileTicket/lib/libauthjni_old.so";

	}

	/**
	 * 获取新文件的文件名
	 * 
	 * @return
	 */
	public static String GetlibauthjniName() {
		String dataRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		Log.v("file", dataRoot);

		String dataFilePath = dataRoot + "/fangbian/libauthjni.so";
		Log.v("file", dataFilePath);
		return dataFilePath;
	}

	/**
	 * 获取新文件的文件名
	 * 
	 * @return
	 */
	public static String Getlibauthjni_oldName() {
		String dataRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		Log.v("file", dataRoot);

		String dataFilePath = dataRoot + "/fangbian/libauthjni_old.so";
		Log.v("file", dataFilePath);
		return dataFilePath;
	}

	/**
	 * 获取SD卡根目录
	 * 
	 * @return
	 */
	public static String GetSDRoot() {

		String dataRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		return dataRoot;
	}
	
	/**
	 * 获取SD卡根目录
	 * 
	 * @return
	 */
	public static String GetDataRoot() {

		String dataRoot = Environment.getDataDirectory().getAbsolutePath();

		return dataRoot;
	}
	
	public static String GetFilesDir(Context context){
		return context.getFilesDir().getAbsolutePath();
	}

	/**
	 * 存储文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean ExistFile(String path) {
		File from = new File(path);
		boolean value = from.exists();
		return value;
	}

	/**
	 * 获取配置文件的地址
	 * 
	 * @return
	 */
	public static String GetServcfgPath() {
		String dataRoot = Environment.getDataDirectory().getAbsolutePath();
		return dataRoot + "/data/com.MobileTicket/files/servcfg.txt";
	}

	/**
	 * 获取文件大小
	 * 
	 * @param Path
	 * @return
	 * @throws IOException
	 */
	public static int GetFileSize(String Path) throws IOException {
		File dataFile = new File(Path);
		if (dataFile.exists()) {
			FileInputStream fis = new FileInputStream(dataFile);
			return fis.available();
		}
		return 0;
	}

	/**
	 * 复制文件
	 * 
	 * @param fromFile
	 * @param toFile
	 * @return
	 * @throws IOException
	 */
	public static int CopyFile(String fromFile, String toFile)
			throws IOException {

		File from = new File(fromFile);
		if (!from.exists()) {
			return 1;
		}
		Runtime.getRuntime().exec("su");
		Runtime.getRuntime().exec("adb push " + fromFile + " " + toFile);
		return 1;
	}
}
