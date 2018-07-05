package com.kejis.requestutil.api;

import com.kejis.requestutil.bean.BaseBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * ClassName:	TestServiceApi
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 17:07
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public interface TestServiceApi {
    /**
     * 获取物流信息
     *
     * @param map
     * @return
     */
    @GET("query")
    Flowable<BaseBean> testUrl1(@QueryMap Map<String, Object> map);

    /**
     * 批量上传格式
     *
     * @return
     */
    @Multipart
    @POST("upload")
    Flowable<Object> uploadImage(@Query("uid") String uid, @Part List<MultipartBody.Part> images);

    /**
     * 单个上传格式
     *
     * @param images
     * @return
     */
    @POST("file/upload")
    Flowable<Object> rxUploadImage(@Body MultipartBody images);

    /*断点续传下载接口*/
    @Streaming/*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET
    Flowable<ResponseBody> download(@Header("RANGE") String start, @Url String url);
}
