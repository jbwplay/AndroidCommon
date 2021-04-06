package com.androidlib.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbase.download.DownloadException;
import com.androidbase.download.downinterfaceimpl.SimpleDownLoadCallBack;
import com.androidlib.R;

import java.io.File;

import static com.androidbase.download.DownloadManager.getInstance;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }


    protected void setupView() {

        findViewById(R.id.tv_test1_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().download("https://fs.zhaogangtest.com/filegw/dl?_aid=10&_tk=FMRY17%2FdFKXGOQ458lO1%2F8GBatAUOJwOkAFUs%2FuTbbQxZIn2DFmToPKiCW5KYHLoQtDWalC4g7YkfoQ8P%2BdZh1veOUOxlbmI%2BeyuZGeKZfhGP9AgCkyudEMpYqHyLfl0rwAczVMCRH4K9mBxhMZUGrtrABJ%2BnnV1sSBCmSw2LW0%3D&_fk=f_r-d-b672ab088ffe42d981874d9965150546.pdf&_yl=t", "111.pdf", new SimpleDownLoadCallBack() {

                    @Override
                    public void onConnected(long total, boolean isRangeSupport) {
                        super.onConnected(total, isRangeSupport);
                    }

                    @Override
                    public void onProgress(long finished, long total, int progress) {
                        Log.e("====","=====progress===" + progress);
                    }

                    @Override
                    public void onCompleted(File downloadfile) {
                        Log.e("====","=====downloadfile===" + downloadfile.getAbsolutePath());
                    }

                    @Override
                    public void onFailed(DownloadException e) {
                        Log.e("====","=====DownloadException===" + e.toString());
                    }
                });
            }
        });

        findViewById(R.id.tv_test2_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().download("https://gank.io/images/0c0a6565322248f7a2d1aff9670ba198", "0c0a6565322248f7a2d1aff9670ba198.jpg", new SimpleDownLoadCallBack() {
                    @Override
                    public void onProgress(long finished, long total, int progress) {
                        Log.e("====","=====progress===" + progress);
                    }

                    @Override
                    public void onCompleted(File downloadfile) {
                        Log.e("====","=====downloadfile===" + downloadfile.getAbsolutePath());
                    }

                    @Override
                    public void onFailed(DownloadException e) {
                        Log.e("====","=====DownloadException===" + e.toString());
                    }
                });
            }
        });

        findViewById(R.id.tv_test3_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.tv_test4_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.tv_test5_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
