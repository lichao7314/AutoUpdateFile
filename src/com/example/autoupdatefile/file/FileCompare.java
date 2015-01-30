package com.example.autoupdatefile.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

/**
 * 匹配文件是否大小
 * @author lichao
 *
 */
public class FileCompare 
{ 
	public static boolean Compare()  
	{  
		String oldFile=FileCommonLib.GetOldFileName();
		Log.v("file", oldFile);
		
		int oldFileSize=0;
		try {
			oldFileSize = FileCommonLib.GetFileSize(oldFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int newFileSize=0;
		try {
			newFileSize = FileCommonLib.GetFileSize(FileCommonLib.GetlibauthjniName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return oldFileSize==newFileSize;
	} 
}
