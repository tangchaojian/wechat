package com.tcj.sunshine.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tcj.sunshine.ui.refresh.footer.BaseFooterView;
import com.tcj.sunshine.ui.refresh.footer.FooterView;
import com.tcj.sunshine.ui.refresh.footer.MYFooterView;

/**
 * 自动加载更多RecyclerView
 */
public class RefreshRecyclerView extends RecyclerView {

    private View mHeaderView;
    private BaseFooterView mFooterView;
    private RecyclerViewAdapter mAdapter;
    //适配器更新数据观察者
    private MRecyclerViewDataObserver mRecyclerViewDataObserver;

    private boolean mNeedLoadMoreEnable = true;//是否能加载更多,默认是
    private boolean hasNext = true;//是否还有下一页
    private long loadDelayMillis = 300;//加载延迟,默认300毫秒
    private Status status = Status.STATUS_NONE;

    private OnLoadMoreListener mListener;
    private OnScrollViewListener mOnScrollViewListener;


    public RefreshRecyclerView(@NonNull Context context) {
        super(context);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 是否可以加载更多
     * @param enable
     */
    public void setNeedLoadMoreEnable(boolean enable){
        this.mNeedLoadMoreEnable = enable;
    }

    public void setOnScrollViewListener(OnScrollViewListener mOnScrollViewListener) {
        this.mOnScrollViewListener = mOnScrollViewListener;
    }

    /**
     * 加载更多数据
     */
    public void loadMore(){
        setStatus(Status.STATUS_LOADING);
        if(mListener != null) {
            mListener.onLoadMore();
        }
    }

    /**
     * 加载更多延迟
     * @param delayMillis
     */
    public void loadMore(long delayMillis){
        setStatus(Status.STATUS_LOADING);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }, delayMillis);
    }

    /**
     * 完成加载
     */
    public void completeLoad(){
        this.completeLoad(true);
    }
    /**
     * 完成加载
     * @param hasNext 是否还有下一页
     */
    public void completeLoad(boolean hasNext){
        this.hasNext = hasNext;
        if(this.hasNext) {
            setStatus(Status.STATUS_NONE);
        }else {
            setStatus(Status.STATUS_LOAD_COMPLETE);
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    /**
     * 加载失败
     */
    public void loadFaild(){
        setStatus(Status.STATUS_LOAD_FAILD);
    }

    /**
     * 重置
     */
    public void reset(){
        this.hasNext = true;
        this.status = Status.STATUS_NONE;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {

        if(this.mRecyclerViewDataObserver != null && this.mAdapter != null && this.mAdapter.getAdapter() != null){
            //如果原来有观察者，则移除
            this.mAdapter.getAdapter().unregisterAdapterDataObserver(this.mRecyclerViewDataObserver);
        }

        this.mAdapter = null;
        if(adapter != null) {
            this.mAdapter = new RecyclerViewAdapter(adapter);
            //实例化一个观察者
            this.mRecyclerViewDataObserver = new MRecyclerViewDataObserver();
            //注册观察者
            adapter.registerAdapterDataObserver(this.mRecyclerViewDataObserver);
        }
        super.setAdapter(this.mAdapter);
    }

    /**
     * 设置状态
     * @param status
     */
    public void setStatus(Status status) {
        Status oldStatus = this.status;
        this.status = status;
        if(mFooterView != null) mFooterView.onStateChanged(this, oldStatus, status);
    }


    public void setHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
    }

    public boolean hasHeaderView() {
        return this.mHeaderView != null;
    }

    /**
     * 设置底部view
     * @param mFooterView
     */
    public void setFooterView(BaseFooterView mFooterView) {
        this.mFooterView = mFooterView;
    }

    public void setLoadDelayMillis(long loadDelayMillis){
        this.loadDelayMillis = loadDelayMillis;
    }

    /***
     * 设置监听
     * @param mListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mListener) {
        this.mListener = mListener;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollViewListener != null) mOnScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
    }

    public interface OnScrollViewListener {
        void onScrollChanged(RefreshRecyclerView mRecyclerView, int x, int y, int oldx, int oldy);
    }

    /**
     * 重新组装一个Adapter，用来添加FooterView
     */
    private class RecyclerViewAdapter extends Adapter<ViewHolder> {
        private Adapter adapter;//数据adapter
        private final int ITEM_TYPE_HEADER = Integer.MAX_VALUE - 1;//头部布局
        private final int ITEM_TYPE_FOOTER = Integer.MAX_VALUE;//底部布局

        public RecyclerViewAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public Adapter getAdapter() {
            return adapter;
        }

        @Override
        public int getItemCount() {
            int count = this.adapter.getItemCount();

            if (count > 0) {
                if(mNeedLoadMoreEnable && mListener != null && this.isAddFooterViewEnable()) {
                    count += 1;
                }
            }

            if(mHeaderView != null) {
                count += 1;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {

            if(mHeaderView != null && position == 0) {
                return ITEM_TYPE_HEADER;
            }

            int count = 0;
            if(mNeedLoadMoreEnable && mListener != null && this.isAddFooterViewEnable() && (count = this.getItemCount()) > 0 && position == count -1) {
                return ITEM_TYPE_FOOTER;
            }

            int newPosition = mHeaderView != null ? position -1 : position;

            if (adapter.getItemViewType(newPosition) == ITEM_TYPE_FOOTER) {
                throw new RuntimeException("adapter中itemType不能为:Integer.MAX_VALUE");
            }

            return adapter.getItemViewType(newPosition);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder;
            if (viewType == ITEM_TYPE_HEADER && mHeaderView != null) {
                if(getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setFullSpan(true);
                    mHeaderView.setLayoutParams(params);
                }else {
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mHeaderView.setLayoutParams(params);
                }
                holder = new HeaderViewHolder(mHeaderView);
            }else if (viewType == ITEM_TYPE_FOOTER) {//脚部
                if(mFooterView == null) {
                    mFooterView = new MYFooterView(parent.getContext());
                }

                if(getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setFullSpan(true);
                    mFooterView.setLayoutParams(params);
                }
                holder = new FooterViewHolder(mFooterView);
            } else {//数据
                holder = adapter.onCreateViewHolder(parent, viewType);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType != ITEM_TYPE_FOOTER && viewType != ITEM_TYPE_HEADER) {
                if(mHeaderView != null) {
                    adapter.onBindViewHolder(holder, position - 1);
                }else {
                    adapter.onBindViewHolder(holder, position);
                }

            }else if(viewType == ITEM_TYPE_FOOTER){
                if(mNeedLoadMoreEnable && status == Status.STATUS_NONE && mListener != null) {
                    setStatus(Status.STATUS_LOADING);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onLoadMore();
                        }
                    }, loadDelayMillis);
                }else if(mNeedLoadMoreEnable && status == Status.STATUS_LOAD_COMPLETE) {
                    mFooterView.onStateChanged(RefreshRecyclerView.this, status, Status.STATUS_LOAD_COMPLETE);
                }
            }
        }


        public class HeaderViewHolder extends ViewHolder {

            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class FooterViewHolder extends ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }


        /**
         * 是否能添加FooterView
         * @return
         */
        private boolean isAddFooterViewEnable(){
            return !(getLayoutManager() instanceof GridLayoutManager )
                    && (getLayoutManager() instanceof StaggeredGridLayoutManager
                    || getLayoutManager() instanceof LinearLayoutManager);
        }

    }


    /**
     * 观察者
     */
    private class MRecyclerViewDataObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            if(mAdapter != null) mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if(mAdapter != null) mAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if(mAdapter != null) mAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if(mAdapter != null) mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if(mAdapter != null) mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }


    /**
     * 加载更多接口
     */
    public interface OnLoadMoreListener {
        /**
         * 刷新回调
         */
        void onLoadMore();
    }


    /**
     * 加载状态
     */
    public enum Status {
        STATUS_NONE,//可加载更多状态
        STATUS_LOADING,//正在加载
        STATUS_LOAD_FAILD,//加载失败
        STATUS_LOAD_COMPLETE,//完成加载
    }

}
