package com.kejis.requestutil.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ClassName:	AddCookieInterceptor
 * Function:	${TODO} 设置cookie,保持okhttp网路请求cookie一致
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 17:15
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class AddCookieInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Cookie", "Cookie");
        return chain.proceed(builder.build());
    }
}
