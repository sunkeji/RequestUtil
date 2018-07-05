package com.kejis.requestutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kejis.requestutil.activity.DownActivity;
import com.kejis.requestutil.activity.MFDownBCActivity;
import com.kejis.requestutil.activity.RequestActivity;
import com.kejis.requestutil.activity.UploadActivity;
import com.skj.wheel.util.IntentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_request)
    Button btnRequest;
    @BindView(R.id.btn_down)
    Button btnDown;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.btn_down_mf)
    Button btnDownMf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_request, R.id.btn_down, R.id.btn_down_mf, R.id.btn_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_request:
                IntentUtil.startActivity(this, RequestActivity.class);
                break;
            case R.id.btn_down:
                IntentUtil.startActivity(this, DownActivity.class);
                break;
            case R.id.btn_down_mf:
                IntentUtil.startActivity(this, MFDownBCActivity.class);
                break;
            case R.id.btn_upload:
                IntentUtil.startActivity(this, UploadActivity.class);
                break;
        }
    }
}
