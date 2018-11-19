package com.guyu.android.gis.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.guyu.android.R;
import com.guyu.android.gis.entity.UpdateMapsEntity;

import java.util.List;

/**
 * Created by YunHuan on 2017/11/6.
 */

public class UpdateAppAdapter extends BaseMultiItemQuickAdapter<UpdateMapsEntity, BaseViewHolder> {
    private String ver;
    private BaseViewHolder helper;


    public UpdateAppAdapter(List<UpdateMapsEntity> list,String ver) {
        super(list);
        this.ver = ver;
        addItemType(UpdateMapsEntity.UPDATE_VIEW, R.layout.item_update_view);
//        addItemType(UpdateMapsEntity.MOVE_VIEW, R.layout.item_move_view);
        addItemType(UpdateMapsEntity.CANCEL_VIEW, R.layout.item_cancel_view);
    }




    @Override
    protected void convert(BaseViewHolder helper, UpdateMapsEntity item) {

        int position = helper.getAdapterPosition();
        switch (item.getItemType()) {
            case UpdateMapsEntity.UPDATE_VIEW:
                helper.addOnClickListener(R.id.btn);
                helper.setText(R.id.tv, "正在下载 GuyuGis_" + ver);
                this.helper = helper;
                break;
//            case UpdateMapsEntity.MOVE_VIEW:
//
//                helper.setText(R.id.tv, "正在移动 " + versions.get(position - versions.size()).getNAMECN() + "_" + versions.get(position - versions.size()).getMAPVER());
//
//                break;
            case UpdateMapsEntity.CANCEL_VIEW:
                helper.addOnClickListener(R.id.btn_cancel);
                break;
        }
    }


    public void updateProgress(int progress) {
        this.helper.setProgress(R.id.pb,progress);
    }

    public void setMaxProgress(int maxProgress) {
        this.helper.setProgress(R.id.pb,0,maxProgress);
    }


}
