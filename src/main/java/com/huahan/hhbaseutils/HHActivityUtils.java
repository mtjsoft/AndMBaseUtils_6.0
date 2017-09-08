package com.huahan.hhbaseutils;

import android.app.Activity;

import java.util.ArrayList;
/**
 * 继承至HHActivity的页面可以实现该方法
 * @author xiao
 *
 */
public class HHActivityUtils {
	private static final String tag=HHActivityUtils.class.getName();
	private static HHActivityUtils manager;
	private ArrayList<Activity> list = new ArrayList<Activity>();

	public static synchronized HHActivityUtils getInstance() {
		if (manager == null) {
			manager = new HHActivityUtils();
		}
		return manager;
	}

	public ArrayList<Activity> getAliveActivity() {
		return this.list;
	}

	public void addActivity(Activity activity) {
		this.list.add(activity);
	}

	public void removeActivity(Activity activity) {
		this.list.remove(activity);
	}
	/**
	 * 关闭倒数n-1个页面
	 * @param num
	 */
	public void closeActivity(int num)
	{
		if (list.size() > num) {
			for (int i = list.size() - num; i < list.size()-1; i++) {
				Activity activity=list.get(i);
				if (activity!=null) {
					activity.finish();
				}
			}
		}
	}
	/**
	 * 关闭所有页面
	 */
	public void clearActivity()
	{
		for (Activity activity:list) {    
            if (activity != null)    
                activity.finish();    
        }  
	}
}
