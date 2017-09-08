package com.huahan.hhbaseutils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.huahan.hhbaseutils.imp.DownLoadListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 流操作的帮助类
 * 
 * @author yuan
 *
 */
public class HHStreamUtils {
	private static final String tag = HHStreamUtils.class.getName();

	/**
	 * 把流对象写入到文件中
	 * 
	 * @param stream
	 *            输入流
	 * @param savePath
	 *            文件的路径
	 * @param totalSize
	 *            文件的总大小
	 * @param listener
	 *            文件写入进度的监听器
	 * @throws Exception
	 *             文件不存在或者写入文件异常
	 */
	public static void writeStreamToFile(InputStream stream, String savePath,
			int totalSize, DownLoadListener listener) throws Exception {
		if (stream == null || TextUtils.isEmpty(savePath)) {
			return;
		}

		File file = new File(savePath);
		FileOutputStream fos = new FileOutputStream(file);
		int len = 0;
		int downloadSize = 0;
		byte[] buf = new byte[1024];
		while ((len = stream.read(buf)) != -1) {
			fos.write(buf, 0, len);
			if (listener != null && totalSize > 0) {
				downloadSize += len;
				listener.onSizeChangedListener((int) ((float) downloadSize
						/ totalSize * 100), downloadSize);
			}
		}
		fos.flush();
		fos.close();
		stream.close();
	}

	/**
	 * 将流对象写入到文件中
	 * 
	 * @param stream
	 *            流对象
	 * @param savePath
	 *            文件的保存路径
	 * @throws Exception
	 *             文件不存在或者写入文件异常
	 */
	public static void writeStreamToFile(InputStream stream, String savePath)
			throws Exception {
		writeStreamToFile(stream, savePath, 0, null);
	}

	/**
	 * 把流对象写入到文件中,下载完成自动删除临时文件
	 * 
	 * @param stream
	 *            流对象
	 * @param savePath
	 *            文件的保存路径
	 * @param totalSize
	 *            文件的总大小
	 * @param listener
	 *            完成的进度
	 * @throws Exception
	 */
	public static void writeStreamToRandomAccessFile(InputStream stream,
			String savePath, int totalSize, DownLoadListener listener)
			throws Exception {
		if (stream == null || TextUtils.isEmpty(savePath) || totalSize < 1) {
			return;
		}
		RandomAccessFile randomAccessFile = new RandomAccessFile(savePath,
				"rwd");
		randomAccessFile.setLength(totalSize);
		String tempFilePath = savePath + ".temp";
		File file = new File(tempFilePath);
		int downloadSize = 0;
		RandomAccessFile tempAccessFile = new RandomAccessFile(tempFilePath,
				"rwd");
		if (file.exists()) {
			if (tempAccessFile.length() > 0) {
				downloadSize = tempAccessFile.readInt();
			}
			randomAccessFile.seek(downloadSize);
		}
		if (listener != null) {
			listener.onSizeChangedListener((int) ((float) downloadSize
					/ totalSize * 100), downloadSize);
		}
		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = stream.read(buf)) != -1) {
			randomAccessFile.write(buf, 0, len);
			downloadSize += len;
			tempAccessFile.seek(0);
			tempAccessFile.writeInt(downloadSize);
			if (listener != null && totalSize > 0) {
				listener.onSizeChangedListener((int) ((float) downloadSize
						/ totalSize * 100), downloadSize);
			}
		}

		randomAccessFile.close();
		tempAccessFile.close();
		if (downloadSize == totalSize) {
			file.delete();
			HHLog.i(tag, "writeStreamToRandomAccessFile:delete file "
					+ tempFilePath);
		}

	}

	/**
	 * 把流对象转换成字符串
	 * 
	 * @param stream
	 *            流对象
	 * @return
	 * @throws IOException
	 *             转换失败的时候返回null
	 */
	public static String convertStreamToString(InputStream stream)
			throws IOException {
		if (stream != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = stream.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			bos.flush();
			stream.close();
			byte[] stringInfo = bos.toByteArray();
			return new String(stringInfo);
		}
		return null;
	}

	/**
	 * 获取网络的输入流
	 * 
	 * @param webUrl
	 *            网络地址
	 * @return 返回该网络地址对应的输入流，获取失败返回null
	 */
	public static InputStream getWebStream(String webUrl) {
		try {
			URL url = new URL(webUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			HHLog.e(tag, "getWebStream", e);
		}
		return null;
	}

	/**
	 * 从Assets文件夹中获取一个文件的输入流
	 * 
	 * @param context
	 *            上下文对象
	 * @param name
	 *            文件的名称
	 * @return 如果文件不存在或者其他原因获取失败返回null
	 */
	public static InputStream getInputStreamFromAssets(Context context,
			String name) {
		AssetManager manager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = manager.open(name);
		} catch (IOException e) {
			e.printStackTrace();
			HHLog.e(tag, "getInputStreamFromAssets", e);
		}
		return inputStream;
	}

	/**
	 * 获取Uri对应的InputStream
	 * 
	 * @param context
	 *            上下文对象
	 * @param uri
	 *            uri路径
	 * @return 获取输入流失败返回null
	 */
	public static InputStream getInputStreamFromUri(Context context, Uri uri) {
		ContentResolver resolver = context.getContentResolver();
		InputStream is = null;
		try {
			is = resolver.openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			HHLog.e(tag, "getInputStreamFromUri", e);
		}
		return is;
	}

	/**
	 * bitmap转换成字节
	 * 
	 * @param bitmap
	 * @param needRecycle
	 * @return
	 */
	public static byte[] convertBitmapToByteArray(final Bitmap bitmap,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bitmap.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获取bitmap字节大小
	 * @param bitmap
	 * @return
	 */
	public static long getBitmapByteSize(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		HHLog.i(tag, "getBitmapByteSize=="+bitmap.getRowBytes());
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}
