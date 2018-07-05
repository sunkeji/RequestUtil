package com.kejis.requestutil.http;

import android.os.Environment;

import com.kejis.requestutil.interceptor.MyInterceptor;
import com.skj.wheel.util.LogUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;


import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * ClassName:	MyOkHttpClient
 * Function:	${TODO} okhttp网络请求配置,自定义拦截器
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/20 14:29
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class MyOkHttpClient {

    public static final String FILE_SDCARD = Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/cache";
    /**
     * 初始化OkHttp3
     */
    private static OkHttpClient okHttpClient;
    private static long timeout = 10 * 6;//网路连接、读、写超时时间设置
    /**
     * 设置缓存目录和大小
     */
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(new File(FILE_SDCARD), cacheSize);

    /**
     * 配置okhttp 请求
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                    .sslSocketFactory(SSLValidate.getSSLSocketFactory())
//                    .hostnameVerifier(SSLValidate.getHostnameVerifier())//https连接添加安全认证
                    .cache(cache)  //禁用okhttp自身的的缓存
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)//连接、读、写超时时间设置
                    .retryOnConnectionFailure(true)//错误重连
//                    .addInterceptor(new MyInterceptor())
//                    .addNetworkInterceptor(new DownloadInterceptor())//添加拦截器
                    .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            LogUtil.i("okhttp:", "Url=" + message);
                        }
                    }).setLevel(HttpLoggingInterceptor.Level.BODY))//日志拦截器
                    .build();
        }
        return okHttpClient;
    }


}
