package com.kejis.requestutil.interceptor;

/**
 * ClassName:	MyInterceptor
 * Function:	${TODO} http 请求拦截器（为请求发生过程中拦截配置一些参数）
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 17:15
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        //添加header、配置缓存类型、对请求签名等等在网络请求收到回复前的操作
        Request request = builder
                .cacheControl(CacheControl.FORCE_NETWORK)
//                    .addHeader("Accept", "application/json")
                .addHeader("Connection", "close")
                .build();
        Response originalResponse = chain.proceed(request);
        return originalResponse;
    }
}
