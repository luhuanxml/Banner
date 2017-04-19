package com.luhuan.banner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by 鲁欢 on 2017/4/19 0019.
 * 带指示点dot的banner布局
 */

public class BannerLayout extends FrameLayout implements Banner.OnChangeDotColorListener {

    private static final String TAG = "BannerLayout";

    private Banner banner;
    private LinearLayout linear;

    public BannerLayout(@NonNull Context context) {
        super(context);
        initBanner();
        initDotLayout();
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBanner();
        initDotLayout();
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBanner();
        initDotLayout();
    }

    /**
     * 初始化banner 添加到控件中
     */
    private void initBanner() {
        banner = new Banner(getContext());
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        banner.setLayoutParams(layoutParams);
        banner.setOnChangeDotColorListener(this);
        addView(banner);
    }

    /**
     * 启动轮播
     */
    public void startAuto(){
        banner.startAuto();
    }

    /**
     * 初始化底部圆点dot线性布局
     */
    private void initDotLayout() {
        linear = new LinearLayout(getContext());
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,40);
        linear.setLayoutParams(layoutParams);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.CENTER);
        linear.setBackgroundColor(Color.BLACK);
        addView(linear);
        LayoutParams frameParams= (LayoutParams) linear.getLayoutParams();
        frameParams.gravity=Gravity.BOTTOM;
        linear.setLayoutParams(frameParams);
        linear.setAlpha(0.5f);
    }

    //添加轮播图 添加相应的dot个数
    public void addImageRes(List<Integer> list){
        for (int i = 0; i < list.size(); i++) {
            ImageView imageview = new ImageView(getContext());
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setImageResource(list.get(i));
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            banner.addView(imageview);
            addDots();
        }
    }

    //添加dot
    private void addDots(){
        ImageView dot=new ImageView(getContext());
        dot.setImageResource(R.drawable.orange_radius);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,5,5,5);
        dot.setLayoutParams(layoutParams);
        dot.setImageResource(R.drawable.white_radius);
        linear.addView(dot);
    }

    @Override
    public void onChangeDotColor(int position) {
        Log.d("Banner", "onChangeDotColor: "+position);
        int count=linear.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot= (ImageView) linear.getChildAt(i);
                if (position==i){
                    dot.setImageResource(R.drawable.orange_radius);
                }else {
                    dot.setImageResource(R.drawable.white_radius);
                }
        }
    }
}
