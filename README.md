# okhttp3+retrofit2+rxjava2+greendao3.3+Gson框架组成的网络请求框架，包含网络请求数据处理、文件上传待上传进度、文件下载（下载、多文件下载断点续传） #

## okhttp3封装 ##
    ` /**
     * 初始化OkHttp3
     */
    private static OkHttpClient okHttpClient;
    private static long timeout = 10 * 6;//网路连接、读、写超时时间设置
    /**
     * 设置缓存目录和大小
     */
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(new File(FILE_SDCARD), cacheSize);

    /**
     * 配置okhttp 请求
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)  //禁用okhttp自身的的缓存
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)//连接、读、写超时时间设置
                    .retryOnConnectionFailure(true)//错误重连
                   .addInterceptor(new MyInterceptor())
                  .addNetworkInterceptor(new DownloadInterceptor())//添加拦截器
                    .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            LogUtil.i("okhttp:", "Url=" + message);
                        }
                    }).setLevel(HttpLoggingInterceptor.Level.BODY))//日志拦截器
                    .build();
        }
        return okHttpClient;
    }`

 *自定义配置*

    ` OkHttpClient.Builder builder = MyOkHttpClient.getOkHttpClient().newBuilder();
      /*添加拦截器（在项目中的interceptor文件下封装多种拦截器可自定义使用）*/
      builder.addNetworkInterceptor(interceptor);
      /*https连接添加安全认证*/
      builder.sslSocketFactory(SSLValidate.getSSLSocketFactory())
      builder..hostnameVerifier(SSLValidate.getHostnameVerifier())`
 

    
## retrofit2封装 ##
    `  /**
     * 初始化Retrofit
     */
    private static Retrofit retrofit;
    //对数据解析做一些转化
    private static Gson gson = new GsonBuilder().
            setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

    /**
     * 配置Retrofit参数 默认MyOkHttpClient请求配置
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit getRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(MyOkHttpClient.getOkHttpClient())//设置使用okhttp网络请求
                .baseUrl(baseUrl)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create(gson))//添加gosn转换器数据解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())//添加回调库，对RxJava的支持
                //也可以添加自定义的RxJavaCallAdapterFactory
                .build();
        return retrofit;
    }`
    ` /**
     * 配置Retrofit参数 重新定义MyOkHttpClient请求配置
     *
     * @param baseUrl
     * @param okHttpClient
     * @return
     */
    public static Retrofit getRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(baseUrl)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create(gson))//添加gosn转换器数据解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())//添加回调库，对RxJava的支持
                //也可以添加自定义的RxJavaCallAdapterFactory
                .build();
        return retrofit;
    }`
## rxjava2封装 ##
    `  /**
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
    }`
     
## 请求回调 ##
    `/**
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
}`
## 带进度的请求回调 ##
    `/**
      * ClassName:	CommonDownSubscriber
      * Function:	${TODO} 监听上传和下载请求回调
      * Reason:	${TODO} ADD REASON(可选)
      * Date:	2018/7/3 9:16
      *
      * @author 孙科技
      * @version ${TODO}
      * @see
      * @since JDK 1.8
      */
    public class CommonProgressSubscriber<T> extends CommonResponseSubscriber<T>
        implements UploadProgressListener, DownloadProgressListener {
    /*弱引用结果回调*/
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    /* handler更新数据 */
    private Handler handler;

    public CommonProgressSubscriber(HttpDownOnNextListener listener) {
        mSubscriberOnNextListener = new SoftReference<>(listener);
        this.handler = new Handler(Looper.getMainLooper());
    }

    public CommonProgressSubscriber(Activity activity, HttpDownOnNextListener listener) {
        super(activity);
        mSubscriberOnNextListener = new SoftReference<>(listener);
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 上传进度监听回调
     *
     * @param read
     * @param count
     * @param done
     */
    @Override
    public void update(final long read, final long count, boolean done) {
        if (mSubscriberOnNextListener.get() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                    mSubscriberOnNextListener.get().updateProgress(read, count);
                }
            });
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }
    /**
     * 下载进度监听回调
     * @param readLength
     * @param contentLength
     * @param done
     */
    @Override
    public void down(final long readLength, final long contentLength, boolean done) {
        if (mSubscriberOnNextListener.get() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                    mSubscriberOnNextListener.get().updateProgress(readLength, contentLength);
                }
            });
        }
    }
}`
根据需求和UI，DialogUtil可做定制化开发，自定义开发界面
## 网路请求 ##
  ![avatar](/1531446090.png)
  ![avatar](/1531448013.png)
  ![avatar](/1531448012.png)
  **基本网络请求**

    `/**
     * 测试请求接口，物流信息
     */
     private void getRequest() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "yuantong");
        map.put("postid", "11111111111");
        TestRequest.getRequest(map, new CommonResponseSubscriber<BaseBean>(this) {
            @Override
            public void onNext(BaseBean baseBean) {
                LogUtil.i("result:" + new Gson().toJson(baseBean).toString());
                tvMsg.setText("返回结果：" + new Gson().toJson(baseBean).toString());
            }
        });
    }`
  
   **图片上传**

    ` /**
       * 上传图片测试接口
       */
    private void upload() {
        List<File> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            File outputFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "" + "image1.jpg");
            images.add(outputFile);
        }
        TestRequest.uploadImage(this, images, new HttpDownOnNextListener() {
            @Override
            public void onNext(Object o) {
                tvMsg.setText("请求返回结果：" + new Gson().toJson(o).toString());
            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                tvMsg.setText("请求返回结果：" + readLength + "-" + countLength);
            }
        });
    }`

   **文件下载**

    `/**
     * 单文件下载测试接口
     */
    private void down() {
        TestRequest.getDown(new HttpDownOnNextListener() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                tvMsg.setText("readLength：" + readLength + "-countLength：" + countLength);
            }
        });
    }`
## 多文件下载之断点续传 ##

   *使用greenDao作为数据库储存（如不了解greenDao请查资料）和使用封装的SwipeRefreshLayout+RecyclerView列表（下面会详细介绍）*

    `public class HttpDownManager {
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
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        LogUtil.i(responseBody.contentLength() + "");
                        writeCaches(responseBody, new File(downBean.getApkSavePath()));
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
     * 停止全部下载
     */
    public void stopAllDown() {
        for (DownBean downInfo : downBeans) {
            stopDown(downInfo);
        }
        subMap.clear();
        downBeans.clear();
    }

    /**
     * 停止下载
     */
    public void stopDown(DownBean info) {
        if (info == null) return;
        info.setState(DownState.STOP);
        info.getListener().onStop();
        if (subMap.containsKey(info.getApkUrl())) {
            CommonDownSubscriber subscriber = subMap.get(info.getApkUrl());
            subscriber.dispose();
            subMap.remove(info.getApkUrl());
        }
        /*保存数据库信息和本地文件*/
        db.update(info);
    }

    /**
     * 暂停全部下载
     */
    public void pauseAll() {
        for (DownBean downInfo : downBeans) {
            pause(downInfo);
        }
        subMap.clear();
        downBeans.clear();
    }


    /**
     * 返回全部正在下载的数据
     *
     * @return
     */
    public Set<DownBean> getDownInfos() {
        return downBeans;
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
    }`
*封装的HttpDownManager类，实现数据库保存、下载文件储存在SD卡、下载暂停等功能。*

   **多文件下载请求**

    ` /**
     * 多文件下载测试数据
     */
    private void getList() {
        dbUtil = DbUtil.getInstance();
        listData = dbUtil.queryAllList();
        if (listData.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                File outputFile = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS), "test" + i + ".apk");
                DownBean downBean = new DownBean();
                if (i == 0)
                    downBean.setApkUrl("http://imtt.dd.qq.com/16891/1003ECB6536079D8BE30B99D07D3B106.apk");
                else
                    downBean.setApkUrl("http://imtt.dd.qq.com/16891/D7C2338128FB42016E1116F017BF31F0.apk");
                downBean.setId(i);
                downBean.setUpdateProgress(true);
                downBean.setApkSavePath(outputFile.getAbsolutePath());
                dbUtil.save(downBean);
            }
            listData = dbUtil.queryAllList();
        }
        adapter = new DownListAdapter(this, listData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*记录退出时下载任务的状态-复原用*/
        for (DownBean downInfo : listData) {
            dbUtil.update(downInfo);
        }
    }`

>总结RequestUtil项目封装okhttp3+retrofit2+rxjava2+greendao3.3+Gson框架组成的网络请求框架，可根据上述说明了解该框架的重要类和方法，具体的一些关联类在项目中查找了解


# 封装的SwipeRefreshLayout+RecyclerView列表+FileUtil文件管理框架+Glide图片处理框架  #

 *gradle配置引入implementation 'com.wheel:tools:1.1.5'*

## SwipeRefreshLayout+RecyclerView列表使用 ##

*xml资源引用*

    `  <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
        <!--带下拉刷新、上拉加载更多列表-->
        <include layout="@layout/recycler_view_linear_v_refresh" />
        <!--垂直列表-->
        <include layout="@layout/recycler_view_linear_v" />
        <!--水平列表-->
        <include layout="@layout/recycler_view_linear_h" />
    </LinearLayout>
`

*activity使用*

    MyRecyclerView recyclerView;

    MySwipeRLView swipeRefresh;

    `   /*下拉刷新（第一次默认自动加载）*/
        swipeRefresh.setOnSwipeListener(new MySwipeRLView.OnSwipeListener() {
            @Override
            public void onRefresh() {
             // listData.clear();
             //getList();
             // adapter.updataList(listData);
            }
        });
        /*上拉加载更多（滑动到底部自动加载）*/
        recyclerView.setOnBottomListener(new MyRecyclerView.OnBottomListener() {
            @Override
            public void onLoadMore() {
             // getList();
             // adapter.updataList(listData);
            }
        });`

MyRecyclerView类封装了部分属性包括自定义分割线、移除分割线等等

## FileUtil文件管理框架 ##

![avatar](/1531452424.png)

FileUtil类
封装新建文件、新建文件夹、删除文件、删除文件夹、获取文件夹下的所有文件、计算系统内存大小、文件大小、文件夹大小、获取文件类型、获取系统储存路径、打开文件等等

## Glide图片处理框架 ##

![avatar](/1531453032.png)

使用方法

    ` GlideUtil.getInstance().ImageCircleLoader(this, imageView,
                "http://img0.imgtn.bdimg.com/it/u=3565185884,2248353566&fm=27&gp=0.jpg");`

GlideUtil类封装加载图片、加载圆形图片等Transformation转换图片处理的方法，封装代码自定义使用

   