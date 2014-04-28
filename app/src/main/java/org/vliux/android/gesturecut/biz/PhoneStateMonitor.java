package org.vliux.android.gesturecut.biz;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.vliux.android.gesturecut.util.AppLog;

/**
 * <p>来电状态监听</p>
 * <p>通过两种方式进行来电状态监听：</p>
 * <ul>
 * <li>1. 通过{@link android.content.BroadcastReceiver BroadcastReceiver}接收intents(etc: "android.intent.action.PHONE_STATE")</li>
 * <li>2. {@link android.telephony.TelephonyManager#listen(android.telephony.PhoneStateListener, int)}监听来电状态</li>
 * </ul>
 * <p/>
 * Created by diaoling.jj on 14-1-3.
 */
public class PhoneStateMonitor extends BroadcastReceiver {
    public static final String TAG = PhoneStateMonitor.class.getSimpleName();
    private static PhoneStateMonitor sInstance;
    /**
     * 兼容多设备来电Broadcast Intents
     */
    private static final String[] PHONE_STATE_INTENTS = {
            "android.intent.action.PHONE_STATE",
            "android.intent.action.PHONE_STATE_2",
            "android.intent.action.PHONE_STATE2",
            "android.intent.action.DUAL_PHONE_STATE",
            "android.intent.action.NEW_OUTGOING_CALL"
    };

    private Context mAppContext;
    private TelephonyManager mTelephonyManager;
    private boolean mIsOnCall = false;

    public static void init(Context context){
        if(null == sInstance){
            synchronized (PhoneStateMonitor.class){
                if(null == sInstance){
                    sInstance = new PhoneStateMonitor(context);
                }
            }
        }
    }

    public static PhoneStateMonitor getInstance(){
        return sInstance;
    }

    private PhoneStateMonitor(Context context){
        mAppContext = context.getApplicationContext();
        mTelephonyManager = (TelephonyManager)mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 来电状态监听
     */
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            AppLog.logv(TAG, "onCallStateChanged: " + state + " incomingNumber:" + incomingNumber);
            /**
             * 根据来电状态更新{@link #mIsActive}的值
             */
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  // Incoming call
                case TelephonyManager.CALL_STATE_OFFHOOK:  // A call is dialing
                    mIsOnCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:  // Not in call
                    mIsOnCall = false;
                    break;
            }
        }

    };

    public boolean isOnCall() {
        return mIsOnCall;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state == null || state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                mIsOnCall = false;
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)
                    || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                mIsOnCall = true;
            }
        }
    }

    /**
     * Need to call explicitly.
     */
    public void register() {
        IntentFilter filter = new IntentFilter();
        for (String action : PHONE_STATE_INTENTS) {
            filter.addAction(action);
        }
        filter.setPriority(1000);
        mAppContext.registerReceiver(this, filter);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void unregister() {
        mAppContext.unregisterReceiver(this);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

}

