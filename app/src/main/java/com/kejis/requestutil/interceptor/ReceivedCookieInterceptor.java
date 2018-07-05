package com.kejis.requestutil.interceptor;

import com.skj.wheel.util.LogUtil;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * ClassName:	ReceivedCookieInterceptor
 * Function:	${TODO} 获取cookie,保持okhttp网路请求cookie一致
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 17:17
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class ReceivedCookieInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            LogUtil.i("-------cookie:" + cookies);
        }
        return originalResponse;
    }
}
