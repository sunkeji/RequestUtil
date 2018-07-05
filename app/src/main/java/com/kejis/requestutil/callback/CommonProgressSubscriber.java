package com.kejis.requestutil.callback;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.SoftReference;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * ClassName:	CommonDownSubscriber
 * Function:	${TODO} 监听上传和下载请求回调
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 9:16
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class CommonProgressSubscriber<T> extends CommonResponseSubscriber<T>
        implements UploadProgressListener, DownloadProgressListener {
    /*弱引用结果回调*/
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    /* handler更新数据 */
    private Handler handler;

    public CommonProgressSubscriber(HttpDownOnNextListener listener) {
        mSubscriberOnNextListener = new SoftReference<>(listener);
        this.handler = new Handler(Looper.getMainLooper());
    }

    public CommonProgressSubscriber(Activity activity, HttpDownOnNextListener listener) {
        super(activity);
        mSubscriberOnNextListener = new SoftReference<>(listener);
        this.handler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void update(final long read, final long count, boolean done) {
        if (mSubscriberOnNextListener.get() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                    mSubscriberOnNextListener.get().updateProgress(read, count);
                }
            });
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }

    @Override
    public void down(final long readLength, final long contentLength, boolean done) {
        if (mSubscriberOnNextListener.get() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                    mSubscriberOnNextListener.get().updateProgress(readLength, contentLength);
                }
            });
        }
    }
}
