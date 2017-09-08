package com.huahan.hhbaseutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * 图片处理的特效类
 * 
 * @author yuan
 * 
 */
public class HHImageSpecial
{
	/**
	 * 获取模糊效果的图片，类似高斯模糊
	 * 
	 * @param sentBitmap
	 *            源bitmap
	 * @param radius
	 *            中心距两边的距离
	 * @param canReuseInBitmap
	 *            是否重用bitmap（bitmap是否可以编辑）
	 * @return
	 */
	public static Bitmap getBlurBitmap(Bitmap sentBitmap, int radius, boolean canReuseInBitmap)
	{
		Bitmap bitmap;
		if (canReuseInBitmap)
		{
			bitmap = sentBitmap;
		} else
		{
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
			sentBitmap.recycle();
		}

		if (radius < 1)
		{
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++)
		{
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++)
		{
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++)
			{
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0)
				{
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else
				{
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++)
			{

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0)
				{
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++)
		{
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++)
			{
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0)
				{
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else
				{
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm)
				{
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++)
			{
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0)
				{
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	/**
	 * 获取一个就有怀旧效果的bitmap
	 * 
	 * @param sourceBitmap
	 *            源bitmap
	 * @param canReuseInBitmap
	 *            bitmap是否可以编辑
	 * @return
	 */
	public static Bitmap getNostalgicBitmap(Bitmap sourceBitmap, boolean canReuseInBitmap)
	{
		Bitmap bitmap = null;
		if (sourceBitmap != null)
		{

			if (canReuseInBitmap)
			{
				bitmap = sourceBitmap;
			} else
			{
				bitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
				sourceBitmap.recycle();
			}
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int data[] = new int[width * height];
			bitmap.getPixels(data, 0, width, 0, 0, width, height);
			int pixColor = 0;
			int pixR = 0;
			int pixG = 0;
			int pixB = 0;
			int newR = 0;
			int newG = 0;
			int newB = 0;
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					pixColor = data[width * i + j];
					// 等价于(pixColor >> 16) & 0xFF 获取一个像素的R
					pixR = Color.red(pixColor);
					// 等价于(pixColor >> 8) & 0xFF 获取一个像素的G
					pixG = Color.green(pixColor);
					// 等价于(pixColor) & 0xFF 获取一个像素的B
					pixB = Color.blue(pixColor);
					// 根据算法由原图的RGB生成新的RGB
					newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
					newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
					newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
					// 由RGB生成一个像素
					// 函数：argb (int alpha, int red, int green, int blue)
					int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
					data[width * i + j] = newColor;
				}
			}
			// 生成新的图片
			bitmap.setPixels(data, 0, width, 0, 0, width, height);
		}
		return bitmap;

	}

	/**
	 * 获取具有浮雕效果的Bitmap
	 * 
	 * @param sourceBitmap
	 * @return
	 */
	public static Bitmap getReliefBitmap(Bitmap sourceBitmap)
	{
		Bitmap bitmap = null;
		bitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
		sourceBitmap.recycle();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int data[] = new int[width * height];
		int newData[] = new int[width * height];
		bitmap.getPixels(data, 0, width, 0, 0, width, height);
		int oldPix = 0;
		int newPix = 0;
		int oldR = 0, oldG = 0, oldB = 0;
		int newR = 0, newG = 0, newB = 0;
		int alpha = 0;
		for (int i = 1; i < data.length; i++)
		{
			oldPix = data[i - 1];
			alpha = Color.alpha(oldPix);
			// 前一个像素
			oldR = Color.red(oldPix);
			oldG = Color.green(oldPix);
			oldB = Color.blue(oldPix);
			// 当前像素
			newPix = data[i];

			newR = Color.red(newPix);
			newG = Color.green(newPix);
			newB = Color.blue(newPix);

			newR = (newR - oldR + 127);
			newG = (newG - oldG + 127);
			newB = (newB - oldB + 127);
			// 均小于等于255
			if (newR > 255)
			{
				newR = 255;
			}

			if (newG > 255)
			{
				newG = 255;
			}

			if (newB > 255)
			{
				newB = 255;
			}

			newData[i] = Color.argb(alpha, newR, newG, newB);
		}
		bitmap.setPixels(newData, 0, width, 0, 0, width, height);
		return bitmap;
	}
	/**
	 * 对一个Bitmap进行缩放
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	private static Bitmap getScaleImage(Bitmap bitmap, float width, float height)
	{
		if (null == bitmap || width < 0.0f || height < 0.0f)
		{
			return null;
		}
		Matrix matrix = new Matrix();
		float scaleWidth = width / bitmap.getWidth();
		float scaleHeight = height / bitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	/**
	 * 获取一个指定形状的Bitmap
	 * @param srcBitmap					原始的Bitmap
	 * @param shapeResID				形状的资源的ID，一般情况下是一张图片，这张图片定义了显示的形状
	 * @return
	 */
	public static Bitmap getShapeBitmap(Context context,Bitmap srcBitmap,int shapeResID)
	{
		if (null == srcBitmap)
		{
			return null;
		}
		Bitmap background = null;
		//获取背景的bitmap
		background = BitmapFactory.decodeResource(context.getResources(), shapeResID);
		//必须设置背景，这个背景用于实现显示特定形状的图片
		if (null == background)
		{
			return null;
		}
		return getShapeBitmap(srcBitmap, background);
//		Bitmap mask = null;
//		Bitmap newBitmap = null;
//		mask = srcBitmap;
////		//获取原始显示图片的宽高
////		float srcWidth = (float) srcBitmap.getWidth();
////		float srcHeight = (float) srcBitmap.getHeight();
////		//如果原始图片的大小太小
////		if (srcWidth < 300f && srcHeight < 500f)
////		{
////			srcWidth = 300f;
////			srcHeight = (float) 500f;
////			//对背景图片进行缩放
////			Bitmap tmp = getScaleImage(background, srcWidth, srcHeight);
////			if (null != tmp)
////			{
////				background = tmp;
////			} else
////			{
////				tmp = getScaleImage(srcBitmap, (float) 100f, (float) 100f);
////				if (null != tmp)
////				{
////					mask = tmp;
////				}
////			}
////		}
//		Config config = background.getConfig();
//		if (null == config)
//		{
//			config = Bitmap.Config.ARGB_8888;
//		}
//		newBitmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), config);
//		Canvas newCanvas = new Canvas(newBitmap);
//		newCanvas.drawBitmap(background, 0, 0, null);
//		Paint paint = new Paint();
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//		int left = 0;
//		int top = 0;
//		int right = mask.getWidth();
//		int bottom = mask.getHeight();
//		if (mask.getWidth() > background.getWidth())
//		{
//			left = (mask.getWidth() - background.getWidth()) / 2;
//			right = mask.getWidth() - left;
//		}
//		if (mask.getHeight() > background.getHeight())
//		{
//			top = (mask.getHeight() - background.getHeight()) / 2;
//			bottom = mask.getHeight() - top;
//		}
//		newCanvas.drawBitmap(mask, new Rect(left, top, right, bottom), new Rect(0, 0, background.getWidth(), background.getHeight()), paint);
//		return newBitmap;
	}
	public static Bitmap getShapeBitmap(Bitmap srcBitmap,Bitmap shapeBitmap)
	{
		//获取原始显示图片的宽高
		float srcWidth = (float) srcBitmap.getWidth();
		float srcHeight = (float) srcBitmap.getHeight();
		//如果原始图片的大小太小
		if (srcWidth < 300f && srcHeight < 500f)
		{
			srcWidth = 300f;
			srcHeight = (float) 500f;
			//对背景图片进行缩放
			Bitmap tmp = getScaleImage(shapeBitmap, srcWidth, srcHeight);
			if (null != tmp)
			{
				shapeBitmap = tmp;
			}
		}
		Bitmap newBitmap=null;
		Config config = shapeBitmap.getConfig();
		if (null == config)
		{
			config = Bitmap.Config.ARGB_8888;
		}
		newBitmap = Bitmap.createBitmap(shapeBitmap.getWidth(), shapeBitmap.getHeight(), config);
		Canvas newCanvas = new Canvas(newBitmap);
		newCanvas.drawBitmap(shapeBitmap, 0, 0, null);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		int left = 0;
		int top = 0;
		int right = srcBitmap.getWidth();
		int bottom = srcBitmap.getHeight();
		if (srcBitmap.getWidth() > shapeBitmap.getWidth())
		{
			left = (srcBitmap.getWidth() - shapeBitmap.getWidth()) / 2;
			right = srcBitmap.getWidth() - left;
		}
		if (srcBitmap.getHeight() > shapeBitmap.getHeight())
		{
			top = (srcBitmap.getHeight() - shapeBitmap.getHeight()) / 2;
			bottom = srcBitmap.getHeight() - top;
		}
		newCanvas.drawBitmap(srcBitmap, new Rect(left, top, right, bottom), new Rect(0, 0, shapeBitmap.getWidth(), shapeBitmap.getHeight()), paint);
		return newBitmap;
	}

}
