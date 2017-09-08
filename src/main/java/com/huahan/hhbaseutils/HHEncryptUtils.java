package com.huahan.hhbaseutils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Base64;

import com.huahan.hhbaseutils.base64.BASE64Decoder;
import com.huahan.hhbaseutils.base64.BASE64Encoder;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 该类完成常用的加密解密
 * 
 * @author yuan
 * 
 */
public class HHEncryptUtils
{
	private static final String tag = HHEncryptUtils.class.getName();
	// AES加密需要使用的变量
	private static final int AES_KEY_LENGTH = 16;
	private static final byte[] OIV = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10 }; // 此处向量可自定义，请注意如果超过0x80请加(byte)强制转换
	private static final String DEFAULT_KEY="1862b0deb369e73a";

	/**
	 * 获取一个字符串加密过后的MD5值
	 * 
	 * @param plainText
	 *            需要加密的字符串
	 * @return
	 */
	public static String encodeMD5_32(String plainText)
	{

		String s = null;
		// 用来将字节转换成16进制表示的字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] source = plainText.getBytes();
			md.update(source);
			// MD5的计算结果是一个128位的长整数，用字节表示为16个字节
			byte[] tmp = md.digest();
			// 每个字节用16进制表示的话，使用2个字符(高4位一个,低4位一个)，所以表示成16进制需要32个字符
			char[] str = new char[16 * 2];
			int k = 0;// 转换结果中对应的字符位置
			for (int i = 0; i < 16; i++)
			{// 对MD5的每一个字节转换成16进制字符
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 对字节高4位进行16进制转换
				str[k++] = hexDigits[byte0 & 0xf]; // 对字节低4位进行16进制转换
			}
			s = new String(str);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			HHLog.i(tag, "encodeMD5", e);
		}
		return s;
	}

	/**
	 * 获取字符串的md5加密值，16位
	 * 
	 * @param plainText
	 *            加密的字符串
	 * @return
	 */
	public static String encodeMD5_16(String plainText)
	{
		// 加密后的字符串
		String newstr = null;
		String result = encodeMD5_32(plainText);
		if (!TextUtils.isEmpty(result))
		{
			newstr = result.substring(8, 24);
		}
		return newstr;
	}

	/**
	 * AES加密，密码必须是16位
	 * 
	 * @param plainText
	 *            加密的字符串
	 * @param password
	 *            加密的密钥
	 * @return 加密成功返回加密的字符串，加密失败返回null
	 */
	@SuppressLint("TrulyRandom")
	public static String encodeAES_P16(String plainText, String password)
	{
		try
		{
			byte[] keyBytes = password.getBytes("UTF-8");
			byte[] keyBytesTruncated = new byte[AES_KEY_LENGTH];
			for (int i = 0; i < AES_KEY_LENGTH; i++)
			{
				if (i >= keyBytes.length)
				{
					keyBytesTruncated[i] = 0x12;
				} else
				{
					keyBytesTruncated[i] = keyBytes[i];
				}
			}
			Key ckey = new SecretKeySpec(keyBytesTruncated, "AES");
			Cipher cp = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(OIV);
			cp.init(1, ckey, iv);
			byte[] inputByteArray = plainText.getBytes("UTF-8");
			byte[] cipherBytes = cp.doFinal(inputByteArray);
			String result = new BASE64Encoder().encode(cipherBytes);
			return result;
		} catch (Exception e)
		{
			e.printStackTrace();
			HHLog.i(tag, "encodeAES_P16", e);
		}
		return null;
	}

	/**
	 * 公司使用的AES加密
	 * 
	 * @param plainText
	 *            需要加密的字符串
	 * @return 加密成功返回加密的字符串，加密失败返回null
	 */
	public static String encodeAES_B(String plainText)
	{
		String result = encodeAES_P16(plainText, DEFAULT_KEY);
		if (!TextUtils.isEmpty(result))
		{
			result = result.replace("+", "%2b");
			result = result.replace("\r\n", "").replace("\n", "");
		}
		return result;
	}

	/**
	 * 对字符串进行base64加密，默认的编方式是utf-8
	 * 
	 * @param plainText
	 *            加密的字符串
	 * @return
	 */
	public static String encodeBase64(String plainText)
	{
		return Base64.encodeToString(plainText.getBytes(), Base64.NO_WRAP);
	}

	/**
	 * 对字符串进行base64加密
	 * 
	 * @param plainText
	 *            加密的字符串
	 * @param charset
	 *            编码方式
	 * @return 加密失败返回null
	 */
	public static String encodeBase64(String plainText, String charset)
	{
		try
		{
			byte[] encode = Base64.encode(plainText.getBytes(charset), Base64.NO_WRAP);
			return new String(encode, charset);
		} catch (Exception e)
		{
			HHLog.i(tag, "encodeBase64", e);
		}
		return null;
	}

	/**
	 * 解密Base64加密的数据，默认使用的是utf-8的编码方式
	 * 
	 * @param decodeText
	 *            需要解密的数据
	 * @return 解密失败返回null
	 */
	public static String decodeBase64(String decodeText)
	{
		return decodeBase64(decodeText, "utf-8");
	}

	/**
	 * 解密Base64加密的数据
	 * 
	 * @param decodeText
	 *            需要解密的数据
	 * @param charset
	 *            编码方式
	 * @return 解密失败返回null
	 */
	public static String decodeBase64(String decodeText, String charset)
	{
		try
		{
			byte[] decode = Base64.decode(decodeText.getBytes(charset), Base64.DEFAULT);
			return new String(decode, charset);
		} catch (Exception e)
		{
			HHLog.i(tag, "decodeBase64", e);
		}
		return null;
	}
	/**
	 * 解密AES加密的数据
	 * @param decodeText		需要解密的数据
	 * @param password			解密的密钥
	 * @return					解密失败返回null
	 */
	public static String decodeAES_P16(String decodeText, String password)
	{
		try
		{
			byte[] cipherByte = new BASE64Decoder().decodeBuffer(decodeText);
			byte[] keyBytes = password.getBytes("UTF-8");
			byte[] keyBytesTruncated = new byte[AES_KEY_LENGTH];
			for (int i = 0; i < AES_KEY_LENGTH; i++)
			{
				if (i >= keyBytes.length)
				{
					keyBytesTruncated[i] = 0x12;
				} else
				{
					keyBytesTruncated[i] = keyBytes[i];
				}
			}
			Key ckey = new SecretKeySpec(keyBytesTruncated, "AES");
			Cipher cp = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(OIV);
			cp.init(2, ckey, iv);
			byte[] decryptBytes = cp.doFinal(cipherByte);
			return new String(decryptBytes, "UTF-8").replace("", "");
		} catch (Exception e)
		{
			e.printStackTrace();
			HHLog.i(tag, "decodeAES_P16", e);
		}
		return null;
	}
	/**
	 * 解密公司默认使用的AES加密的数据
	 * @param decodeText
	 * @return
	 */
	public static String decodeAES_B(String decodeText)
	{
		if (TextUtils.isEmpty(decodeText))
		{
			return null;
		}
		decodeText=decodeText.replace("%2b", "+");
		return decodeAES_P16(decodeText, DEFAULT_KEY);
	}

}
