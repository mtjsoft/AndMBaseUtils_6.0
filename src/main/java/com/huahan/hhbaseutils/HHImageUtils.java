package com.huahan.hhbaseutils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.HHImageDecorator;
import com.huahan.hhbaseutils.imp.LoadImageListener;
import com.huahan.hhbaseutils.model.HHBitmapDrawable;
import com.huahan.hhbaseutils.model.HHCompressResult;
import com.huahan.hhbaseutils.model.HHImageParam;
import com.huahan.hhbaseutils.task.HHImageLoadTask;

import java.io.File;

/**
 * 图片的加载类<br/>
 * 加载图片的时候，调用loadImage方法时，如果图片的大小是确定的，即设置了图片确切的显示的宽度和高度，
 * 可以直接调用没有widthDes和heightDes的方法，
 * 在加载图片的方法内部可以获取图片的大小自动进行缩放来获取合适的Bitmap；如果图片没有设置确定的大小
 * ，则建议调用带有widthDes和heightDes参数的
 * 方法来加载图片，以获取大小合适的图片，如果没有调用带有widthDes和heightDes的方法
 * ，则默认设置图片的大小为200dp，以200dp的大小来加载图片
 * 
 * @author yuan
 *
 */
public class HHImageUtils {
	private static final String tag = HHImageUtils.class.getName();
	/**
	 * 默认的缓存地址
	 */
	private static final String DEFAULT_CACHE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/huahan/cacheImage/";
	private static final int DEFAULT_LOAD_IMAGE_SIZE = 200;
	/**
	 * 默认图片的大小
	 */
	private static int DEFAULT_IMAGE_SIZE = 0;
	/**
	 * HHImageUtils的实例
	 */
	private static HHImageUtils imageUtils;
	/**
	 * 缓存的路径
	 */
	private static String baseCacheDir = null;
	/**
	 * 保存着图片在内存中的缓存
	 */
	private HHImageCache memoryCache;
	/**
	 * 管理图片的文件缓存
	 */
	private HHFileCache fileCache;

	/**
	 * 私有化构造函数
	 */
	private HHImageUtils() {
	}

	/**
	 * 获取HHImageUtils的实例
	 * 
	 * @param cacheDir
	 *            图片缓存的本地文件的路径
	 * @return
	 */
	public static HHImageUtils getInstance(String cacheDir) {
		synchronized (HHImageUtils.class) {
			if (imageUtils == null) {
				imageUtils = new HHImageUtils();
				imageUtils.init();
			}
		}
		if (TextUtils.isEmpty(baseCacheDir) && !TextUtils.isEmpty(cacheDir)) {
			baseCacheDir = cacheDir;
		}
		if (!TextUtils.isEmpty(baseCacheDir)) {
			File file = new File(baseCacheDir);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return imageUtils;
	}

	/**
	 * 执行初始化
	 */
	private void init() {
		memoryCache = HHImageCache.getInstance(0);
		fileCache = HHFileCache.getInstance(imageUtils, 0);
	}

	/**
	 * 加载本地代码库
	 */
	static {
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");

	}

	public Bitmap getBitmap(HHImageParam param) {
		// 文件是一个网络文件
		Bitmap bitmap = null;
		if (HHFormatUtils.isHttpUrl(param.filePath)) {
			// 判断文件时候存在，如果文件不存在的话就下载该文件，然后在加载该文件
			String fileTempName = param.filePath.hashCode() + "";
			File file = new File(baseCacheDir, fileTempName);
			if (!file.exists()) {
				// 下载两次：如果下载一次失败，则继续第二次的下载
				boolean download = HHDownLoadHelper.download(
						param.filePath,
						TextUtils.isEmpty(param.savePath) ? file
								.getAbsolutePath() : param.savePath,
						param.listener);
				if (!download) {
					download = HHDownLoadHelper.download(param.filePath,
							file.getAbsolutePath(), param.listener);
				}
			}
			if (file.exists()) {
				bitmap = fileCache.getBitmapFromFile(param.filePath + "_"
						+ param.widthDes + "_" + param.heightDes, TextUtils
						.isEmpty(param.savePath) ? file.getAbsolutePath()
						: param.savePath, param.widthDes, param.heightDes,
						true, false, param.cacheToMemory, param.imageDecorator);
				if (!param.isUseLocalFile) {
					file.delete();
				}
			}
			// 1:先判断该图片是否允许使用本地文件

		} else {
			// 文件时本地文件的话直接加载
			bitmap = fileCache.getBitmapFromFile(param.filePath + "_"
					+ param.widthDes + "_" + param.heightDes, param.filePath,
					param.widthDes, param.heightDes, true, false,
					param.cacheToMemory, param.imageDecorator);
		}
		return bitmap;
	}

	/**
	 * 本地方法，用于压缩图片文件
	 * 
	 * @param bit
	 *            需要压缩的Bitmap对象
	 * @param w
	 *            压缩后期望的图片的宽度
	 * @param h
	 *            压缩后期望的图片的高度
	 * @param quality
	 *            压缩的图片的质量，对文件的大小影响较大
	 * @param fileNameBytes
	 *            文件的路径的字节数组
	 * @param optimize
	 * @return
	 */
	private static native String compressBitmap(Bitmap bit, int w, int h,
			int quality, byte[] fileNameBytes, boolean optimize);

	/**
	 * 压缩图片到文件中。压缩后的图片的高度或者宽度都限制在最大的宽度和高度之内。图片的质量对文件的大小影响较大，根据情况选择合适的质量<br/>
	 * option参数为true，方法会返回压缩后的bitmap，如果为false，则返回null，并且会执行bitmap的recycle方法<br/>
	 * 需要注意的是sourceBitmap的回收，如果sourceBitmap在压缩完成后没有存在的必要，
	 * 需要执行Bitmap的recycle方法来回收bitmap占用的内存
	 * 
	 * @param sourceBitmap
	 *            原始Bitmap
	 * @param widthDes
	 *            最大宽度
	 * @param heightDes
	 *            最大高度
	 * @param savePath
	 *            压缩文件的保存路径
	 * @param quality
	 *            生成的图片的质量
	 * @param option
	 *            是否返回压缩的bitmap
	 * @return 压缩后的Bitmap
	 */
	public static HHCompressResult compressBitmap(Bitmap sourceBitmap,
			int widthDes, int heightDes, String savePath, int quality,
			boolean option) {
		HHCompressResult compressResult = new HHCompressResult();
		if (sourceBitmap != null) {
			int bitmapWidth = sourceBitmap.getWidth();
			int bitmapHeight = sourceBitmap.getHeight();
			float scaleX = (float) bitmapHeight / heightDes;
			float scaleY = (float) bitmapWidth / widthDes;
			Bitmap result = null;
			float scale = 1.0f;
			if (scaleX >= scaleY && scaleX > 1) {
				scale = scaleX;
			} else if (scaleX < scaleY && scaleY > 1) {
				scale = scaleY;
			} else {
				result = sourceBitmap;
			}
			try {
				if (scale != 1.0f) {
					int width = (int) (bitmapWidth / scale);
					int height = (int) (bitmapHeight / scale);
					result = Bitmap.createBitmap(width, height,
							Config.ARGB_8888);
					Rect rect = new Rect(0, 0, width, height);
					Canvas canvas = new Canvas(result);
					canvas.drawBitmap(sourceBitmap, null, rect, null);
				}
				compressBitmap(result, result.getWidth(), result.getHeight(),
						quality, savePath.getBytes(), true);
				compressResult.compressSuccess = true;
				if (option) {
					compressResult.resultBitmap = result;
				} else {
					result.recycle();
					result = null;
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HHLog.i(tag, "compressBitmap", e);
			}

		}
		return compressResult;
	}

	/**
	 * 压缩图片文件
	 * 
	 * @param sourcePath
	 *            原始图片的地址
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param savePath
	 *            图片的保存地址
	 * @param quality
	 *            图片的质量
	 * @return true压缩成功，false压缩失败
	 */
	public boolean compressImageFile(String sourcePath, int widthDes,
			int heightDes, String savePath, int quality) {
		Bitmap sourceBitmap = fileCache.getBitmapFromFile(sourcePath,
				sourcePath, (int) (widthDes * 1.5), (int) (heightDes * 1.5),
				true, true, true, null);
		boolean flag = false;
		if (sourceBitmap != null) {
			flag = compressBitmap(sourceBitmap, widthDes, heightDes, savePath,
					quality, false).compressSuccess;
			sourceBitmap.recycle();
			sourceBitmap = null;
		}
		return flag;
	}

	/**
	 * 压缩Bitmap到文件中，默认的质量是70
	 * 
	 * @param sourceBitmap
	 *            需要压缩的Bitmap
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param savePath
	 *            文件的保存位置
	 * @param option
	 *            是否返回压缩后的Bitmap
	 * @return
	 */
	public static HHCompressResult compressBitmap(Bitmap sourceBitmap,
			int widthDes, int heightDes, String savePath, boolean option) {
		return compressBitmap(sourceBitmap, widthDes, heightDes, savePath, 70,
				option);
	}

	/**
	 * 计算图片的缩放比例.通过该方法计算的比例获取出来的图片的大小在期望的图片的大小周围浮动<br/>
	 * 如果isStick为true，则严格遵循缩放后的宽高尽量接近期望的宽高，但是不小于期望的宽高。<br/>
	 * 如果isStick为false，则设置阙值为.8,当缩放的期望值大于阙值时，则返回大于阙值的最小整数，否则返回严格遵循的值。<br/>
	 * 例如一个图片的大小是600*600，现在期望的图片的大小是301*301,如果严格遵循放缩原则，此时是不需要缩放的；如果不严格遵循<br/>
	 * 缩放原则，则此时返回的缩放比例是2，此时缩放图片时获取的实际的图片的大小是300*300
	 * 
	 * @param bitmapWidth
	 *            Bitmap的宽度
	 * @param bitmapHeight
	 *            Bitmap的高度
	 * @param widthDes
	 *            期望的图片的宽度
	 * @param heightDes
	 *            期望的图片的高度
	 * @param isStick
	 *            是否严格的遵循缩放后的宽高必须接近期望的宽高，且不小于期望的宽高
	 * @return 图片应该缩放的值
	 */
	public int calculateSampleSize(int bitmapWidth, int bitmapHeight,
			int widthDes, int heightDes, boolean isStick) {
		float scaleX = (float) bitmapWidth / widthDes;
		float scaleY = (float) bitmapHeight / heightDes;
		float scale = 1.0f;
		if (scaleX >= scaleY && scaleY > 1) {
			scale = scaleY;
		} else if (scaleX < scaleY && scaleX > 1) {
			scale = scaleX;
		}
		HHLog.i(tag, "calculateSampleSize:scale value is " + scale);
		return isStick ? (int) scale : (scale - (int) scale > 0.8f ? Math
				.round(scale) : (int) scale);
	}

	/**
	 * 计算图片的缩放比例.通过该方法计算的比例获取出来的图片的大小在期望的图片的大小周围浮动<br/>
	 * 如果isStick为true，则严格遵循缩放后的宽高尽量接近期望的宽高，但是不小于期望的宽高。<br/>
	 * 如果isStick为false，则设置阙值为.8,当缩放的期望值大于阙值时，则返回大于阙值的最小整数，否则返回严格遵循的值。<br/>
	 * 例如一个图片的大小是600*600，现在期望的图片的大小是301*301,如果严格遵循放缩原则，此时是不需要缩放的；如果不严格遵循<br/>
	 * 缩放原则，则此时返回的缩放比例是2，此时缩放图片时获取的实际的图片的大小是300*300
	 * 
	 * @param opts
	 *            Bitmap的参数
	 * @param widthDes
	 *            期望的图片的宽度
	 * @param heightDes
	 *            期望的图片的高度
	 * @param isStick
	 *            是否严格的遵循缩放后的宽高必须接近期望的宽高，且不小于期望的宽高
	 * @return 图片应该缩放的值
	 */
	public int calculateSampleSize(Options opts, int widthDes, int heightDes,
			boolean isStick) {
		if (opts != null) {
			return calculateSampleSize(opts.outWidth, opts.outHeight, widthDes,
					heightDes, isStick);
		}
		return 1;
	}

	/**
	 * 计算bitmap加载到内存中是将要占用的内存的大小
	 * 
	 * @param bitmapWidth
	 *            bitmap的宽度
	 * @param bitmapHeight
	 *            bitmap的高度
	 * @param config
	 *            bitmap的颜色宽度
	 * @return
	 */
	public int getBitmapMemorySize(int bitmapWidth, int bitmapHeight,
			Config config) {
		int size = 0;
		switch (config) {
		case ALPHA_8:
			size = bitmapWidth * bitmapHeight;
			break;
		case ARGB_4444:
			size = bitmapWidth * bitmapHeight * 2;
			break;
		case ARGB_8888:
			size = bitmapWidth * bitmapHeight * 4;
			break;
		case RGB_565:
			size = bitmapWidth * bitmapHeight * 2;
			break;
		default:
			size = bitmapWidth * bitmapHeight;
			break;
		}
		return size;
	}

	/**
	 * 清除内存缓存
	 */
	public void clearCache() {
		memoryCache.clear();
	}

	/**
	 * 把Bitmap添加到内存的缓存中
	 * 
	 * @param key
	 *            bitmap对应的key
	 * @param value
	 *            缓存的Bitmap
	 */
	public void putBitmapToMemoryCache(String key, Bitmap value) {
		memoryCache.put(key, value);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 */
	public void loadImage(int defaultImage, String filePath, ImageView imageView) {
		loadImage(defaultImage, filePath, null, imageView, 0, 0, true, true, 0,
				0, null, true);
	}


	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, boolean loadImageNotWifi) {
		loadImage(defaultImage, filePath, null, imageView, 0, 0, true, true, 0,
				0, null, loadImageNotWifi);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param savePath
	 *            图片的保存路径
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 */
	public void loadImage(int defaultImage, String filePath, String savePath,
			ImageView imageView) {
		loadImage(defaultImage, filePath, savePath, imageView, 0, 0, true,
				true, 0, 0, null, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param savePath
	 *            图片的保存路径
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 */
	public void loadImage(int defaultImage, String filePath, String savePath,
			ImageView imageView, boolean loadImageNotWifi) {
		loadImage(defaultImage, filePath, savePath, imageView, 0, 0, true,
				true, 0, 0, null, loadImageNotWifi);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param savePath
	 *            图片的保存路径
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param listener
	 *            加载图片的监听器
	 */
	public void loadImage(int defaultImage, String filePath, String savePath,
			ImageView imageView, LoadImageListener listener) {
		loadImage(defaultImage, filePath, savePath, imageView, 0, 0, true,
				true, 0, 0, listener, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param isUseLocalFile
	 *            是否使用本地缓存：如果是false则不使用本地缓存，同时获取到的图片也不会缓存到本地文件
	 * @param loadImageNotWifi
	 *            在不是wifi的时候是否加载图片
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, boolean isUseLocalFile,
			boolean loadImageNotWifi) {
		loadImage(defaultImage, filePath, null, imageView, 0, 0, true,
				isUseLocalFile, 0, 0, null, loadImageNotWifi);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param isUseLocalFile
	 *            是否使用本地缓存：如果是false则不使用本地缓存，同时获取到的图片也不会缓存到本地文件
	 * @param listener
	 *            加载图片的监听器
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, boolean isUseLocalFile,
			LoadImageListener listener) {
		loadImage(defaultImage, filePath, null, imageView, 0, 0, true,
				isUseLocalFile, 0, 0, listener, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            图片显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param listener
	 *            加载图片的监听器
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, LoadImageListener listener) {
		loadImage(defaultImage, filePath, null, imageView, 0, 0, true, true, 0,
				0, listener, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, int widthDes, int heightDes) {
		loadImage(defaultImage, filePath, null, imageView, widthDes, heightDes,
				true, true, 0, 0, null, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param loadImageNotWifi
	 *            在不是wifi的时候是否加载图片
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, int widthDes, int heightDes,
			boolean loadImageNotWifi) {
		loadImage(defaultImage, filePath, null, imageView, widthDes, heightDes,
				true, true, 0, 0, null, loadImageNotWifi);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param listener
	 *            加载图片的监听器
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, int widthDes, int heightDes,
			LoadImageListener listener) {
		loadImage(defaultImage, filePath, null, imageView, widthDes, heightDes,
				true, true, 0, 0, listener, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param defaultImage
	 *            显示的默认图片
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param listener
	 *            加载图片的监听器
	 * @param loadImageNotWifi
	 *            在不是wifi的时候是否加载图片
	 */
	public void loadImage(int defaultImage, String filePath,
			ImageView imageView, int widthDes, int heightDes,
			LoadImageListener listener, boolean loadImageNotWifi) {
		loadImage(defaultImage, filePath, null, imageView, widthDes, heightDes,
				true, true, 0, 0, listener, loadImageNotWifi);
	}

	/**
	 * 加载图片<br/>
	 * 如果需要显示的图片的大小根据图片显示空间的大小自动调整缩放的大小，则期望的宽或高其中一个传0即可
	 * 
	 * @param defaultImage
	 *            显示的默认图片
	 * @param filePath
	 *            文件的路径，可以使本地路径也可以是网络路径
	 * @param savePath
	 *            图片的保存路径
	 * @param imageView
	 *            显示图片的ImageView
	 * @param widthDes
	 *            期望的宽度
	 * @param heightDes
	 *            期望的高度
	 * @param cacheToMemory
	 *            图片加载完成的时候时候需要把图片缓存到内存中以提高下次的加载速度
	 * @param isUseLocalFile
	 *            加载网络图片的时候是否使用本地缓存
	 * @param showAnim
	 *            图片加载出来的时候显示的动画效果(暂时没有效果)
	 * @param imageEffect
	 *            图片显示的特效，例如黑白等(暂时没有效果)
	 * @param listener
	 *            加载图片的监听器，主要用于加载网络图片
	 */
	public void loadImage(int defaultImage, String filePath, String savePath,
			ImageView imageView, int widthDes, int heightDes,
			boolean cacheToMemory, boolean isUseLocalFile, int showAnim,
			int imageEffect, LoadImageListener listener,
			boolean loadImageNotWifi) {
		// 1：先判断参数文件的路径的是否是合法的，否则直接退出当前的方法
		// 2:从内存的缓存中去图片，如果图片存在，则直接显示
		// 3:判断当前有没有异步任务在加载当前的图片，如果没有则开启一个线程加载图片；如果有判断加载的图片
		// 3:开启线程从文件或者网络中获取需要显示的图片
		HHImageParam imageParam = new HHImageParam(filePath, savePath,
				widthDes, heightDes, cacheToMemory, isUseLocalFile, showAnim,
				imageEffect, listener);
		imageParam.defaultImageID = defaultImage;
		imageParam.loadImageNotWifi = loadImageNotWifi;
		loadImage(imageView, imageParam);
	}

	private void initSavePath() {
		if (TextUtils.isEmpty(baseCacheDir)) {
			baseCacheDir = HHConstantParam.DEFAULT_CACHE_IMAGE;
		}
		File file = new File(baseCacheDir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 如果不设置 获取图片大小，默认设置图片的大小
	 * 
	 * @return
	 */
	protected int getLoadImageSize() {
		return DEFAULT_IMAGE_SIZE;
	}

	private String getImageCacheKey(HHImageParam param) {
		StringBuilder builder = new StringBuilder();
		builder.append(param.filePath);
		builder.append("_");
		builder.append(param.widthDes);
		builder.append("_");
		builder.append(param.heightDes);
		if (param.imageDecorator != null) {
			builder.append("_");
			builder.append(param.imageDecorator.getClass().getSimpleName());
		}
		return builder.toString();
	}

	public void loadImage(ImageView imageView, HHImageParam imageParam) {
		// 1：先判断参数文件的路径的是否是合法的，否则直接退出当前的方法
		// 2:从内存的缓存中去图片，如果图片存在，则直接显示
		// 3:判断当前有没有异步任务在加载当前的图片，如果没有则开启一个线程加载图片；如果有判断加载的图片
		// 3:开启线程从文件或者网络中获取需要显示的图片
		if (DEFAULT_IMAGE_SIZE < 1) {
			DEFAULT_IMAGE_SIZE = HHDensityUtils.dip2px(imageView.getContext(),
					DEFAULT_LOAD_IMAGE_SIZE);
		}
		Bitmap defaultBitmap = memoryCache.getDefaultImage(
				imageView.getContext(), imageParam.defaultImageID);
		if (!TextUtils.isEmpty(imageParam.filePath)) {
			if (imageParam.loadImageNotWifi
					|| HHNetWorkUtils.isWifiConnected(imageView.getContext())) {
				Bitmap bitmap = memoryCache
						.getBitmap(getImageCacheKey(imageParam));
				if (bitmap == null) {
					if (cancelPotentialWork(imageParam.filePath, imageView,
							imageParam)) {
						initSavePath();
						HHImageLoadTask task = new HHImageLoadTask(this,
								imageView, imageParam);
						HHBitmapDrawable drawable = new HHBitmapDrawable(
								imageView.getContext().getResources(),
								defaultBitmap, task);
						imageView.setImageDrawable(drawable);
						imageView.setTag(task);
						task.execute();
					}
				} else {
					Log.i(tag,
							"loadImage:filePath:" + imageParam.filePath
									+ ",bitmap:" + bitmap + ",width"
									+ bitmap.getWidth());
					if (imageParam.listener != null) {
						imageParam.listener.onGetBitmap(bitmap);
					} else {
						imageView.setImageBitmap(bitmap);
					}
				}

			} else if (defaultBitmap != null) {
				imageView.setImageBitmap(defaultBitmap);
			}
		} else if (defaultBitmap != null) {
			imageView.setImageBitmap(defaultBitmap);
			HHLog.i(tag, "loadImage:filePath is empty or null");
		}
	}

	/**
	 * 获取ImageView绑定的异步任务，
	 * 
	 * @param imageView
	 * @return 如果ImageView已经绑定了异步任务，则返回绑定的异步任务，否则返回null
	 */
	public HHImageLoadTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof HHBitmapDrawable) {
				final HHBitmapDrawable asyncDrawable = (HHBitmapDrawable) drawable;
				return asyncDrawable.getImageLoadTask();
			} else {
				Object task = imageView.getTag();
				HHImageLoadTask loadTask = null;
				if (task != null && task instanceof HHImageLoadTask) {
					loadTask = (HHImageLoadTask) task;
					return loadTask;
				}
			}
		}
		return null;
	}

	/**
	 * 取消重复的任务
	 * 
	 * @param filePath
	 *            图片的路径
	 * @param imageView
	 *            显示图片的ImageView
	 * @return true，需要重新开启一个新的任务，false不需要开启新的任务
	 */
	private boolean cancelPotentialWork(String filePath, ImageView imageView,
			HHImageParam imageParam) {
		final HHImageLoadTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.getFilePath();
			// 如果没有设置ImageView显示的图片的数据或者设置的显示的图片和当前需要显示的图片的路径不一致
			if (!imageParam.isUseLocalFile) {
				bitmapWorkerTask.cancel(true);
				return true;
			}
			if (TextUtils.isEmpty(bitmapData) || !filePath.equals(bitmapData)) {
				// 取消前边的任务
				bitmapWorkerTask.cancel(true);
			} else {
				// 存在相同的任务，不需要再重新开启新的任务
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	public static class Builder {
		private HHImageParam mImageParam;
		private ImageView mLoadImageView;

		/**
		 * 创建一个Builder对象
		 * 
		 * @param imageView
		 *            显示图片
		 * @param path
		 *            路径
		 */
		public Builder(ImageView imageView, String path) {
			mImageParam = new HHImageParam();
			this.mLoadImageView = imageView;
			mImageParam.filePath = path;
		}

		/**
		 * 获取一个新的实例
		 * 
		 * @param imageView
		 * @param path
		 * @return
		 */
		public static Builder getNewInstance(ImageView imageView, String path) {
			return new Builder(imageView, path);
		}

		/**
		 * 设置图片的修饰器
		 * 
		 * @param imageDecorator
		 *            图片的修饰器
		 * @return
		 */
		public Builder imageDecorator(HHImageDecorator imageDecorator) {
			mImageParam.imageDecorator = imageDecorator;
			return this;
		}

		/**
		 * 设置图片的保存路径
		 * 
		 * @param savePath
		 * @return
		 */
		public Builder savePath(String savePath) {
			mImageParam.savePath = savePath;
			return this;
		}

		/**
		 * 设置加载图片的监听器
		 * 
		 * @param listener
		 * @return
		 */
		public Builder listener(LoadImageListener listener) {
			mImageParam.listener = listener;
			return this;
		}

		/**
		 * 是否启用本地缓存
		 * 
		 * @param isUseLocalFile
		 * @return
		 */
		public Builder isUseLocalFile(boolean isUseLocalFile) {
			mImageParam.isUseLocalFile = isUseLocalFile;
			return this;
		}

		/**
		 * 图片显示的特效，暂时没有效果，传0
		 * 
		 * @param imageEffect
		 */
		public Builder imageEffect(int imageEffect) {
			mImageParam.imageEffect = imageEffect;
			return this;
		}

		/**
		 * 设置期望的图片的宽度，默认可以设置为0
		 * 
		 * @param width
		 */
		public Builder width(int width) {
			mImageParam.widthDes = width;
			return this;
		}

		/**
		 * 设置期望的图片的高度，默认可以设置为0
		 * 
		 * @param height
		 */
		public Builder height(int height) {
			mImageParam.heightDes = height;
			return this;
		}

		/**
		 * 是否缓存到内存
		 * 
		 * @param cacheToMemory
		 */
		public Builder cacheToMemory(boolean cacheToMemory) {
			mImageParam.cacheToMemory = cacheToMemory;
			return this;
		}

		public void load() {
			HHImageUtils.getInstance(null).loadImage(mLoadImageView,
					mImageParam);
		}

		public Builder loadImageNotWifi(boolean loadImageNotWifi) {
			mImageParam.loadImageNotWifi = loadImageNotWifi;
			return this;
		}

		public Builder defaultImageID(int defaultImageID) {
			mImageParam.defaultImageID = defaultImageID;
			return this;
		}

		/**
		 * 设置当图片显示出来的时候显示的动画效果，暂时没有效果，传0
		 * 
		 * @param showAnim
		 * @return
		 */
		public Builder showAnim(int showAnim) {
			mImageParam.showAnim = showAnim;
			return this;
		}
	}

}
