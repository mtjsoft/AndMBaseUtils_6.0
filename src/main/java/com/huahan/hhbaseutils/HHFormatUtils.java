package com.huahan.hhbaseutils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HHFormatUtils {
	private static final String tag = HHFormatUtils.class.getSimpleName();
	/**
	 * 判断是否是电话的正则表达式
	 */
	private static final String PATTERN_PHONE = "1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
	/**
	 * 判断是否是手机号的正则表达式
	 */
	private static final String PATTERN_MOBILE = "1([\\d]{10})";
	private static final String PATTERN_MOBILE_NEW = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
	/**
	 * 判断是否是邮箱的正则表达式
	 */
	private static final String PATTERN_EMAIL = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
	/**
	 * 判断是否全是中文的正则表达式
	 */
	private static final String PATTERN_ALL_CHINESE = "[\u4e00-\u9fa5]*";
	/**
	 * 判断是否还有中文的正则表达式
	 */
	private static final String PATTERN_CONTAINS_CHINESE = "[\u4e00-\u9fa5]";
	/**
	 * 判断是否全是韩文和数字的正则表达式
	 */
	private static final String PATTERN_ALL_KOREA = "[\uAC00-\uD7AF0-9]*$";
	/**
	 * 判断域名
	 */
	private static final String PATTERN_DOMAIN = "[\u4e00-\u9fa5]";


	/**
	 * 判断一个地址是不是网络地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isHttpUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			if (url.startsWith("http://") || url.startsWith("www.")
					|| url.startsWith("https://")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前时间，出现异常返回当前时间默认时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param outFormat
	 *            date输出时的格式
	 * @return
	 */
	public static Date getNowFormatDate(String outFormat) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(outFormat);
			Date date = format.parse(format.format(new Date()));
			return date;
		} catch (Exception e) {
			return new Date();
		}
	}

	/**
	 * 获取当前时间的格式化字符串,如果出现异常，返回当前时间的默认格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param outFormat
	 *            字符串输出时的格式
	 * @param defaultResult
	 *            如果出现异常是否返回默认的时间格式。false的时候返回null
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowFormatString(String outFormat,
			boolean defaultResult) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(outFormat);
			String string = format.format(new Date());
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			HHLog.i(tag, "getNowFormatString error:", e);
			return defaultResult ? getNowDefaultString() : null;
		}

	}

	/**
	 * 返回当前时间的格式化输出。如果出现异常返回当前的时间的默认格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param outFormat
	 *            输出的格式
	 * @return
	 */
	public static String getNowFormatString(String outFormat) {
		return getNowFormatString(outFormat, true);
	}

	private static String getNowDefaultString() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		return String.format(Locale.CHINESE, "%d-%02d-%02d %02d:%02d:%02d",
				year, month, day, hour, minute, second);
	}

	/**
	 * 判断是否是电话号码，包括手机和固话
	 * 
	 * @param phone
	 *            电话号码
	 * @return 如果是电话号码，返回true
	 */
	public static boolean isPhone(String phone) {
		return isPatternAvalible(PATTERN_PHONE, phone);
	}

	/**
	 * 判断是不是手机号
	 * 
	 * @param phone
	 *            手机号码
	 * @return
	 */
	public static boolean isMobile(String phone) {
		return isPatternAvalible(PATTERN_MOBILE_NEW, phone);
	}

	/**
	 * 判断参数是不是手机号或者固话
	 * 
	 * @param pattern
	 *            判断是手机或者固话的正则表达式
	 * @param param
	 *            判断的参数
	 * @return 如果是返回true
	 */
	private static boolean isPatternAvalible(String pattern, String param) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(param);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是邮箱
	 * 
	 * @param email
	 *            邮箱
	 * @return 如果是一个邮箱的地址，返回true
	 */
	public static boolean isEmail(String email) {
		return isPatternAvalible(PATTERN_EMAIL, email);
	}

	/**
	 * 判断是否全是中文
	 * 
	 * @param value
	 *            需要判断的字符串
	 * @return 如果都是中文的话，返回true
	 */
	public static boolean isAllChinese(String value) {
		return isPatternAvalible(PATTERN_ALL_CHINESE, value);
	}

	/**
	 * 判断是否含有中文
	 * 
	 * @param value
	 *            需要判断的字符串
	 * @return 如果含有中文的话返回true
	 */
	public static boolean isContainsChinese(String value) {
		return isPatternAvalible(PATTERN_CONTAINS_CHINESE, value);
	}

	/**
	 * 判断是否全是韩文和数字
	 * 
	 * @param value
	 *            需要判断的字符串
	 * @return 如果都是中文的话，返回true
	 */
	public static boolean isAllKorea(String value) {
		return isPatternAvalible(PATTERN_ALL_KOREA, value);
	}

	/**
	 * 把字符串转换成Date
	 * 
	 * @param dateString
	 *            需要转换的字符串
	 * @param inFormat
	 *            字符串的格式，例如yyyy-MM-dd HH：mm:ss
	 * @return 如果转换成功返回转换以后的date，转换失败的话返回null
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date convertToDate(String dateString, String inFormat) {
		SimpleDateFormat format = new SimpleDateFormat(inFormat);
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (Exception e) {
			HHLog.i(tag, "convertToDate", e);
		}
		return date;
	}

	/**
	 * 把字符串转换成毫秒值
	 * 
	 * @param dateString
	 *            需要转换的字符串
	 * @param inFormat
	 *            字符串的格式，例如yyyy-MM-dd HH:mm:ss
	 * @return 如果转换成功，返回转换以后的毫秒值，如果转换失败返回-1
	 */
	public static long convertToMilliSecond(String dateString, String inFormat) {
		Date convertToDate = convertToDate(dateString, inFormat);
		return convertToDate == null ? -1 : convertToDate.getTime();
	}

	/**
	 * 把一个格式的时间字符串转换成另外一种格式的时间字符串
	 * 
	 * @param dateString
	 *            原始的时间字符串
	 * @param inFormat
	 *            原始的时间字符串的格式
	 * @param outFormat
	 *            输出的字符串的格式
	 * @return 如果转换失败返回null
	 */
	public static String convertToString(String dateString, String inFormat,
			String outFormat) {
		return convertToString(dateString, inFormat, outFormat, true);
	}

	/**
	 * 把一种格式的时间字符串转换成另外一种格式的时间字符串
	 * 
	 * @param dateString
	 *            原始的时间字符串
	 * @param inFormat
	 *            原始的时间字符串的格式
	 * @param outFormat
	 *            输出的字符串的格式
	 * @param returnNull
	 *            如果转换失败是否返回null
	 * @return 如果转换失败的话：returnNull为true，返回null，false返回原始字符串
	 */
	public static String convertToString(String dateString, String inFormat,
			String outFormat, boolean returnNull) {
		Date date = convertToDate(dateString, inFormat);
		if (date != null) {
			return convertToString(date, outFormat);
		}
		return returnNull ? null : dateString;
	}

	/**
	 * 把一个Date对象转换成相应格式的字符串
	 * 
	 * @param date
	 *            时间
	 * @param outFormat
	 *            输出的格式
	 * @return 返回转换的字符串
	 */
	@SuppressLint("SimpleDateFormat")
	public static String convertToString(Date date, String outFormat) {
		SimpleDateFormat format = new SimpleDateFormat(outFormat);
		return format.format(date);
	}

}
