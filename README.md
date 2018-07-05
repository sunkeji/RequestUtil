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
//                    .sslSocketFactory(SSLValidate.getSSLSocketFactory())
//                    .hostnameVerifier(SSLValidate.getHostnameVerifier())//https连接添加安全认证
                    .cache(cache)  //禁用okhttp自身的的缓存
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)//连接、读、写超时时间设置
                    .retryOnConnectionFailure(true)//错误重连
//                    .addInterceptor(new MyInterceptor())
//                    .addNetworkInterceptor(new DownloadInterceptor())//添加拦截器
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