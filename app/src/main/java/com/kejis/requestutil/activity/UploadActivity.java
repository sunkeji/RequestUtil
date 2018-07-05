package com.kejis.requestutil.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kejis.requestutil.R;
import com.kejis.requestutil.api.TestRequest;
import com.kejis.requestutil.callback.HttpDownOnNextListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ClassName:	UploadActivity
 * Function:	${TODO} okhttp 上传文件，多个或单个
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 17:04
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class UploadActivity extends AppCompatActivity {
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);
        btn.setText("上传文件");
    }


    @OnClick(R.id.btn)
    public void onViewClicked() {
        upload();
    }

    /**
     * 上传图片测试接口
     */
    private void upload() {
        List<File> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            File outputFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "" + "name2.jpg");
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
    }
}
