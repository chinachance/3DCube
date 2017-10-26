package com.haohao.framwork.a3dcube;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private MyAnimationView mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("haohao", "MainActivity onCreate()");
        mView = new MyAnimationView(this);
        setContentView(mView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
