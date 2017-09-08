package com.huahan.hhbaseutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.huahan.hhbaseutils.imp.HHImageDecorator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class HHFileCache
{
	private static HHFileCache fileCache;
	private HHImageUtils imageUtils;

	/**
	 * 私有化构造函数
	 */
	private HHFileCache()
	{
	}

	/**
	 * 获取HHFileCache的一个实例
	 * 
	 * @param fileCacheSize
	 *            文件缓存的最大的大小，暂时传0
	 * @return
	 */
	public static HHFileCache getInstance(HHImageUtils imageUtils, long fileCacheSize)
	{
		synchronized (HHFileCache.class)
		{
			if (fileCache == null)
			{
				fileCache = new HHFileCache();
				fileCache.init(imageUtils);
			}
		}
		return fileCache;
	}

	private void init(HHImageUtils imageUtils)
	{
		// TODO Auto-generated method stub
		this.imageUtils = imageUtils;
	}
	/**
	 * 从文件中获取Bitmap,如果文件不存在或者其他原因引起的获取失败返回null
	 * @param cacheKey 					缓存的key的名称
	 * @param filePath					文件的路径
	 * @param widthDes					期望的宽度
	 * @param heightDes					期望的高度
	 * @param bigImage					是否加载大图
	 * @param forceScaleImage			如果内存不足以加载该图片的时候，是否强制缩小图片以显示该图片
	 * @param cacheBitmapToMemory		图片加载出来以后是否缓存到内存中
	 * @return							加载失败返回null
	 */
	public Bitmap getBitmapFromFile(String cacheKey,String filePath, int widthDes, int heightDes, boolean bigImage, boolean forceScaleImage,boolean cacheBitmapToMemory,HHImageDecorator imageDecorator)
	{
		File file = new File(filePath);
		Bitmap bitmap = null;
		if (file.exists())
		{
			try
			{
				Options opts = new Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, opts);
				if (widthDes<1||heightDes<1)
				{
					heightDes=widthDes=imageUtils.getLoadImageSize();
				}
				int sampleSize = imageUtils.calculateSampleSize(opts, widthDes, heightDes, bigImage);
				opts.inSampleSize = sampleSize;
				int bitmapMemorySize = imageUtils.getBitmapMemorySize(opts.outWidth / sampleSize, opts.outHeight / sampleSize, opts.inPreferredConfig);
				opts.inJustDecodeBounds = false;
				if (HHSystemUtils.getNowAvalibleMemory() > bitmapMemorySize)
				{
					bitmap = decodeAndCacheBitmap(cacheKey,filePath, opts,cacheBitmapToMemory,imageDecorator);
				} else
				{
					// 清除缓存
					imageUtils.clearCache();
					// 请求虚拟机回收垃圾
					System.gc();
					if (HHSystemUtils.getNowAvalibleMemory()> bitmapMemorySize)
					{
						bitmap = decodeAndCacheBitmap(cacheKey,filePath, opts,cacheBitmapToMemory,imageDecorator);
					} else if (forceScaleImage)
					{
						float scale = bitmapMemorySize / HHSystemUtils.getNowAvalibleMemory();
						opts.inSampleSize = sampleSize * ((int) Math.sqrt(scale) + 1);
						bitmap = decodeAndCacheBitmap(cacheKey,filePath, opts,cacheBitmapToMemory);
					}
					
				}
				
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (Throwable throwable)
			{
			}
		}
		return bitmap;
	}
	/**
	 * 从文件中按照指定的参数来获取一个Bitmap
	 * @param cacheKey					bitmap添加到缓存中时对应的key
	 * @param filePath					文件的路径
	 * @param opts						解析Bitmap的参数
	 * @param cacheBitmapToMemory		bitmap解析出来以后是否把Bitmap缓存到内存中
	 * @return
	 * @throws FileNotFoundException
	 */
	public Bitmap decodeAndCacheBitmap(String cacheKey,String filePath, Options opts,boolean cacheBitmapToMemory) throws FileNotFoundException
	{
//		FileInputStream fis = new FileInputStream(filePath);
//		Bitmap bitmap = null;
//		synchronized (fileCache)
//		{
//			bitmap= BitmapFactory.decodeStream(fis, null, opts);
//			if (cacheBitmapToMemory)
//			{
//				imageUtils.putBitmapToMemoryCache(cacheKey, bitmap);
//			}
//		}
//		return bitmap;
		return decodeAndCacheBitmap(cacheKey, filePath, opts, cacheBitmapToMemory, null);
	}
	public Bitmap decodeAndCacheBitmap(String cacheKey,String filePath, Options opts,boolean cacheBitmapToMemory,HHImageDecorator decorator) throws FileNotFoundException
	{
		FileInputStream fis = new FileInputStream(filePath);
		Bitmap bitmap = null;
		synchronized (fileCache)
		{
			bitmap= BitmapFactory.decodeStream(fis, null, opts);
			if (bitmap!=null&&decorator!=null)
			{
				Bitmap tempBitmap=decorator.decorateImage(bitmap);
				if (tempBitmap!=null)
				{
					cacheKey=cacheKey+"_"+decorator.getClass().getSimpleName();
					bitmap=tempBitmap;
				}
			}
			if (cacheBitmapToMemory)
			{
				imageUtils.putBitmapToMemoryCache(cacheKey, bitmap);
			}
		}
		return bitmap;
	}
}
