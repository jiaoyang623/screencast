package org.daniel.android.dlna;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import org.cybergarage.upnp.Device;

/**
 * UI线程可直接调用的DLNA控制器，封装线程和数据缓存
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 2:13 PM
 */
public class ThreadRenderController {
    private static final int ACTION_MOCK = -1;
    private static final int ACTION_INIT_DEVICE = 0;
    private static final int ACTION_SET_DATASOURCE = 1;
    private static final int ACTION_SEEK = 2;
    private static final int ACTION_PLAY = 3;
    private static final int ACTION_PAUSE = 4;
    private static final int ACTION_STOP = 5;
    private static final int ACTION_SET_BRIGHTNESS = 6;
    private static final int ACTION_SET_VOLUME = 7;
    private static final int ACTION_SET_VOLUMEDB = 8;


    private Thread mControlThread, mRefreshThread;
    private ControllerHandler mControlHandler;
    private RefreshHandler mRefreshHandler;
    private RenderController mRenderController;

    private boolean mIsBrightnessEnabled = false;
    private boolean mIsVolumeEnabled = false;
    private boolean mIsVolumeDbEnabled = false;
    private int mBrightness = 0;
    private int mVolume = 0;
    private int mVolumeDb = 0;
    private int mVolumeDbMin = 0;

    private int mVolumeDbMax = 100;

    private int mLength = 0;
    private int mPosition = 0;
    RenderController.PlayerState mState = RenderController.PlayerState.STOPPED;

    public void setCallback(RenderCallback callback) {
        mCallback = callback;
    }

    private RenderCallback mCallback;

    public ThreadRenderController(Device device) {
        mRenderController = new RenderController(device);
        mControlThread = new Thread() {
            @Override
            public void run() {
                Log.i("jy", "mControlThread.prepare()");
                Looper.prepare();
                mControlHandler = new ControllerHandler();
                Looper.loop();
                Log.i("jy", "mControlThread.quited");
            }
        };
        mControlThread.start();
        mRefreshThread = new Thread() {
            @Override
            public void run() {
                Log.i("jy", "mRefreshThread.prepare()");
                Looper.prepare();
                mRefreshHandler = new RefreshHandler();
                Looper.loop();
                Log.i("jy", "mRefreshThread.quited");
            }
        };
        mRefreshThread.start();
    }

    //工作线程
    private void initDevice() {
        //音量
        mIsVolumeDbEnabled = mRenderController.isVolumeDbEnabled();
        if (mIsVolumeDbEnabled) {
            int[] volumeRange = mRenderController.getVolumeDbRange();
            mVolumeDbMin = volumeRange[0];
            mVolumeDbMax = volumeRange[1];

            mVolumeDb = mRenderController.getVolumeDb();
        } else {
            Log.i("jy", "VolumeDb disabled");
        }

        mIsVolumeEnabled = mRenderController.isVolumeEnabled();
        if (mIsVolumeEnabled) {
            mVolume = mRenderController.getVolume();
        } else {
            Log.i("jy", "Volume disabled");
        }
        //亮度

        mIsBrightnessEnabled = mRenderController.isBrightnessEnabled();
        if (mIsBrightnessEnabled) {
            mBrightness = mRenderController.getBrightness();
        } else {
            Log.i("jy", "Brightness disabled");
        }
    }

    public void setDataSource(String uri) {
        mControlHandler.removeMessages(ACTION_SET_DATASOURCE);
        Message msg = mControlHandler.obtainMessage(ACTION_SET_DATASOURCE);
        msg.obj = uri;
        mControlHandler.sendMessage(msg);

        startRefresh();
    }


    /**
     * 完成内容设置，可以直接播放
     */
    private void onPrepared() {
        //获取时长
        mRenderController.getPosition();
        if (mCallback != null) {
            UIHandler.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onPrepared();
                }
            });
        }
    }

    public void seek(int position) {
        mControlHandler.removeMessages(ACTION_SEEK);
        Message msg = mControlHandler.obtainMessage(ACTION_SEEK);
        msg.obj = position;
        mControlHandler.sendMessage(msg);
    }

    public int getLength() {
        return mLength;
    }

    public void play() {
        mControlHandler.sendEmptyMessage(ACTION_PLAY);
        startRefresh();
    }

    public void pause() {
        mControlHandler.sendEmptyMessage(ACTION_PAUSE);
    }

    public void stop() {
        mControlHandler.sendEmptyMessage(ACTION_STOP);
        stopRefresh();
    }

    public int getPosition() {
        return mPosition;
    }

    public boolean isBrightnessEnabled() {
        return mIsBrightnessEnabled;
    }

    public int getBrightness() {
        return mBrightness;
    }

    public void setBrightness(int brightness) {
        if (mIsBrightnessEnabled) {
            Message msg = mControlHandler.obtainMessage(ACTION_SET_BRIGHTNESS);
            msg.obj = brightness;
            mControlHandler.sendMessage(msg);
        }
    }

    public int getVolumeDb() {
        return mVolumeDb;
    }

    public int getVolumeDbMax() {
        return mVolumeDbMax;
    }

    public int getVolumeDbMin() {
        return mVolumeDbMin;
    }

    public void setVolumeDb(int volume) {
        if (mIsVolumeDbEnabled) {
            Message msg = mControlHandler.obtainMessage(ACTION_SET_VOLUMEDB);
            msg.obj = volume;
            mControlHandler.sendMessage(msg);
        }
    }

    public int getVolume() {
        return mVolume;
    }

    public void setVolume(int volume) {
        if (mIsVolumeEnabled) {
            Message msg = mControlHandler.obtainMessage(ACTION_SET_VOLUME);
            msg.obj = volume;
            mControlHandler.sendMessage(msg);
        }
    }

    public RenderController.PlayerState getState() {
        return mState;
    }

    public void quit() {
        mControlHandler.getLooper().quit();
        mRefreshHandler.getLooper().quit();
    }


    private class ControllerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_INIT_DEVICE:
                    initDevice();
                    break;
                case ACTION_SET_DATASOURCE:
                    mRenderController.setDataSource((String) msg.obj);
                    onPrepared();
                    break;
                case ACTION_SEEK:
                    mRenderController.seek((Integer) msg.obj);
                    break;
                case ACTION_PLAY:
                    mRenderController.play();
                    break;
                case ACTION_PAUSE:
                    mRenderController.pause();
                    break;
                case ACTION_STOP:
                    mRenderController.stop();
                    break;
                case ACTION_SET_BRIGHTNESS:
                    mRenderController.setBrightness((Integer) msg.obj);
                    break;
                case ACTION_SET_VOLUME:
                    mRenderController.setVolume((Integer) msg.obj);
                    break;
                case ACTION_SET_VOLUMEDB:
                    mRenderController.setVolumeDb((Integer) msg.obj);
                    break;
                case ACTION_MOCK:
                    mRenderController.mock(msg.obj);
            }
        }
    }

    private boolean mNeedRefresh = false;

    private void stopRefresh() {
        mNeedRefresh = false;
        mRefreshHandler.removeMessages(0);
    }

    private void startRefresh() {
        mNeedRefresh = true;
        mRefreshHandler.sendEmptyMessage(0);
    }

    private void refreshState() {
        // Position
        int[] values = mRenderController.getPosition();
        mPosition = values[0];
        mLength = values[1];

        // Playing state
        mState = mRenderController.getState();

        // volume
        if (mIsVolumeEnabled) {
            mVolume = mRenderController.getVolume();
        }
        if (mIsVolumeDbEnabled) {
            mVolume = mRenderController.getVolumeDb();
        }

        // brightness
        if (mIsBrightnessEnabled) {
            mBrightness = mRenderController.getBrightness();
        }
    }

    /**
     * 获取时长间隔
     */
    private static final int REFRESH_INTERVAL = 500;

    private class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (mNeedRefresh) {
                refreshState();
                removeMessages(0);
                sendEmptyMessageDelayed(0, REFRESH_INTERVAL);
            }
        }
    }


    public void mock(Object obj) {
        Message msg = mControlHandler.obtainMessage(ACTION_MOCK);
        msg.obj = obj;
        mControlHandler.sendMessage(msg);
    }

    public interface RenderCallback {
        void onPrepared();
    }
}
