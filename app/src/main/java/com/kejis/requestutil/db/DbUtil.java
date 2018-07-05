package com.kejis.requestutil.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kejis.requestutil.MyAppcation;
import com.kejis.requestutil.bean.DownBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * ClassName:	DbUtil
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/4 16:54
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class DbUtil {
    private static DbUtil db;
    private Context context;
    private final static String dbName = "tests_db";
    private DaoMaster.DevOpenHelper openHelper;

    /**
     * 初始化greenDao
     */
    public DbUtil() {
        this.context = MyAppcation.mContext;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);

    }

    /**
     * 单例
     *
     * @return
     */
    public static DbUtil getInstance() {
        if (db == null) {
            synchronized (DbUtil.class) {
                if (db == null)
                    db = new DbUtil();
            }
        }
        return db;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 针对操作类DownApkInfo，保存
     *
     * @param bean
     */
    public void save(DownBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownBeanDao downInfoDao = daoSession.getDownBeanDao();
        downInfoDao.insert(bean);
    }

    /**
     * 针对操作类DownApkInfo，更新
     *
     * @param bean
     */
    public void update(DownBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownBeanDao downInfoDao = daoSession.getDownBeanDao();
        downInfoDao.update(bean);
    }

    /**
     * 针对操作类DownApkInfo，删除
     *
     * @param info
     */
    public void delete(DownBean info) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownBeanDao downInfoDao = daoSession.getDownBeanDao();
        downInfoDao.delete(info);
    }

    /**
     * 针对操作类DownApkInfo，通过键名查询
     *
     * @param id
     */
    public DownBean query(long id) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownBeanDao downInfoDao = daoSession.getDownBeanDao();
        QueryBuilder<DownBean> qb = downInfoDao.queryBuilder();
        qb.where(DownBeanDao.Properties.Id.eq(id));
        List<DownBean> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 查询所有的数据
     *
     * @return
     */
    public List<DownBean> queryAllList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownBeanDao downInfoDao = daoSession.getDownBeanDao();
        QueryBuilder<DownBean> qb = downInfoDao.queryBuilder();
        return qb.list();
    }
}

