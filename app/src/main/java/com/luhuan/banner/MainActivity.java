package com.luhuan.banner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        for (int i = 0; i < 3; i++) {
            ImageView imageview=new ImageView(this);
            imageview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            imageview.setImageResource(resImgs[i]);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            banner.addView(imageview,i);
        }

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banner.startAuto();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banner.stopAuto();
            }
        });
    }
}
