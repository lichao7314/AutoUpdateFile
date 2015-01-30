package com.example.autoupdatefile.vnc;

import java.io.IOException;
import java.io.OutputStream;

import com.example.autoupdatefile.file.FileCommonLib;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class VNCManager {
	public void Start(Context a) {
		Process sh;
		String password = "fangbian";
		String password_check = "";
		if (!password.equals(""))
			password_check = "-p " + password;

		String rotation = "0";
		rotation = "-r " + rotation;

		String scaling = "100";

		String scaling_string = "";
		if (!scaling.equals("0"))
			scaling_string = "-s " + scaling;

		String donate = " -d ";

		String port = "5900";
		try {
			int port1 = Integer.parseInt(port);
			port = String.valueOf(port1);
		} catch (NumberFormatException e) {
			port = "5900";
		}
		String port_string = "-P " + port;

		try { 
			sh = Runtime.getRuntime().exec("su");

			OutputStream os = sh.getOutputStream();

			String path = FileCommonLib.GetFilesDir(a) + "/smartvncserver"; 
			
			Process value = writeCommand(os, "chmod 777 " + path);

			value = writeCommand(os, path + " -r 0 -s 100 -P 5900 ");
			os.close();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("smartvncserver", "Æô¶¯vnc");
	}

	public static Process writeCommand(OutputStream os, String command)
			throws Exception {
		Process sh = null;
		if (os != null)
			os.write((command + "\n").getBytes("ASCII"));
		else
			sh = Runtime.getRuntime().exec(command);
		return sh;
	}
}
