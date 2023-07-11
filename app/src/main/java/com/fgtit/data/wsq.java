package com.fgtit.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class wsq {
	private static wsq mCom=null;
	
	public static wsq getInstance(){
		if(mCom==null){
			mCom=new wsq();
		}
		return mCom;
	}
	
	public native int RawToWsq(byte[] inpdata,int inpsize,int width,int height,byte[] outdata,int[] outsize,float bitrate);
	public native int WsqToRaw(byte[] inpdata,int inpsize,byte[] outdata,int[] outsize);
	
	static {
		System.loadLibrary("wsq");
	}


	public void SaveWsqFile(byte[] rawdata, int rawsize, String filename) {
		byte[] outdata = new byte[73728];
		int[] outsize = new int[1];
		wsq.getInstance().RawToWsq(rawdata, rawsize, 256, 288, outdata, outsize, 2.833755f);
		try {
			File fs = new File("/sdcard/" + filename);
			if (fs.exists()) {
				fs.delete();
			}
			new File("/sdcard/" + filename);
			RandomAccessFile randomFile = new RandomAccessFile("/sdcard/" + filename, "rw");
			long fileLength = randomFile.length();
			randomFile.seek(fileLength);
			randomFile.write(outdata, 0, outsize[0]);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
