package com.example.autoupdatefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileCompare 
{ 
	public boolean Compare()  
	{  
		String oldFile=FileCommon.GetOldFileName();
		Log.v("file", oldFile);
		
		int oldFileSize=0;
		try {
			oldFileSize = FileCommon.GetFileSize(oldFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int newFileSize=0;
		try {
			newFileSize = FileCommon.GetFileSize(FileCommon.GetlibauthjniName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return oldFileSize==newFileSize;
	} 
}
