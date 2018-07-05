package com.kejis.requestutil.bean;

import com.kejis.requestutil.api.TestServiceApi;
import com.kejis.requestutil.callback.HttpDownOnNextListener;
import com.kejis.requestutil.db.DownState;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * ClassName:	DownBean
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/4 16:56
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
@Entity
public class DownBean {
    @Id
    private long id;
    /*文件名*/
    private String apkName;
    /*文件图片*/
    private String apkLogo;
    /*文件储存路径*/
    private String apkSavePath;
    /*文件总大小*/
    private long apkTotalLength;
    /*文件已下载大小*/
    private long apkDownLength;
    /*文件下载状态*/
    private int apkDownState;
    /*文件下载地址*/
    private String apkUrl;
    /*是否需要实时更新下载进度,避免线程的多次切换*/
    private boolean updateProgress;
    @Transient
    private TestServiceApi service;
    /*回调监听*/
    @Transient
    private HttpDownOnNextListener listener;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkLogo() {
        return apkLogo;
    }

    public void setApkLogo(String apkLogo) {
        this.apkLogo = apkLogo;
    }

    public String getApkSavePath() {
        return apkSavePath;
    }

    public void setApkSavePath(String apkSavePath) {
        this.apkSavePath = apkSavePath;
    }

    public long getApkTotalLength() {
        return apkTotalLength;
    }

    public void setApkTotalLength(long apkTotalLength) {
        this.apkTotalLength = apkTotalLength;
    }

    public long getApkDownLength() {
        return apkDownLength;
    }

    public void setApkDownLength(long apkDownLength) {
        this.apkDownLength = apkDownLength;
    }

    public int getApkDownState() {
        return apkDownState;
    }

    public void setApkDownState(int apkDownState) {
        this.apkDownState = apkDownState;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public boolean isUpdateProgress() {
        return updateProgress;
    }

    public void setUpdateProgress(boolean updateProgress) {
        this.updateProgress = updateProgress;
    }

    public boolean getUpdateProgress() {
        return this.updateProgress;
    }

    public TestServiceApi getService() {
        return service;
    }

    public void setService(TestServiceApi service) {
        this.service = service;
    }

    public HttpDownOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpDownOnNextListener listener) {
        this.listener = listener;
    }

    @Generated(hash = 1212166558)
    public DownBean(long id, String apkName, String apkLogo, String apkSavePath,
                    long apkTotalLength, long apkDownLength, int apkDownState,
                    String apkUrl, boolean updateProgress) {
        this.id = id;
        this.apkName = apkName;
        this.apkLogo = apkLogo;
        this.apkSavePath = apkSavePath;
        this.apkTotalLength = apkTotalLength;
        this.apkDownLength = apkDownLength;
        this.apkDownState = apkDownState;
        this.apkUrl = apkUrl;
        this.updateProgress = updateProgress;
    }

    @Generated(hash = 2113458446)
    public DownBean() {
    }

    /**
     * 记录下载状态
     *
     * @return
     */
    public DownState getState() {
        switch (getApkDownState()) {
            case 0:
                return DownState.START;
            case 1:
                return DownState.DOWN;
            case 2:
                return DownState.PAUSE;
            case 3:
                return DownState.STOP;
            case 4:
                return DownState.ERROR;
            case 5:
            default:
                return DownState.FINISH;
        }
    }

    public void setState(DownState state) {
        setApkDownState(state.getState());
    }
}
