package com.kejis.requestutil.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kejis.requestutil.R;
import com.kejis.requestutil.bean.DownBean;
import com.kejis.requestutil.callback.HttpDownOnNextListener;
import com.kejis.requestutil.db.DownState;
import com.kejis.requestutil.http.HttpDownManager;
import com.kejis.requestutil.http.HttpExceptionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ClassName:	DownListAdapter
 * Function:	${TODO} 描述这个类的作用
 * Reason:	${TODO} ADD REASON(可选)
 * Date:	2018/6/27 11:25
 *
 * @author 孙科技
 * @version ${TODO}
 * @see
 * @since JDK 1.8
 */
public class DownListAdapter extends RecyclerView.Adapter {
    /*承接上下文*/
    private Context mContext;
    /*下载列表数据*/
    public List<DownBean> list;

    /**
     * 初始化
     *
     * @param context
     * @param list
     */
    public DownListAdapter(Context context, List<DownBean> list) {
        this.mContext = context;
        this.list = list;
    }

    /**
     * 更新
     *
     * @param list
     */
    public void updataList(List<DownBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_mf_down, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final @NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemViewHolder) {
            final DownBean downBean = list.get(i);

            ((ItemViewHolder) viewHolder).btnRxDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downBean.getState() != DownState.FINISH)
                        HttpDownManager.getInstance().startDown(downBean);
                }
            });
            ((ItemViewHolder) viewHolder).btnRxPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downBean.getState() != DownState.FINISH)
                        HttpDownManager.getInstance().pause(downBean);
                }
            });

            ((ItemViewHolder) viewHolder).progressBar.setMax((int) downBean.getApkTotalLength());
            ((ItemViewHolder) viewHolder).progressBar.setProgress((int) downBean.getApkDownLength());
            /*第一次加载，恢复上次保存的状态 */
            switch (downBean.getState()) {
                case START:
                    /*起始状态*/
                    break;
                case PAUSE:
                    ((ItemViewHolder) viewHolder).tvMsg.setText("暂停中");
                    break;
                case DOWN:
                    HttpDownManager.getInstance().startDown(downBean);
                    break;
                case STOP:
                    ((ItemViewHolder) viewHolder).tvMsg.setText("下载停止");
                    break;
                case ERROR:
                    ((ItemViewHolder) viewHolder).tvMsg.setText("下载错误");
                    break;
                case FINISH:
                    ((ItemViewHolder) viewHolder).tvMsg.setText("下载完成");
                    break;
            }
            /* 监听下载状态，下载回调 */
            downBean.setListener(new HttpDownOnNextListener() {
                @Override
                public void updateProgress(long readLength, long countLength) {
                    ((ItemViewHolder) viewHolder).tvMsg.setText("提示:下载中");
                    ((ItemViewHolder) viewHolder).progressBar.setMax((int) countLength);
                    ((ItemViewHolder) viewHolder).progressBar.setProgress((int) readLength);
                }

                @Override
                public void onNext(Object o) {

                }

                @Override
                public void onStart() {
                    ((ItemViewHolder) viewHolder).tvMsg.setText("提示:开始下载");
                }

                @Override
                public void onComplete() {
                    ((ItemViewHolder) viewHolder).tvMsg.setText("提示:下载完成");
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    ((ItemViewHolder) viewHolder).tvMsg.setText("失败:" + e.toString());
                    HttpExceptionUtil.switchException(mContext, e);
                }


                @Override
                public void onPuase() {
                    super.onPuase();
                    ((ItemViewHolder) viewHolder).tvMsg.setText("提示:暂停");
                }

                @Override
                public void onStop() {
                    super.onStop();
                    ((ItemViewHolder) viewHolder).tvMsg.setText("提示:删除");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_rx_down)
        Button btnRxDown;
        @BindView(R.id.btn_rx_pause)
        Button btnRxPause;
        @BindView(R.id.tv_msg)
        TextView tvMsg;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
//            R.layout.item_mf_down
            ButterKnife.bind(this, itemView);
        }
    }
}
