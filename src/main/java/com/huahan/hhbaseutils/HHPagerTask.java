package com.huahan.hhbaseutils;

import android.support.v4.view.ViewPager;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class HHPagerTask {

    int time = 5000;
    int count;
    WeakReference<ViewPager> pagerReference;
    Timer timer;

    /**
     * 开始切换图片
     */
    public void startChange() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final ViewPager viewPager = pagerReference.get();
                if (viewPager == null) {
                    resetTimer();
                } else {
                    viewPager.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            int position = (viewPager.getCurrentItem() + 1)
                                    % count;
                            viewPager.setCurrentItem(position);
                        }
                    });
                }
            }
        }, 1000, time);
    }

    public WeakReference<ViewPager> getPager() {
        return pagerReference;
    }

    public HHPagerTask(int time, int count, ViewPager pager) {
        this.count = count;
        this.time = time;
        pagerReference = new WeakReference<ViewPager>(pager);
    }

    /**
     * 取消timer;并且设置ViewPager的tag
     */
    public void cancelTask() {
        resetTimer();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
