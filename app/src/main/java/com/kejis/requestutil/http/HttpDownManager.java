package com.kejis.requestutil.http;

import com.kejis.requestutil.api.TestServiceApi;
import com.kejis.requestutil.bean.DownBean;
import com.kejis.requestutil.callback.CommonDownSubscriber;
import com.kejis.requestutil.db.DbUtil;
import com.kejis.requestutil.db.DownState;
import com.kejis.requestutil.interceptor.DownloadInterceptor;
import com.skj.wheel.util.NetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * ClassName:	HttpDownManager
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/3 9:15
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class HttpDownManager {
    /*记录下载数据*/
    private Set<DownBean> downBeans;
    /*回调sub队列*/
    private HashMap<String, CommonDownSubscriber> subMap;
    /*单利对象*/
    private volatile static HttpDownManager INSTANCE;
    /*数据库类*/
    private DbUtil db;

    /**
     * 初始化下载配置
     */
    public HttpDownManager() {
        downBeans = new HashSet<>();
        subMap = new HashMap<>();
        db = DbUtil.getInstance();

    }

    /**
     * 单列
     *
     * @return
     */
    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 下载
     */
    public void startDown(final DownBean downBean) {
        /*正在下载不处理*/
        if ((downBean == null || subMap.get(downBean.getApkUrl()) != null)
                && downBean.getState() == DownState.DOWN) {
            subMap.get(downBean.getApkUrl()).setDownBean(downBean);
            return;
        }
        /*添加回调处理类*/
        CommonDownSubscriber subscriber = new CommonDownSubscriber(downBean);
        /*记录回调sub*/
        subMap.put(downBean.getApkUrl(), subscriber);
        /*获取service，多次请求公用一个sercie*/
        TestServiceApi httpService;
        if (downBeans.contains(downBean)) {
            httpService = downBean.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            OkHttpClient.Builder builder = MyOkHttpClient.getOkHttpClient().newBuilder();
            //手动创建一个OkHttpClient并设置超时时间
            builder.addNetworkInterceptor(interceptor);

            httpService = MyRetrofit.getRequest(TestServiceApi.class,
                    NetUtil.getBasUrl(downBean.getApkUrl()), builder.build());
            downBean.setService(httpService);
            downBeans.add(downBean);
        }
        httpService.download("bytes=" + downBean.getApkDownLength() + "-",
                downBean.getApkUrl().replace(NetUtil.getBasUrl(downBean.getApkUrl()), ""))
                .subscribeOn(Schedulers.io())//  io操作的线程, 通常io操作,如文件读写
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .map(new Function<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody apply(ResponseBody responseBody) throws Exception {
                        writeCaches(responseBody, new File(downBean.getApkSavePath()));
                        return responseBody;
                    }
                })
                .subscribe(subscriber);
    }

    /**
     * 暂停下载
     *
     * @param info
     */
    public void pause(DownBean info) {
        if (info == null) return;
        info.setState(DownState.PAUSE);
        info.getListener().onPuase();
        if (subMap.containsKey(info.getApkUrl())) {
            CommonDownSubscriber subscriber = subMap.get(info.getApkUrl());
            subscriber.dispose();
            subMap.remove(info.getApkUrl());
        }
        /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
        db.update(info);
    }

    /**
     * 移除下载数据
     *
     * @param info
     */
    public void remove(DownBean info) {
        subMap.remove(info.getApkUrl());
        downBeans.remove(info);
    }

    /**
     * 写入文件
     *
     * @param file
     * @throws IOException
     */
    public void writeCaches(ResponseBody responseBody, File file) {
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
}
