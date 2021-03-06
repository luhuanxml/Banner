package com.luhuan.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
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

    private OnBannerLayoutListener banerlayoutListener;

    /**
     * 提供给开发者自己定义dot样式的权力
     * 不设置的情况下默认给出两个默认值
     */
    @DrawableRes
    Integer lightDot = R.drawable.orange_radius;
    @DrawableRes
    Integer normalDot= R.drawable.white_radius;

    /**
     * 指示器条背景
     */
    @ColorRes
    Integer linearbackgroud;
    /**
     * 指示器条透明度
     */
    Float linerAlpha;

    /**
     * 轮播时间间隔
     */
    int interval;//自动轮播间隔时间 默认1000毫秒
    /**
     * dot margin
     */
    int left =5,right=5,top=15,bottom=15;

    /**
     * 设置点亮dot样式
     * @param lightDot 样式资源
     */
    public void setLightDot(Integer lightDot) {
        this.lightDot = lightDot;
    }

    /**
     * 设置普通dot样式
     * @param normalDot 样式资源
     */
    public void setNormalDot(Integer normalDot) {
        this.normalDot = normalDot;
    }

    /**
     * dot与dot之间上下左右的间距Margin
     */
    public void setDotMargin(@Px int left_right,@Px int top_bottom){
        left=left_right;
        right=left_right;
        top=top_bottom;
        bottom=top_bottom;
    }

    /**
     * 点击图片事件监听
     * @param banerlayoutListener  点击事件监听
     */
    public void setBanerlayoutListener(OnBannerLayoutListener banerlayoutListener) {
        this.banerlayoutListener = banerlayoutListener;
    }

    public BannerLayout(@NonNull Context context) {
        super(context);
        initBanner();
        initDotLayout();
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getArrs(context,attrs);
        initBanner();
        initDotLayout();
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getArrs(context,attrs);
        initBanner();
        initDotLayout();
    }

    /**
     * 自定义控件加属性
     */
    private void getArrs(@NonNull Context context, @Nullable AttributeSet attrs){
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.BannerLayout);
        left=typedArray.getInteger(R.styleable.BannerLayout_dot_marginleft,5);
        right=typedArray.getInteger(R.styleable.BannerLayout_dot_marginright,5);
        top=typedArray.getInteger(R.styleable.BannerLayout_dot_margintop,15);
        bottom=typedArray.getInteger(R.styleable.BannerLayout_dot_marginbottom,15);
        linearbackgroud=typedArray.getResourceId(R.styleable.BannerLayout_linear_background,android.R.color.black);
        linerAlpha=typedArray.getFloat(R.styleable.BannerLayout_linear_alpha,0.5f);
        interval=typedArray.getInteger(R.styleable.BannerLayout_interval,3000);
        typedArray.recycle();
    }

    /**
     * 初始化banner 添加到控件中
     */
    private void initBanner() {
        banner = new Banner(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        banner.setLayoutParams(layoutParams);
        banner.setOnChangeDotColorListener(this);
        banner.setBannarListener(new Banner.OnBannerListener() {
            @Override
            public void onClick(int position) {
                Log.d(TAG, "onClick: " + position);
                if (banerlayoutListener != null) {
                    banerlayoutListener.onBannerClick(position);
                }
            }
        });
        banner.setInterval(interval);
        addView(banner);
    }

    /**
     * 启动轮播
     */
    public void startAuto() {
        banner.startAuto();
    }

    /**
     * 关闭轮播
     */
    public void stopAuto() {
        banner.stopAuto();
    }

    public void setInterval(int interval_time) {
        interval=interval_time;
        banner.setInterval(interval);
    }


    /**
     * 初始化底部圆点dot线性布局
     */
    private void initDotLayout() {
        linear = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(layoutParams);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.CENTER);
        Log.d(TAG, "initDotLayout: "+linearbackgroud);
        linear.setBackgroundResource(linearbackgroud);
        addView(linear);
        LayoutParams frameParams = (LayoutParams) linear.getLayoutParams();
        frameParams.gravity = Gravity.BOTTOM;
        linear.setLayoutParams(frameParams);
        linear.setAlpha(linerAlpha);
    }

    //添加轮播图 添加相应的dot个数
    public void addImageRes(List<Integer> list) {
        banner.addImageRes(list);
        addDots(list.size());
    }

    public void addImageUrl(List<String> list) {
        banner.addImageUrl(list);
        addDots(list.size());
    }

    /**
     * 添加指示器
     * @param count  指示器个数
     */
    private void addDots(int count) {
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(left, top, right,bottom);
            dot.setLayoutParams(layoutParams);
            //设置dot初始颜色，默认第一个为亮色
            if (i==0){
                dot.setImageResource(lightDot);
            }else {
                dot.setImageResource(normalDot);
            }
            linear.addView(dot);
        }
    }

    @Override
    public void onChangeDotColor(int position) {
        Log.d("Banner", "onChangeDotColor: " + position);
        int count = linear.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) linear.getChildAt(i);
            if (position == i) {
                dot.setImageResource(lightDot);
            } else {
              dot.setImageResource(normalDot);
            }
        }
    }

    /**
     * 对banner 图片点击事件的监听回调
     */
    public interface OnBannerLayoutListener {
        void onBannerClick(int bannerPosition);
    }
}
