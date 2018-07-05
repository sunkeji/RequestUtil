package com.kejis.requestutil.callback;

/**
 * ClassName:	DownloadProgressListener
 * Function:	${TODO} OkHttp未封装上传，添加数据流进度实现上传监听
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/26 16:49
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public interface UploadProgressListener {
    /**
     * 上传进度
     *
     * @param writeLength
     * @param contentLength
     * @param done
     */
    void update(long writeLength, long contentLength, boolean done);
}
