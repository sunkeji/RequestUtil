package com.kejis.requestutil.http;


import android.os.Environment;

import com.kejis.requestutil.bean.BaseBean;
import com.kejis.requestutil.callback.CommonResponseSubscriber;
import com.kejis.requestutil.callback.CommonProgressSubscriber;
import com.kejis.requestutil.listenter.UploadRequestBody;
import com.skj.wheel.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * ClassName:	MyRequest
 * Function:	${TODO} 封装rxjava请求订阅回调
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/25 10:01
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class MyRequest {

    /**
     * 统一请求处理，网路请求成功统一处理返回结果成功和失败的数据
     *
     * @param observable
     * @param subscriber
     * @return
     */
    public static <T> Flowable post(Flowable observable, CommonResponseSubscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())//  io操作的线程, 通常io操作,如文件读写
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(subscriber);
        return observable;
    }

    /**
     * 统一请求处理，网路请求成功统一处理返回结果成功和失败的数据
     *
     * @param observable
     * @return
     */
    public static <T> Flowable down(Flowable observable, CommonProgressSubscriber subscriber) {
        observable.subscribeOn(Schedulers.io())//  io操作的线程, 通常io操作,如文件读写
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .map(new Function<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody apply(ResponseBody responseBody) throws Exception {
                        LogUtil.i(responseBody.contentLength() + ">>>>>");
                        return responseBody;
                    }
                })
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        LogUtil.i(responseBody.contentLength() + "");
                        writeCaches(responseBody, new File(Environment.getExternalStoragePublicDirectory
                                (Environment.DIRECTORY_DOWNLOADS), "shanchuan.apk"));
                    }
                }).subscribe(subscriber);
        return observable;
    }

    /**
     * 写入文件，保存到sdcard
     *
     * @param file
     * @throws IOException
     */
    public static void writeCaches(ResponseBody responseBody, File file) {
        try {
            RandomAccessFile randomAccessFile = null;
            FileChannel channelOut = null;
            InputStream inputStream = null;
            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                long allLength = responseBody.contentLength();

                inputStream = responseBody.byteStream();
                randomAccessFile = new RandomAccessFile(file, "rwd");
                channelOut = randomAccessFile.getChannel();
                MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                        0, allLength);
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    mappedBuffer.put(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 批量上传图片
     *
     * @param files
     * @return
     */
    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData
                    ("images", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }


    public static MultipartBody filesToMultipartBody(List<File> files, final CommonProgressSubscriber subscriber) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("uid", "4123");
//        for (File file : files) {
//            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//            builder.addFormDataPart("file", file.getName(), requestBody);
//        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),
                files.get(0));
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody, subscriber);
        builder.addFormDataPart("file", files.get(0).getName(), uploadRequestBody);
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }
}
