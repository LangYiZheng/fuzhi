package com.guyu.android.gis.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.guyu.android.R;
import com.guyu.android.gis.adapter.UpdateAppAdapter;
import com.guyu.android.gis.adapter.UpdateMapsAdapter;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.entity.UpdateMapsEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YunHuan on 2017/11/6.
 */

public class UpdateAppDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private UpdateAppAdapter adapter;

    private LoginActivity activity;
    private String version;

    public void setActivity(LoginActivity activity, String version) {
        this.activity = activity;
        this.version = version;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_maps, container);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        initAdapter();
        addOnClickListener();
        return view;
    }

    private void addOnClickListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.btn_cancel:
                        UpdateAppDialogFragment.this.dismiss();
                        LoginActivity.setStartDownloadApp(false);
                        activity.presenter.downloadStop();
                        break;
                    case R.id.btn:
                        Object o = view.getTag();
                        boolean isContinue = false;
                        if (o != null) {
                            isContinue = (boolean) view.getTag();
                        }
                        Button b = (Button) view;
                        if (isContinue) {
                            b.setText("暂停");
                            activity.presenter.downloadContinue();
                        } else {
                            b.setText("继续");
                            activity.presenter.downloadPause();

                        }
                        view.setTag(!isContinue);
                        break;
                }

            }
        });
    }

    private void initAdapter() {
        List<UpdateMapsEntity> data = new ArrayList<>();
        data.add(new UpdateMapsEntity(UpdateMapsEntity.UPDATE_VIEW));
        data.add(new UpdateMapsEntity(UpdateMapsEntity.CANCEL_VIEW));
        adapter = new UpdateAppAdapter(data,version);
        adapter.openLoadAnimation();
        mRecyclerView.setAdapter(adapter);
    }

    public void updateProgress(int progress) {
        adapter.updateProgress(progress);
    }

    public void setMaxProgress(int maxProgress) {
        adapter.setMaxProgress(maxProgress);
    }
}
