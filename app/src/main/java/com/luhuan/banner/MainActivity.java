package com.luhuan.banner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private Banner banner;
    private BannerLayout bannerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerLayout = (BannerLayout) findViewById(R.id.bannerframeLayout);
        Integer[] resImgs = {
                R.mipmap.img01,
                R.mipmap.img02,
                R.mipmap.img03,
                R.mipmap.img04,
                R.mipmap.img05,
                R.mipmap.img06,
                R.mipmap.img07,
                R.mipmap.img08,
                R.mipmap.img09
        };

        //<--############################################直接使用banner###################################################-->

//        /**
//         * 添加本地图片资源的方式
//         */
//        banner.addImageRes(Arrays.asList(resImgs));
//        /**
//         * 添加网络url图片资源的方式
//         */
//        banner.setInterval(3000);
//        banner.setBannarListener(new Banner.OnBannerListener() {
//            @Override
//            public void onClick(int position) {
//                Log.d("luhuan", "onClick: "+position);
//                Observable.just(position).observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Integer>() {
//                            @Override
//                            public void accept(@NonNull Integer integer) throws Exception {
//                                Toast.makeText(MainActivity.this, integer+"", Toast.LENGTH_SHORT).show();
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(@NonNull Throwable throwable) throws Exception {
//                                Log.d("luhuan", "accept: "+throwable.getMessage());
//                            }
//                        });
//
//            }
//        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerLayout.startAuto();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerLayout.stopAuto();
            }
        });

      //  <--#############################使用bannerFrameLayout#################################-->
        bannerLayout.addImageRes(Arrays.asList(resImgs));
        bannerLayout.setBanerlayoutListener(new BannerLayout.OnBannerLayoutListener() {
            @Override
            public void onBannerClick(int bannerPosition) {
                Log.d("luhuan", "onClick: "+bannerPosition);
                Observable.just(bannerPosition).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Toast.makeText(MainActivity.this, integer+"", Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.d("luhuan", "accept: "+throwable.getMessage());
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    //    banner.startAuto();
        bannerLayout.startAuto();
    }

    @Override
    protected void onPause() {
        super.onPause();
     //   banner.stopAuto();
        bannerLayout.stopAuto();
    }
}
