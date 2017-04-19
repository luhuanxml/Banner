package com.luhuan.banner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screenWidth=Screen.getScreenWidth(this);
        banner = (Banner) findViewById(R.id.banner);
        int[] resImgs = {
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
        for (int i = 0; i < resImgs.length; i++) {
            ImageView imageview=new ImageView(this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setImageResource(resImgs[i]);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            banner.addView(imageview,i);
        }
        banner.setInterval(1000);
        banner.setBannarListener(new Banner.OnBannerListener() {
            @Override
            public void onClick(int position) {
                Log.d("luhuan", "onClick: "+position);
                Observable.just(position).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                Toast.makeText(MainActivity.this, integer+"", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.d("luhuan", "accept: "+throwable.getMessage());
                            }
                        });

            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banner.startAuto();
                finish();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banner.stopAuto();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        banner.startAuto();
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAuto();
    }
}
