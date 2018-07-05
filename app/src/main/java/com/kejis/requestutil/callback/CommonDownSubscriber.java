package com.kejis.requestutil.callback;

import android.os.Handler;
import android.os.Looper;

import com.kejis.requestutil.MyAppcation;
import com.kejis.requestutil.bean.DownBean;
import com.kejis.requestutil.callback.DownloadProgressListener;
import com.kejis.requestutil.callback.HttpDownOnNextListener;
import com.kejis.requestutil.db.DbUtil;
import com.kejis.requestutil.db.DownState;
import com.kejis.requestutil.http.HttpDownManager;
import com.kejis.requestutil.http.HttpExceptionUtil;
import com.skj.wheel.util.LogUtil;

import java.lang.ref.SoftReference;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * ClassName:	CommonDownSubscriber
 * Function:	${TODO} 多任务下载断点续传监听回调
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 9:16
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class CommonDownSubscriber<T> extends ResourceSubscriber<T>
        implements DownloadProgressListener {
    //弱引用结果回调
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    /*下载进度回掉主线程*/
    private Handler handler;
    private DownBean downBean;

    public CommonDownSubscriber(DownBean downBean) {
        mSubscriberOnNextListener = new SoftReference<>(downBean.getListener());
        this.handler = new Handler(Looper.getMainLooper());
        this.downBean = downBean;
    }

    public void setDownBean(DownBean downBean) {
        this.mSubscriberOnNextListener = new SoftReference<>(downBean.getListener());
        this.downBean = downBean;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void down(long read, long count, boolean done) {
        if (downBean.getApkTotalLength() > count)
            read = downBean.getApkTotalLength() - count + read;
        else
            downBean.setApkTotalLength(count);
        downBean.setApkDownLength(read);
        if (mSubscriberOnNextListener.get() == null || !downBean.isUpdateProgress()) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                if (downBean.getState() == DownState.PAUSE || downBean.getState() == DownState.STOP)
                    return;
                downBean.setState(DownState.DOWN);
                mSubscriberOnNextListener.get().updateProgress(downBean.getApkDownLength(), downBean.getApkTotalLength());
            }
        });
    }

    @Override
    public void onNext(T t) {
        LogUtil.i("---" + t.toString());
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }

    @Override
    public void onError(Throwable t) {
        LogUtil.i("---onError");
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onError(t);
        }
        HttpExceptionUtil.switchException(MyAppcation.mContext, t);
        //关闭下载请求
        HttpDownManager.getInstance().remove(downBean);
        downBean.setState(DownState.ERROR);
        DbUtil.getInstance().update(downBean);
    }

    @Override
    public void onComplete() {
        LogUtil.i("---onComplete");
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onComplete();
        }
        //关闭下载请求
        HttpDownManager.getInstance().remove(downBean);
        downBean.setState(DownState.FINISH);
        DbUtil.getInstance().update(downBean);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i("---onStart");
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onStart();
        }
        downBean.setState(DownState.START);
    }
}
