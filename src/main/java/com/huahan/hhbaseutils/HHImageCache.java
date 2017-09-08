package com.huahan.hhbaseutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 该类用于在内存中缓存图片
 * @author yuan
 *
 */
class HHImageCache
{

	private static final String tag=HHImageCache.class.getSimpleName();
	private static HHImageCache imageCache;
	/**
	 * 图片的内存缓存，实现的是硬缓存
	 */
	private LruCache<String,Bitmap> lruCache;
	/**
	 * 图片的内存缓存，实现的是软缓存
	 */
	private Map<String, SoftReference<Bitmap>> softCache;
	/**
	 * 私有化构造函数
	 */
	private HHImageCache(){}
	/**
	 * 获取一个HHImageCache实例
	 * @param memoryCacheSize
	 * @return
	 */
	public static HHImageCache getInstance(int memoryCacheSize)
	{
		// TODO Auto-generated method stub
		synchronized (HHImageCache.class)
		{
			if (imageCache==null)
			{
				imageCache=new HHImageCache();
				imageCache.init(memoryCacheSize,false);
			}
		}
		return imageCache;
	}
	/**
	 * 初始化HHImageCache
	 * @param maxSize					内存的最大大小,如果大小小于1，则默认设置为该应用可以使用的最大内存的八分之一
	 * @param cacheRemoveBitmap			是否缓存调用LruCache.remove或者put方法移除的Bitmap
	 */
	private void init(int maxSize,final boolean cacheRemoveBitmap)
	{
		if (maxSize<1)
		{
			maxSize=(int) (HHSystemUtils.getMaxMemory()/4);
		}
		softCache=new HashMap<String, SoftReference<Bitmap>>();
		lruCache=new LruCache<String, Bitmap>(maxSize)
		{

			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
			{
				// TODO Auto-generated method stub
				//Bitmap被移除是因为内存不足
				HHLog.i(tag, "entryRemove:"+key);
				if (evicted||cacheRemoveBitmap)
				{
						SoftReference<Bitmap> softReference = softCache.get(key);
						if (softReference!=null&&softReference.get()!=null)
						{
							return ;
						}
						if (softReference!=null)
						{
							softCache.remove(key);
						}
						softCache.put(key, new SoftReference<Bitmap>(oldValue));
				}
			}

			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				// TODO Auto-generated method stub
				return value.getHeight()*value.getRowBytes();
			}
			
		};
	}
	/**
	 * 从内存缓存中获取bitmap
	 * @param key			bitmap对应的键值
	 * @return				内存中不存在对应的Bitmap时，返回null
	 */
	public Bitmap getBitmap(String key)
	{
		Bitmap bitmap = lruCache.get(key);
		if (bitmap!=null&&bitmap.isRecycled())
		{
			bitmap=null;
			lruCache.remove(key);
		}
		if (bitmap==null)
		{
			SoftReference<Bitmap> softReference = softCache.get(key);
			if (softReference!=null)
			{
				bitmap=softReference.get();
				if (bitmap!=null&&bitmap.isRecycled())
				{
					bitmap=null;
					softCache.remove(key);
				}
			}
		}

		return bitmap;
	}
	/**
	 * 把Bitmap添加到内存缓存中
	 * @param key			Bitmap对应的key
	 * @param value			缓存的Bitmap
	 */
	public void put(String key,Bitmap value)
	{
		synchronized (lruCache)
		{
			lruCache.put(key, value);
		}
	}
	/**
	 * 清空内存缓存
	 */
	public void clear()
	{
		lruCache.evictAll();

		Collection<SoftReference<Bitmap>> values = softCache.values();
		for (SoftReference<Bitmap> softReference : values)
		{
			Bitmap bitmap = softReference.get();
			if (bitmap!=null&&!bitmap.isRecycled())
			{
				bitmap.recycle();
			}
		}
		softCache.clear();
	}
	public Bitmap getDefaultImage(Context context,int defaultImage)
	{
		Bitmap bitmap = getBitmap(defaultImage+"");
		if ((bitmap==null||bitmap.isRecycled())&&defaultImage>0)
		{
			InputStream inputStream = context.getResources().openRawResource(defaultImage);
			bitmap=BitmapFactory.decodeStream(inputStream);
			put(defaultImage+"", bitmap);
		}
		return bitmap;
		
	}
	

}
