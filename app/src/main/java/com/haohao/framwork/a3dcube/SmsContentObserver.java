package com.haohao.framwork.a3dcube;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by Ma1 on 2017/4/25.
 */

public class SmsContentObserver extends ContentObserver {

    private static String uri = "";

    private int MSG_OUTBOXCONTENT = 2 ;

    private Context mContext  ;
    private Handler mHandler;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsContentObserver(Context context , Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        //发送消息,重新截取bitmap
    }
}
