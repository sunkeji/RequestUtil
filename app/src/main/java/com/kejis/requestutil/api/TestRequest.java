package com.kejis.requestutil.api;

import android.app.Activity;

import com.kejis.requestutil.bean.BaseBean;
import com.kejis.requestutil.callback.CommonDownSubscriber;
import com.kejis.requestutil.callback.CommonResponseSubscriber;
import com.kejis.requestutil.callback.CommonProgressSubscriber;
import com.kejis.requestutil.callback.HttpDownOnNextListener;
import com.kejis.requestutil.http.MyOkHttpClient;
import com.kejis.requestutil.http.MyRequest;
import com.kejis.requestutil.http.MyRetrofit;
import com.kejis.requestutil.interceptor.DownloadInterceptor;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

/**
 * ClassName:	TestRequest
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 17:08
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class TestRequest extends MyRetrofit {
    public static TestServiceApi testApi = getRequest(TestServiceApi.class, "http://www.kuaidi100.com/");
    public static TestServiceApi testApi1 = getRequest(TestServiceApi.class, "https://www.zhaoapi.cn/");


    public static void getRequest(Map<String, Object> map, CommonResponseSubscriber<BaseBean> subscriber) {
        MyRequest.post(testApi.testUrl1(map), subscriber);
    }

    public static void uploadImage(Activity activity, List<File> images, HttpDownOnNextListener listener) {
        CommonProgressSubscriber subscriber = new CommonProgressSubscriber(activity, listener);
        MultipartBody parts = MyRequest.filesToMultipartBody(images, subscriber);
        MyRequest.post(testApi1.rxUploadImage(parts), subscriber);
    }

    public static void getDown(HttpDownOnNextListener listener) {
        CommonProgressSubscriber subscriber = new CommonProgressSubscriber(listener);
        DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
        OkHttpClient.Builder builder = MyOkHttpClient.getOkHttpClient().newBuilder();
        //手动创建一个OkHttpClient并设置超时时间
        builder.addNetworkInterceptor(interceptor);

        TestServiceApi testApi2 = getRequest(TestServiceApi.class,
                "http://clips.vorwaerts-gmbh.de/", builder.build());
        MyRequest.down(testApi2.download("bytes=" + 0 + "-", "big_buck_bunny.mp4"), subscriber);
    }

}
