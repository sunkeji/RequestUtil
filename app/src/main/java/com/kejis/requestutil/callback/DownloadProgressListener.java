package com.kejis.requestutil.callback;

/**
 * ClassName:	DownloadProgressListener
 * Function:	${TODO} OkHttp未封装下载，添加数据流进度实现下载监听
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/26 16:49
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public interface DownloadProgressListener {
    /**
     * 下载进度
     *
     * @param readLength
     * @param contentLength
     * @param done
     */
    void down(long readLength, long contentLength, boolean done);
}
