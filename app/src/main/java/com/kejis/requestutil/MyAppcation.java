package com.kejis.requestutil;

import android.app.Application;
import android.content.Context;

/**
 * ClassName:	MyAppcation
 * Function:	${TODO} 启动应用预加载
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 15:33
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class MyAppcation extends Application {
    /**
     * 单例
     */
    private static MyAppcation app;

    public static MyAppcation getInstance() {
        if (app == null)
            app = new MyAppcation();
        return app;
    }

    /* 全局调用，承接上下文*/
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
