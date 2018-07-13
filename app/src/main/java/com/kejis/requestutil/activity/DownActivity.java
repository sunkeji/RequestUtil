package com.kejis.requestutil.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.kejis.requestutil.R;
import com.kejis.requestutil.api.TestRequest;
import com.kejis.requestutil.callback.HttpDownOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ClassName:	DownActivity
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 17:03
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class DownActivity extends AppCompatActivity {
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);
        btn.setText("下载文件");
    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        TestRequest.getDown(new HttpDownOnNextListener() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                tvMsg.setText("readLength：" + readLength + "-countLength：" + countLength);
            }
        });
    }

}
