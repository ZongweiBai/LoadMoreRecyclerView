package com.baymin.loadmorerecyclerview.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 自动加载的RecyclerView
 * User: Baymin
 * Date: 2015-12-06
 * Time: 16:38
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private Context mContext;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();

    private boolean isLoadingData = false; // 是否正在加载数据

    private Boolean mLoadMore = false;   // 是否自动加载更多数据

    private LoadDataListener mLoadDataListener;

    private Adapter mAdapter;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOverScrollMode(OVER_SCROLL_NEVER);
        post(new Runnable() {
            @Override
            public void run() {
                initManager();
            }
        });
    }

    /**
     * 添加头部视图，可以添加多个
     *
     * @param view 头部视图
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    /**
     * 添加尾部视图，此视图只能添加一个，添加多个时，默认最后添加的一个。
     *
     * @param view 底部视图
     */
    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    /**
     * 设置是否开启自动加载更多
     *
     * @param openLoadMore
     */
    public void setLoadMore(Boolean openLoadMore) {
        if (!openLoadMore) {
            mFootViews.clear();
        }
        mLoadMore = openLoadMore;
    }

    /**
     * 设置加载更多数据的监听
     */
    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    /**
     * 加载更多数据完成后调用，必须在UI线程中
     */
    public void loadMoreComplete() {
        isLoadingData = false;
        if (mFootViews.size() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.isEmpty() && mFootViews.isEmpty()) {
            adapter = new WrapAdapter(null, null, adapter);
            super.setAdapter(adapter);
        } else {
            adapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }

    public void initManager() {
        LayoutManager manager = getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            layoutGridAttach((GridLayoutManager) manager);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            layoutStaggeredGridHeadAttach();
        }
        if (((WrapAdapter) mAdapter).getFootersCount() > 0) {
            // 初始隐藏footer
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * 给StaggeredGridLayoutManager附加头部
     */
    private void layoutStaggeredGridHeadAttach() {
        // 从前向后查找Header并设置为充满一行
        View view;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (((WrapAdapter) mAdapter).isHeader(i)) {
                view = getChildAt(i);
                ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(true);
                view.requestLayout();
            } else {
                break;
            }
        }
    }

    /**
     * 给StaggeredGridLayoutManager附加尾部
     *
     * @param view 底部视图
     */
//    private void layoutStaggeredGridFootAttach(View view) {
//        // Footer设置为充满一行
//        ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(true);
//    }

    /**
     * 给{@link GridLayoutManager}附加头部尾部和滑动过度监听
     *
     * @param manager {@link GridLayoutManager}
     */
    private void layoutGridAttach(final GridLayoutManager manager) {
        // GridView布局
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((WrapAdapter) mAdapter).isHeader(position) ||
                        ((WrapAdapter) mAdapter).isFooter(position) ? manager.getSpanCount() : 1;
            }
        });
        requestLayout();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 当前不滚动，且不是正在刷新或加载数据
        if (mLoadMore && state == RecyclerView.SCROLL_STATE_IDLE && mLoadDataListener != null && !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            // 获取最后一个正在显示的Item的位置
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {
                if (mFootViews.size() > 0) {
                    mFootViews.get(0).setVisibility(VISIBLE);
                }
                // 加载更多
                mLoadDataListener.onLoadMore();
                isLoadingData = true;
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 自定义带有头部/尾部的适配器
     */
    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        private ArrayList<View> mHeaderViews;
        private ArrayList<View> mFootViews;
        final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();
        private int headerPosition = 0;

        public WrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter) {
            this.mAdapter = mAdapter;
            if (mHeaderViews == null) {
                this.mHeaderViews = EMPTY_INFO_LIST;
            } else {
                this.mHeaderViews = mHeaderViews;
            }
            if (mFootViews == null) {
                this.mFootViews = EMPTY_INFO_LIST;
            } else {
                this.mFootViews = mFootViews;
            }
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Header
         */
        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Footer
         */
        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        /**
         * @return Header的数量
         */
        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        /**
         * @return Footer的数量
         */
        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                mFootViews.get(0).setLayoutParams(params);
                return new HeaderViewHolder(mFootViews.get(0));
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return RecyclerView.INVALID_TYPE;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 加载更多数据的监听接口
     */
    public interface LoadDataListener {

        /**
         * 执行加载更多
         */
        void onLoadMore();

    }

}
