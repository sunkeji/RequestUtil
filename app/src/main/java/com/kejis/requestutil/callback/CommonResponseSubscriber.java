package com.kejis.requestutil.callback;

import android.app.Activity;

import com.kejis.requestutil.MyAppcation;
import com.kejis.requestutil.http.HttpExceptionUtil;
import com.kejis.requestutil.util.DialogUtil;
import com.skj.wheel.util.LogUtil;


import io.reactivex.subscribers.ResourceSubscriber;


/**
 * ClassName:	CommonResponseSubscriber
 * Function:	${TODO} 封装请求回调基类，处理请求失败类型、成功，是否展示loading动画
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/25 10:04
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public abstract class CommonResponseSubscriber<T> extends ResourceSubscriber<T> {

    /**
     * 显示网路请求loading
     */
    private DialogUtil dialogUtil;

    public CommonResponseSubscriber(Activity activity) {
        dialogUtil = DialogUtil.getInstance(activity);
    }

    /**
     * 不显示网路请求loading
     */
    public CommonResponseSubscriber() {

    }

    @Override
    public abstract void onNext(T t);

    @Override
    public void onError(Throwable e) {
        if (dialogUtil != null)
            dialogUtil.closeLoading();
        HttpExceptionUtil.switchException(MyAppcation.mContext, e);
    }

    @Override
    public void onComplete() {
        if (dialogUtil != null)
            dialogUtil.closeLoading();
        LogUtil.i("onCompleted");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dialogUtil != null)
            dialogUtil.startLoading();
        LogUtil.i("onStart");
    }
}
