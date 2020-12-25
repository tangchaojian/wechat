package com.tcj.sunshine.tools;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ItemDecorationUtils {

    public static RecyclerView.ItemDecoration getSpaceItemDecoration(int left, int top, int right, int bottom, int spanCount) {
        return new SpaceItemDecoration(left, top, right, bottom, spanCount);
    }

    public static RecyclerView.ItemDecoration getSpaceItemDecoration(int left, int top, int right, int bottom, int spanCount, int headerCount) {
        return new SpaceItemDecoration(left, top, right, bottom, spanCount, headerCount);
    }

    // 自定义条目修饰类
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int left;
        private int top;
        private int right;
        private int bottom;
        private int headerViewCount;

        /**
         * @param left 左间隔
         * @param top 上间隔
         * @param right 右间隔
         * @param bottom 下间隔
         * @param spanCount 列数
         */
        public SpaceItemDecoration(int left, int top, int right, int bottom, int spanCount) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.spanCount = spanCount;
        }

        /**
         * @param left 左间隔
         * @param top 上间隔
         * @param right 右间隔
         * @param bottom 下间隔
         * @param spanCount 列数
         */
        public SpaceItemDecoration(int left, int top, int right, int bottom, int spanCount, int headerViewCount) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.spanCount = spanCount;
            this.headerViewCount = headerViewCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // 第一列左边贴边、后边列项依次移动一个space和前一项移动的距离之和

            int position = parent.getChildAdapterPosition(view) - headerViewCount;

            if(position < 0) {
                return;
            }


            int spanCount = 0;
            int spanIndex = 0;
            RecyclerView.Adapter adapter = parent.getAdapter();
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (adapter == null || layoutManager == null) {
                return;
            }

            if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
                spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            }
            //普通Item的尺寸
            int itemCount = adapter.getItemCount();

            if(this.spanCount == 2) {
                //普通Item的尺寸
                if (position < itemCount && spanCount == 2) {
                    if (spanIndex != StaggeredGridLayoutManager.LayoutParams.INVALID_SPAN_ID) {
                        //getSpanIndex方法不管控件高度如何，始终都是左右左右返回index
                        if (spanIndex % 2 == 0) {
                            outRect.left = left;
                            outRect.right = right / 2;
                            if (position < spanCount) {
                                outRect.top = top;
                            }
                            outRect.bottom = bottom;
                        } else {
                            outRect.left = left / 2;
                            outRect.right = right;
                            if (position < spanCount) {
                                outRect.top = top;
                            }
                            outRect.bottom = bottom;
                        }
                    }
                }else {
                    outRect.left = left;
                    outRect.right = right;
                    if (position < spanCount) {
                        outRect.top = top;
                    }
                    outRect.bottom = bottom;
                }
            }else if(this.spanCount > 2){
                if (position < itemCount && spanCount == this.spanCount) {
                    if (spanIndex != StaggeredGridLayoutManager.LayoutParams.INVALID_SPAN_ID) {
                        //getSpanIndex方法不管控件高度如何，始终都是左右左右返回index

                        int column = spanIndex % spanCount;

                        outRect.left = left - column * left / spanCount;
                        outRect.right = (column + 1) * right / spanCount;
                        if (position < spanCount) {
                            outRect.top = top;
                        }
                        outRect.bottom = bottom;
                    }
                }else {
                    outRect.left = left;
                    outRect.right = right;
                    if (position < spanCount) {
                        outRect.top = top;
                    }
                    outRect.bottom = bottom;
                }
            }else {
                outRect.left = left;
                outRect.right = right;
                if (position < spanCount) {
                    outRect.top = top;
                }
                outRect.bottom = bottom;
            }
        }
    }
}
