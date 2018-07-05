package com.kejis.requestutil.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.kejis.requestutil.R;

/**
 * ClassName:	DialogUtil
 * Function:	${TODO} 网络请求自定义loading view
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 9:56
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class DialogUtil {
    /**
     * 单列
     */
    private static DialogUtil dialogUtil;

    public static DialogUtil getInstance(Activity activity) {
        if (dialogUtil == null || activity != dialogUtil.activity)
            dialogUtil = new DialogUtil(activity);
        return dialogUtil;
    }

    public Activity activity;

    /**
     * 初始化dialog
     *
     * @param activity
     */
    public DialogUtil(Activity activity) {
        this.activity = activity;
        initDialog();
    }

    private Dialog dialog;

    public void initDialog() {
        dialog = new Dialog(activity, R.style.dialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
    }

    /**
     * 显示dialog loading
     */
    public void startLoading() {
        dialog.show();
    }

    /**
     * 关闭dialogloading
     */
    public void closeLoading() {
        dialog.dismiss();
    }

}
