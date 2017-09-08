package com.huahan.hhbaseutils.constant;

import android.os.Environment;

import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHLoadViewInfo;

import java.util.HashMap;
import java.util.Map;

public class HHConstantParam {
    /**
     * 请求超时时间
     */
    public static final int DEFAULT_MILLISECONDS = 10000;
    /**
     * bug上传服务器ip地址
     */
    public static final String BUG_IP = "http://192.168.2.216:8080/";
    /**
     * 在本地创建的配置文件的名字
     */
    public static final String PREFERRENCE_FILE_NAME = "preference_file";
    /**
     * 启动页图片的本地路径，本地文件的路径
     */
    public static final String PREFERENCE_SPLASH_DRAWABLE_LOCAL_PATH = "preference_splash_drawable_local_path";
    /**
     * 设置启动页显示图片的开始时间，时间的格式是yyyy-MM-dd HH：mm:ss
     */
    public static final String PREFERENCE_SPLASH_DRAWABLE_START_TIME = "preference_splash_drawable_start_time";
    /**
     * 设置启动页显示图片的结束时间,时间的格式是yyyy-MM-dd HH：mm:ss
     */
    public static final String PREFERENCE_SPLASH_DRAWABLE_END_TIME = "preference_splash_drawable_end_time";
    /**
     * 配置文件的名字
     */
    public static final String FILE_CONFIG = "config.json";
    /**
     * 分享文件的名字
     */
    public static final String FILE_SHARE = "share.json";
    /**
     * 默认的相机的缓存路径
     */
    public static final String DEFAULT_CACHE_CAMERA = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/huahan/camera/";
    /**
     * 分享的时候，有时候需要把图片缓存到本地，这个路径就是分享的时候缓存的路径
     */
    public static final String DEFAULT_CACHE_SHARE = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/huahan/share/";
    /**
     * 保存bug信息的默认路径
     */
    public static final String DEFAULT_BUG_INFO = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/huahan/bug/";
    /**
     * 默认情况下图片的缓存路径
     */
    public static final String DEFAULT_CACHE_IMAGE = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/huahan/image/";
    /**
     * 默认的时间格式化格式
     */
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 分享的时候调用的默认的图片的大小
     */
    public static final int SHARE_THUMP_SIZE = 100;
    /**
     * 发送分享请求成功的时候发送的消息
     */
    public static final int SEND_SHARE_REQUEST_SUCCESS = 2000;
    /**
     * 发送分享请求失败的时候发送的消息
     */
    public static final int SEND_SHARE_REQUEST_FAILED = 2001;
    /**
     * 分享的类型是微信
     */
    public static final int SHARE_TYPE_WX = 0;
    /**
     * 分享的类型是朋友圈
     */
    public static final int SHARE_TYPE_TIMELINE = 1;
    /**
     * 分享类型是QQ
     */
    public static final int SHARE_TYPE_QQ = 2;
    /**
     * 分享类型是QQ好友
     */
    public static final int SHARE_TYPE_QQ_DEFAULT = 0;
    /**
     * 分享类型是QQ空间
     */
    public static final int SHARE_TYPE_QZONE = 1;
    /**
     * 分享的类型是新浪微博
     */
    public static final int SHARE_TYPE_SINA = 3;
    /**
     * 分享平台支持的最大的ID
     */
    public static final int SHARE_TYPE_MAX_ID = 3;
    /**
     * 微信分享成功发送的广播的Action
     */
    public static final String ACTION_SHARE_SUCCESS = "action_share_success";
    /**
     * 微信分享失败发送的广播的Action
     */
    public static final String ACTION_SHARE_FAILED = "action_share_failed";
    /**
     * 微信分享取消发送的广播的Action
     */
    public static final String ACTION_SHARE_CANCEL = "action_share_cancel";
    /**
     * 分享成功
     */
    public static final int SHARE_RESULT_SUCCESS = 0;
    /**
     * 分享失败
     */
    public static final int SHARE_RESULT_FAILED = 1;
    /**
     * 分享取消
     */
    public static final int SHARE_RESULT_CANCEL = 2;
    /**
     * 注解的全称，表示在解析数据的时候是否忽略这个字段
     */
    public static final String ANNOTATION_IGNORE = "com.huahan.hhbaseutils.imp.Ignore";
    /**
     * 注解，表示在解析数据的时候表明该字段是一个Model，需要按照Model的形式来解析
     */
    public static final String ANNOTATION_INSTANCE_MODEL = "com.huahan.hhbaseutils.imp.InstanceModel";
    /**
     * 消息的类型为文本,自己发送
     */
    public static final int CHAT_TYPE_TEXT_SELF = 0;
    /**
     * 消息的类型为文本，其他用户发送
     */
    public static final int CHAT_TYPE_TEXT_USER = 1;
    /**
     * 消息的类型为图片，自己发送
     */
    public static final int CHAT_TYPE_IMAGE_SELF = 2;
    /**
     * 消息的类型为图片，其他用户发送
     */
    public static final int CHAT_TYPE_IMAGE_USER = 3;
    /**
     * 消息的类型为语音,自己发送
     */
    public static final int CHAT_TYPE_VOICE_SELF = 4;
    /**
     * 消息的类型为语音，其他用户发送
     */
    public static final int CHAT_TYPE_VOICE_USER = 5;
    /**
     * 消息的类型是系统消息
     */
    public static final int CHAT_TYPE_SYSTEM = -1;
    /**
     * 消息类型的限制，超过这个值，代表的是不支持的消息类型
     */
    public static final int CHAT_TYPE_LIMIT_RIGHT = 5;
    /**
     * 消息类型的限制，小于这个值，代表的是不支持的消息类型
     */
    public static final int CHAT_TYPE_LIMIT_LEFT = -1;
    /**
     * 消息的类型是在暂不支持的消息,自己发送
     */
    public static final int CHAT_TYPE_OTHER_SELF = 6;
    /**
     * 消息的类型是在暂不支持的消息，其他用户发送
     */
    public static final int CHAT_TYPE_OTHER_USER = 7;
    /**
     * 消息发送失败
     */
    public static final int CHAT_SEND_STATE_FAILED = 0;
    /**
     * 消息正在发送
     */
    public static final int CHAT_SEND_STATE_SENDING = 1;
    /**
     * 消息发送成功
     */
    public static final int CHAT_SEND_STATE_SUCCESS = 2;
    /**
     * 每天有多少毫秒
     */
    public static final long DAY_SECOND = 60 * 60 * 24;
    /**
     * 保存全局的加载状态信息
     */
    public static final Map<HHLoadState, HHLoadViewInfo> loadViewMap = new HashMap<HHLoadState, HHLoadViewInfo>();
    /**
     * 多语言 key
     */
    public static final String LANG_KEY = "Accept-Language";
}
