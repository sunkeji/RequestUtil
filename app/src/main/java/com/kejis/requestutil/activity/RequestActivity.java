package com.kejis.requestutil.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kejis.requestutil.R;
import com.kejis.requestutil.api.TestRequest;
import com.kejis.requestutil.bean.BaseBean;
import com.kejis.requestutil.callback.CommonResponseSubscriber;
import com.skj.wheel.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ClassName:	RequestActivity
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/29 17:03
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class RequestActivity extends AppCompatActivity {
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);

        btn.setText("请求接口");
    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        getRequest();
    }

    private void getRequest() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "yuantong");
        map.put("postid", "11111111111");
        TestRequest.getRequest(map, new CommonResponseSubscriber<BaseBean>() {
            @Override
            public void onNext(BaseBean baseBean) {
                LogUtil.i("result:" + new Gson().toJson(baseBean).toString());
                tvMsg.setText("返回结果：" + new Gson().toJson(baseBean).toString());
            }
        });
    }
}
