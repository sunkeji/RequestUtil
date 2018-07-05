package com.kejis.requestutil.http;

import android.content.Context;
import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.skj.wheel.util.LogUtil;
import com.skj.wheel.util.ToastUtil;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * ClassName:	HttpExceptionUtil
 * Function:	${TODO} 网路请求异常处理
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/4 14:20
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class HttpExceptionUtil {


    public static void switchException(Context context, Throwable e) {
        if (e == null) {
            return;
        }
        LogUtil.e("异常提示:" + e.getMessage() + "-" + e.getLocalizedMessage());
        if (e instanceof HttpException) {     //   HTTP错误
            onException(context, ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(context, ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(context, ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(context, ExceptionReason.PARSE_ERROR);
        } else {
            onException(context, ExceptionReason.UNKNOWN_ERROR);
        }
    }

    /**
     * 请求异常
     *
     * @param context
     * @param reason
     */
    public static void onException(Context context, ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                ToastUtil.TextToast(context, "连接错误");
                break;
            case CONNECT_TIMEOUT:
                ToastUtil.TextToast(context, "连接超时");
                break;
            case BAD_NETWORK:
                ToastUtil.TextToast(context, "网络问题");
                break;
            case PARSE_ERROR:
                ToastUtil.TextToast(context, "解析数据失败");
                break;
            case UNKNOWN_ERROR:
            default:
                ToastUtil.TextToast(context, "未知错误");
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
