package com.luhuan.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by 鲁欢 on 2017/4/19 0019.
 * 轮播广告控件
 */

public class Banner extends ViewGroup {

    private static final String TAG = "Banner";

    int childrenCount;//子视图的数量
    int childWidth;//子视图的宽度
    int childHeight;//子视图的高度

    private int x; //每一次移动之前的起始横坐标
    private int index = 0;//每张图片的索引

    //使用Sroller 对象获得手机轮播图效果
    private Scroller scroller;

    //自动轮播
    boolean isAuto = true; //默认开启轮播图
    Disposable autoDisposable;//自动轮播订阅

    public Banner(Context context) {
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //不要用构造器传入的content
    private void init() {
        scroller = new Scroller(getContext());
        auto();
    }

    private void auto(){
        if (isAuto) {
            autoDisposable = Observable.interval(100, 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            if (++index>=childrenCount){
                                index=0;
                            }
                            scrollTo(childWidth*index,0);
                        }
                    });
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            //水平滑动  y轴不滑动
            scrollTo(scroller.getCurrX(), 0);
        }
    }

    /**
     * 测量ViewGroup的子视图的宽度和高度 然后才能的到Viewgroup的宽度和高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得子视图的个数
        childrenCount = getChildCount();
        Log.d(TAG, "onMeasure: " + childrenCount);
        if (childrenCount == 0) {
            setMeasuredDimension(0, 0);
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            View view = getChildAt(0);//第一个视图，该视图绝对存在
            childWidth = view.getMeasuredWidth();
            childHeight = view.getMeasuredHeight();
            //重新测量viewGroup的宽高  宽为 childWidth*count  高为childHeight;
            int width = childWidth * childrenCount;//viewgroup 宽度
            int height = childHeight; //viewgroup 高度
            setMeasuredDimension(width, height);
        }
    }

    /**
     * 布局
     *
     * @param changed 布局发生改变的时候为true
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            //距离左边距离
            int leftMargin = 0;
            for (int i = 0; i < childrenCount; i++) {
                //只有宽度相对宽度发生变化
                View view = getChildAt(i);
                view.layout(leftMargin, 0, leftMargin + childWidth, childHeight);
                leftMargin += childWidth;
            }
        }
    }

    /**
     * 事件传递过程 调用容器的拦截 要让返回值为true
     * 处理该事件的的方法是onTouchEvent();
     *
     * @param ev
     * @return 如果返回为true  容器会处理此次拦截事件 如果为false 将不处理，并向下传递该事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 实现onTouchEnvent方法  完成轮播图的手动轮播
     * 1.我们在滑动屏幕图片的过程中，其实就是我们自定义viewgroup的子视图的移动过程
     * 需要知道滑动之前和滑动之后的X坐标
     * 2.按下的一瞬间移动之前和移动之后的X坐标值是相等的，在这里得到这个点的横坐标值
     * 3.滑动过程中，不断调用ACTION_MOVE方法，那么此时我们移动之前的值和移动之后的值进行保存
     * 用来计算滑动距离
     * 4.抬起瞬间，计算此时我们要滑动到哪张图片的位置上
     * 此时要得出滑动到哪张图片的索引值
     * (viewgroup的位置+ childwidth/2 )/childwidth
     * 可以利用scrollTo 方法滑动到该图片位置上
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //用户按下的一瞬间
                //scroller滑动是否已经完成
                //按下的一瞬间如果上一个滑动还没完成，那么中止动画
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                //手按下的时候，关掉自动轮播让手来操作滑动
                stopAuto();
                x = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE://表示用户按下之后在屏幕上移动的过程
                int moveX = (int) event.getX();
                //滑动距离记录
                int distance = moveX - x;
                scrollBy(-distance, 0);
                x = moveX;
                break;
            case MotionEvent.ACTION_UP://表示用户抬起的一瞬间
                int scollX = getScrollX();
                index = (scollX + childWidth / 2) / childWidth;
                if (index <= 0) {
                    //此时已经滑动到了左边第一张图片
                    index = 0;
                } else if (index >= childrenCount - 1) {
                    //已经滑动到了最右边
                    index = childrenCount - 1;
                }
                int dx = index * childWidth - scollX;//抬起的时候要滑动的距离
                scroller.startScroll(scollX, 0, dx, 0, 100);//替代 scrollTo(index*childWidth,0);
                postInvalidate();//通知
                //手势滑动完了后抬起手的时候，开启自动轮播
                startAuto();
                break;
            default:
                break;
        }
        Log.d(TAG, "onTouchEvent: " + index);
        return true;//告知ViewGroup的父View,已经处理好了该事件
    }

    /**
     * 开启轮播图
     */
    public void startAuto() {
        isAuto = true;
        auto();
    }

    /**
     * 关闭轮播
     */
    public void stopAuto() {
        isAuto = false;
        if (autoDisposable!=null&&!autoDisposable.isDisposed()){
            autoDisposable.dispose();
        }
    }
}
