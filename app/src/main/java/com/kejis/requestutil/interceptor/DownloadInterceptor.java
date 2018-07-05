package com.kejis.requestutil.interceptor;

/**
 * ClassName:	DownloadInterceptor
 * Function:	${TODO} okhtttp下载拦截，添加下载进度监听
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 17:13
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */

import com.kejis.requestutil.listenter.DownloadResponseBody;
import com.kejis.requestutil.callback.DownloadProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private DownloadProgressListener listener;

    public DownloadInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), listener))
                .build();
    }
}
