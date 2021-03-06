package com.tencent.live.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.dialog.UIDialog;
import com.tcj.sunshine.ui.viewgroup.IndexerView;
import com.tencent.live.R;
import com.tencent.live.adapter.LiveRoomGiftAdapter;
import com.tencent.live.entity.LiveGiftEntity;
import com.tencent.live.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 直播工具弹窗
 */
public class LiveRoomGiftDialog {

    private Context context;

    private View dialogView;
    private UIDialog dialog;
    private LayoutInflater inflater;

    private ViewPager mVpGift;
    private IndexerView mIndexrView;
    private GiftPagerAdapter adapter;
    private List<View> viewList = new ArrayList<>();

    private static final int PAGE_MAX_NUM = 8;//一页8个

    public OnGiftItemDoubleClickListener mListener;


    public LiveRoomGiftDialog(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dialogView = inflater.inflate(R.layout.dialog_live_gift, null, false);
        this.dialog = new UIDialog.Builder(context)
                .setContentView(dialogView)
                .setStyleId(R.style.ThemeStyleUIDailog_Transparent)
                .setWidth(ScreenUtils.getScreenWidth())
                .setGravity(Gravity.BOTTOM)
                .setWindowAnimations(R.style.common_animation_style)
                .build();

        this.mVpGift = this.dialogView.findViewById(R.id.vp_gift);
        this.mIndexrView = this.dialogView.findViewById(R.id.indexr_view);

        this.mVpGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndexrView.updateIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setData(List<LiveGiftEntity> data){
        this.viewList.clear();
        int page = (int)Math.ceil(data.size() / 8.0f);
        for (int i = 0; i < page; i++) {

            int start = i * PAGE_MAX_NUM;
            int end = (i + 1) * PAGE_MAX_NUM;
            if(end > data.size()) end = data.size();

            List<LiveGiftEntity> childData = new ArrayList<>();
            for (int j = start; j < end; j++) {
                childData.add(data.get(j));
            }
            View itemView = this.getItemView(i, childData);
            viewList.add(itemView);
        }

        this.mIndexrView.setNoramlIndexLayoutId(R.layout.view_index_gray_normal)
                .setCheckedIndexLayoutId(R.layout.view_index_orange_checked)
                .setIndexSize(page)
                .build();

        this.adapter = new GiftPagerAdapter(viewList, context);
        this.mVpGift.setAdapter(adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void setOnGiftItemDoubleClickListener(OnGiftItemDoubleClickListener mListener) {
        this.mListener = mListener;
    }

    private View getItemView(int page, List<LiveGiftEntity> childData) {
        View itemView = inflater.inflate(R.layout.adapter_live_gift_item, null);
        GridView mGvGift = itemView.findViewById(R.id.gv_gift);
        LiveRoomGiftAdapter adapter = new LiveRoomGiftAdapter(context, childData, new OnItemClickListener<LiveGiftEntity>() {
            @Override
            public void onItemClick(View view, int position, LiveGiftEntity entity) {
                if(mListener != null) {
                    mListener.onDoubleClick(view, position, entity);
                }
            }
        });
        mGvGift.setAdapter(adapter);
        return  itemView;
    }

    public void show(){
        if(dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void cancel(){
        if(dialog != null) {
            dialog.cancel();
        }
    }

    private class GiftPagerAdapter extends PagerAdapter{


        private List<View> views;
        private Context context;

        public GiftPagerAdapter(List<View> views,Context context){
            this.views = views;
            this.context = context;
        }

        @Override
        public int getCount() {
            return views.size();
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = views.get(position);
            container.addView(itemView);
            return itemView;
        }
    }

    public interface OnGiftItemDoubleClickListener {

        void onDoubleClick(View v, int position, LiveGiftEntity item);
    }
}
