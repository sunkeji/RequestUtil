package com.kejis.requestutil.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ClassName:	MyRetrofit
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/22 16:03
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class MyRetrofit {

    /**
     * 初始化Retrofit
     */
    private static Retrofit retrofit;
    //对数据解析做一些转化
    private static Gson gson = new GsonBuilder().
            setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

    /**
     * 配置Retrofit参数 默认MyOkHttpClient请求配置
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit getRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(MyOkHttpClient.getOkHttpClient())//设置使用okhttp网络请求
                .baseUrl(baseUrl)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create(gson))//添加gosn转换器数据解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())//添加回调库，对RxJava的支持
                //也可以添加自定义的RxJavaCallAdapterFactory
                .build();
        return retrofit;
    }

    /**
     * 配置Retrofit参数 重新定义MyOkHttpClient请求配置
     *
     * @param baseUrl
     * @param okHttpClient
     * @return
     */
    public static Retrofit getRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(baseUrl)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create(gson))//添加gosn转换器数据解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())//添加回调库，对RxJava的支持
                //也可以添加自定义的RxJavaCallAdapterFactory
                .build();
        return retrofit;
    }

    /**
     * 配置请求接口类和服务器路径,重新定义MyOkHttpClient请求
     *
     * @param tClass
     * @param baseUrl
     * @param <T>
     * @return
     */
    public static <T> T getRequest(final Class<T> tClass, String baseUrl) {
        return getRetrofit(baseUrl).create(tClass);
    }

    /**
     * 配置请求接口类和服务器路径
     *
     * @param tClass
     * @param baseUrl
     * @param okHttpClient
     * @param <T>
     * @return
     */
    public static <T> T getRequest(final Class<T> tClass, String baseUrl, OkHttpClient okHttpClient) {
        return getRetrofit(baseUrl, okHttpClient).create(tClass);
    }
}
