package com.luhuan.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

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

    int interval = 3000;//自动轮播间隔时间 默认1000毫秒

    public void setInterval(int interval_time) {
        interval = interval_time;
    }

    //点击事件
    private OnBannerListener listener;
    //这里要给一个变量值判断用户按下后离开的瞬间是要滑动还是要点击图片
    // true  表示是点击事件  false  表示不是点击事件
    public boolean isclick;

    public void setBannarListener(OnBannerListener listener) {
        this.listener = listener;
    }

    private OnChangeDotColorListener dotColorListener;

    public void setOnChangeDotColorListener(OnChangeDotColorListener dotColorListener){
        this.dotColorListener=dotColorListener;
    }

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
    }

    //简化添加view的过程，直接把图片资源放进来
    public void addImageRes(List<Integer> list) {
        for (Integer resId : list) {
            ImageView imageview = new ImageView(getContext());
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setImageResource(resId);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(imageview);
        }

    }

    public void addImageUrl(List<String> list) {
        for (String imgUrl : list) {
            ImageView imageview = new ImageView(getContext());
            imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(getContext()).load(imgUrl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(imageview);
            addView(imageview);
        }
    }

    private void auto() {
        if (isAuto) {
            autoDisposable = Observable.interval(1000, interval, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<Long>() {
                        @Override
                        public boolean test(@NonNull Long aLong) throws Exception {
                            return isAuto = true;
                        }
                    })
                    //每次走一次就卡死，Only the original thread that created a view hierarchy can touch its views.
                    //是因为刷新界面需要在UI线程中
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            /**
                             *  ###### bug容易出现的地方######
                             *  自动轮播之前要把动画结束掉，否则图片位置跟dot位置容易错位，
                             */

                            if (!scroller.isFinished()) {
                                scroller.abortAnimation();
                            }
                            //最后一张图片 将会从第一张图片开始重新滑动
                            if ((++index) >= childrenCount) {
                                index = 0;
                            }
                            scrollTo(childWidth * index, 0);
                            if (dotColorListener != null) {
                                Log.d(TAG, "accept: "+index);
                                dotColorListener.onChangeDotColor(index);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            Log.d(TAG, "accept: "+throwable.getMessage());
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.d(TAG, "run: "+"完成了");
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
     * @return  true 告知事件已经完成
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
                isclick = true;
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
                if (distance != 0) {
                    isclick = false;
                }
                break;
            case MotionEvent.ACTION_UP://表示用户抬起的一瞬间
                int scollX = getScrollX();
                index = (scollX + childWidth / 2) / childWidth;
                if (isclick&&listener!=null) {
                    listener.onClick(index);
                } else {
                    if (index < 0) {
                        //此时已经滑动到了左边第一张图片
                        index = 0;
                    } else if (index > childrenCount - 1) {
                        //已经滑动到了最右边
                        index = childrenCount - 1;
                    }
                    Log.d(TAG, "onTouchEvent: "+index);
                    int dx = index * childWidth - scollX;//抬起的时候要滑动的距离
                    scroller.startScroll(scollX, 0, dx, 0);//替代 scrollTo(index*childWidth,0);
                    postInvalidate();//通知
                    if (dotColorListener != null) {
                        dotColorListener.onChangeDotColor(index);
                    }
                }
                //手势滑动完了后抬起手的时候，开启自动轮播
                startAuto();
                break;
            default:
                break;
        }
        return true;//告知ViewGroup的父View,已经处理好了该事件
    }

    /**
     * 开启轮播图
     */
    public void startAuto() {
        isAuto = true;
        if (autoDisposable != null && !autoDisposable.isDisposed()) {
            autoDisposable.dispose();
        }
        auto();
    }

    /**
     * 关闭轮播
     */
    public void stopAuto() {
        isAuto = false;
        if (autoDisposable != null && !autoDisposable.isDisposed()) {
            autoDisposable.dispose();
        }
    }

    /**
     * 图片点击事件
     */
    public interface OnBannerListener {
        void onClick(int position);
    }

    /**
     * dot跟随变换接口
     */
    public interface OnChangeDotColorListener{
        void onChangeDotColor(int position);
    }
}
