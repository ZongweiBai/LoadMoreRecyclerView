package com.baymin.loadmorerecyclerview.convenientBanner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
    private ImageView imageView;
//    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(final Context context, int position, final String data) {
//        ImageFileUtil.displayImage(imageLoader, data, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
