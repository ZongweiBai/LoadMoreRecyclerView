package com.baymin.loadmorerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baymin.loadmorerecyclerview.convenientBanner.CBViewHolderCreator;
import com.baymin.loadmorerecyclerview.convenientBanner.ConvenientBanner;
import com.baymin.loadmorerecyclerview.convenientBanner.LocalImageHolderView;
import com.baymin.loadmorerecyclerview.util.DimensionConvert;
import com.baymin.loadmorerecyclerview.widget.LoadMoreRecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LinearLayoutActivity extends AppCompatActivity {

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycler_view)
    LoadMoreRecyclerView recyclerView;

    private List<String> dataList = new ArrayList<>();
    private View headerView;
    private View footerView;
    private ConvenientBanner mIndexBanner;
    public List<Integer> topBanners = new ArrayList<>();

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

        //本地图片集合
        for (int position = 0; position < 7; position++) {
            topBanners.add(getResId("ic_test_" + position, R.drawable.class));
        }
        // 头部
        headerView = LayoutInflater.from(LinearLayoutActivity.this).inflate(R.layout.header_view, null);
        mIndexBanner = (ConvenientBanner) headerView.findViewById(R.id.app_index_banner);
        mIndexBanner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
            @Override
            public LocalImageHolderView createHolder() {
                return new LocalImageHolderView();
            }
        }, topBanners);
        //设置两个点图片作为翻页指示器，不设置则没有指示器
        mIndexBanner.setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
        //设置翻页的效果，不需要翻页效果可用不设
        mIndexBanner.setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
        // 设置完成，开始自动轮播
        mIndexBanner.startTurning(5000);

        // 脚部
        footerView = LayoutInflater.from(LinearLayoutActivity.this).inflate(R.layout.footer_view, null);
        // 使用重写后的线性布局管理器
        recyclerView.setLoadMore(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(LinearLayoutActivity.this));
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
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 添加数据
     */
    private void addData() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        for (int i = 0; i < 10; i++) {
            dataList.add("条目  " + (dataList.size() + 1));
        }
    }

    public void newData() {
        dataList.clear();
        for (int i = 0; i < 10; i++) {
            dataList.add("刷新后条目  " + (dataList.size() + 1));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(LinearLayoutActivity.this);
            view.setGravity(Gravity.CENTER);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    DimensionConvert.dip2px(LinearLayoutActivity.this, 50)));
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
