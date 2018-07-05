package com.kejis.requestutil.callback;

/**
 * ClassName:	HttpDownOnNextListener
 * Function:	${TODO} 网络请求时各状态监听操作
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/2 17:14
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public abstract class HttpDownOnNextListener<T> {


    /**
     * 开始
     */
    public void onStart() {
    }


    /**
     * 数据传输进度（上行和下行）
     *
     * @param readLength
     * @param countLength
     */
    public abstract void updateProgress(long readLength, long countLength);

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     *
     * @param e
     */
    public void onError(Throwable e) {

    }

    /**
     * 完成
     */
    public void onComplete() {
    }

    /**
     * 返回结果
     *
     * @param t
     */
    public abstract void onNext(T t);

    /**
     * 暂停
     */
    public void onPuase() {

    }

    /**
     * 停止销毁
     */
    public void onStop() {

    }

}
