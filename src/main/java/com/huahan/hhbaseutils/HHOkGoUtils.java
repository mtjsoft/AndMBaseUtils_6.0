package com.huahan.hhbaseutils;

import com.huahan.hhbaseutils.imp.HHJsonCallBack;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 第三方网络框架okgo的Demo
 * Created by matengjiao on 2017/8/13.
 */

public class HHOkGoUtils {

    /**
     * GET无请求体的
     */
    public void demoGetModel() {
        OkGo.<HHShareModel>get("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/30/1")
                .execute(new HHJsonCallBack<HHShareModel>() {
                    @Override
                    public void onSuccess(Response response) {
                        HHShareModel grilsModel = (HHShareModel) response.body();
                    }

                    @Override
                    public void onError(Response response) {
                        super.onError(response);
                    }
                });
    }


    public void demoGetString() {
        OkGo.<String>get("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/30/1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body().toString();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * POST请求参数无文件
     */
    public void demoPostModel() {
        OkGo.<HHShareModel>post("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/30/1")
                .params("aaa", "123", false)
                .execute(new HHJsonCallBack<HHShareModel>() {
                    @Override
                    public void onSuccess(Response response) {
                        HHShareModel grilsModel = (HHShareModel) response.body();
                    }

                    @Override
                    public void onError(Response response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * POST请求参数有文件
     */
    public void demoPostFileModel() {
        OkGo.<HHShareModel>post("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/30/1")
                .params("aaa", "123", false)
                .params("bbb", new File("path", "1.txt"))
                .execute(new HHJsonCallBack<HHShareModel>() {
                    @Override
                    public void onSuccess(Response response) {
                        HHShareModel grilsModel = (HHShareModel) response.body();
                    }

                    @Override
                    public void onError(Response response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * POST一个key传多个值，或者多个文件
     */
    public void demoPostMoreFileModel() {
        List<File> files = new ArrayList<>();
        files.add(new File("path", "1.txt"));
        files.add(new File("path", "2.txt"));

        List<String> urlParams = new ArrayList<>();
        urlParams.add("111");
        urlParams.add("123");

        OkGo.<HHShareModel>post("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/30/1")
                .params("aaa", "123", false)
                .addUrlParams("bbb", urlParams)
                .addFileParams("ccc", files)
                .execute(new HHJsonCallBack<HHShareModel>() {
                    @Override
                    public void onSuccess(Response response) {
                        HHShareModel grilsModel = (HHShareModel) response.body();
                    }

                    @Override
                    public void onError(Response response) {
                        super.onError(response);
                    }
                });
    }
}
