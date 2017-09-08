package com.huahan.hhbaseutils.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huahan.hhbaseutils.HHConfigUtils;
import com.huahan.hhbaseutils.HHFileUtils;
import com.huahan.hhbaseutils.HHFormatUtils;
import com.huahan.hhbaseutils.HHImageUtils;
import com.huahan.hhbaseutils.HHScreenUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.manager.HHAnimBuilder;

import org.json.JSONException;

import java.io.IOException;

/**
 * 固定时间段显示图片：保存的时间格式必须为：yyyy-MM-dd HH:mm:ss
 *
 * @author yuan
 */
public abstract class HHSplashActivity extends HHBaseActivity {

    //显示启动页
    private ImageView mSplashImageView;
    //根视图
    private RelativeLayout mSplashLayout;
    private Animation mAnimation;

    @Override
    public View initView() {
        View view = View.inflate(getPageContext(), R.layout.hh_activity_splash, null);
        mSplashImageView = HHViewHelper.getViewByID(view, R.id.hh_img_splash);
        mSplashLayout = HHViewHelper.getViewByID(view, R.id.hh_rl_splash);
        return view;
    }

    @Override
    public void initValues() {
        //1：首先需要判断本地文件中是否含有显示图片的本地路径,如果有本地需要显示的图片则显示本地的图片
        //2：如果存在存在本地文件的路径，但是这个本地文件并不存在，则判断本地存储资源文件的id，如果id不为0，则显示该资源文件
        //3：如果本地文件为空，但是资源文件的id不是0，则显示资源文件
        //4：获取配置文件，显示配置文件中显示的图片
        setSplashImage();
        mAnimation = HHAnimBuilder.buildAlphaAnimation(getSplashLastTime());
        mAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onSplashImageFinish();
                finish();
            }
        });
        mSplashImageView.startAnimation(mAnimation);

    }

    @Override
    public void initListeners() {
    }

    @Override
    public void processHandlerMsg(Message msg) {
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(HHConstantParam.PREFERRENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    private Bitmap getSplashBitmap(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int width = HHScreenUtils.getScreenWidth(this);
        int height = HHScreenUtils.getScreenHeight(this);
        int simpleSize = HHImageUtils.getInstance(null).calculateSampleSize(options, width, height, false);
        options.inSampleSize = simpleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 设置显示的图片
     *
     * @throws JSONException
     * @throws IOException
     */
    private void setSplashImage() {
        if (isUseLocalFile()) {
            String path = getSharedPreferences().getString(HHConstantParam.PREFERENCE_SPLASH_DRAWABLE_LOCAL_PATH, "");
            //本地文件路径为空字符串或者本地文件不存在，则显示默认的图片
            if (TextUtils.isEmpty(path) || !HHFileUtils.isFileExist(path)) {
                //把本地文件的路径重新设置为空字符串
                HHConfigUtils.setConfigInfo(this, HHConstantParam.PREFERRENCE_FILE_NAME, HHConstantParam.PREFERENCE_SPLASH_DRAWABLE_LOCAL_PATH, "");
                mSplashImageView.setImageResource(getSplashImageID());
            } else {
                String startTime = getSharedPreferences().getString(HHConstantParam.PREFERENCE_SPLASH_DRAWABLE_START_TIME, "");
                String endTime = getSharedPreferences().getString(HHConstantParam.PREFERENCE_SPLASH_DRAWABLE_END_TIME, "");
                String nowTime = HHFormatUtils.getNowFormatString(HHConstantParam.DEFAULT_TIME_FORMAT);
                if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || (startTime.compareTo(nowTime) < 0 && endTime.compareTo(nowTime) > 0)) {
                    Bitmap bitmap = getSplashBitmap(path);
                    mSplashImageView.setImageBitmap(bitmap);
                } else {
                    mSplashImageView.setImageResource(getSplashImageID());
                }
            }
        } else {
            //不适用本地文件
            mSplashImageView.setImageResource(getSplashImageID());
        }
    }

    /**
     * 当启动页显示完成的时候执行的操作，跳转页面
     */
    protected abstract void onSplashImageFinish();

    /**
     * 获取显示启动页的图片
     *
     * @return
     */
    protected abstract int getSplashImageID();

    /**
     * 获取启动页显示的时长
     *
     * @return
     */
    protected int getSplashLastTime() {
        return 1500;
    }

    /**
     * 获取是否使用本地图片
     *
     * @return
     */
    protected boolean isUseLocalFile() {
        return false;
    }

    /**
     * 获取当前页面显示的根视图
     *
     * @return
     */
    protected final RelativeLayout getSplashLayout() {
        return mSplashLayout;
    }


}

