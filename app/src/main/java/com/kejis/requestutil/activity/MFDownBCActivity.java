package com.kejis.requestutil.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kejis.requestutil.R;
import com.kejis.requestutil.adapter.DownListAdapter;
import com.kejis.requestutil.bean.DownBean;
import com.kejis.requestutil.db.DbUtil;
import com.skj.wheel.definedview.LayoutView;
import com.skj.wheel.swiperecyclerview.MyRecyclerView;
import com.skj.wheel.swiperecyclerview.MySwipeRLView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ClassName:	MFDownBCActivity
 * Function:	${TODO} 列表下载界面
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/7/4 16:49
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class MFDownBCActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    MyRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    MySwipeRLView swipeRefresh;
    @BindView(R.id.layout_view)
    LayoutView layoutView;

    private List<DownBean> listData = new ArrayList<>();
    private DbUtil dbUtil;

    private DownListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mf_down);
        ButterKnife.bind(this);
        swipeRefresh.setOnSwipeListener(new MySwipeRLView.OnSwipeListener() {
            @Override
            public void onRefresh() {
//                listData.clear();
//                getList();
//                adapter.updataList(listData);
            }
        });
        recyclerView.setOnBottomListener(new MyRecyclerView.OnBottomListener() {
            @Override
            public void onLoadMore() {
//                getList();
//                adapter.updataList(listData);
            }
        });
        getList();
    }

    private void getList() {
        dbUtil = DbUtil.getInstance();
        listData = dbUtil.queryAllList();
        if (listData.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                File outputFile = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS), "test" + i + ".mp4");
                DownBean downBean = new DownBean();
                if (i == 0)
                    downBean.setApkUrl("http://47.104.135.91/yc.apk");
                else
                    downBean.setApkUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
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
    }
}
