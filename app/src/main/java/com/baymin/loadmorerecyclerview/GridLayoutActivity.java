package com.baymin.loadmorerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baymin.loadmorerecyclerview.util.DimensionConvert;
import com.baymin.loadmorerecyclerview.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GridLayoutActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    LoadMoreRecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private List<String> dataList = new ArrayList<>();
    private View headerView;
    private View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {

        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_red_dark,
                android.R.color.holo_green_dark, android.R.color.holo_orange_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newData();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        recyclerView.setLoadMore(true);
                        recyclerView.addFootView(footerView);
                    }
                }, 4000);
            }
        });

        // 头部
        headerView = LayoutInflater.from(GridLayoutActivity.this).inflate(R.layout.header_view, null);
        // 脚部
        footerView = LayoutInflater.from(GridLayoutActivity.this).inflate(R.layout.footer_view, null);
        // 使用重写后的线性布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(GridLayoutActivity.this, 2));
        // 添加头部和脚部，如果不添加就使用默认的头部和脚部
        recyclerView.addHeaderView(headerView);
        recyclerView.addFootView(footerView);
        // 设置适配器
        recyclerView.setAdapter(new MyAdapter());
        // 设置刷新和加载更多数据的监听，分别在onRefresh()和onLoadMore()方法中执行刷新和加载更多操作
        recyclerView.setLoadDataListener(new LoadMoreRecyclerView.LoadDataListener() {

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        // 加载更多完成后调用，必须在UI线程中
                        recyclerView.loadMoreComplete();
                        recyclerView.setLoadMore(false);
                    }
                }, 4000);
            }
        });

    }

    /**
     * 添加数据
     */
    private void addData() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        for (int i = 0; i < 18; i++) {
            dataList.add("条目  " + (dataList.size() + 1));
        }
    }

    public void newData() {
        dataList.clear();
        for (int i = 0; i < 18; i++) {
            dataList.add("刷新后条目  " + (dataList.size() + 1));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(GridLayoutActivity.this);
            view.setGravity(Gravity.CENTER);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    DimensionConvert.dip2px(GridLayoutActivity.this, 50)));
            MyViewHolder mViewHolder = new MyViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mTextView.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }

}
