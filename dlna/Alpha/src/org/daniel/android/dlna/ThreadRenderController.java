package org.daniel.android.dlna;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import org.cybergarage.upnp.Device;

/**
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 2:13 PM
 */
public class ThreadRenderController {
    private static final int ACTION_MOCK = -1;
    private static final int ACTION_SET_DEVICE = 0;
    private static final int ACTION_SET_DATASOURCE = 1;
    private static final int ACTION_SEEK = 2;
    private static final int ACTION_PLAY = 3;
    private static final int ACTION_PAUSE = 4;
    private static final int ACTION_STOP = 5;
    private static final int ACTION_SET_BRIGHTNESS = 6;
    private static final int ACTION_SET_VOLUME = 7;


    private Thread mControlThread, mRefreshThread;
    private ControllerHandler mControlHandler;
    private RefreshHandler mRefreshHandler;
    private RenderController mRenderController;

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

    public void setDevice(Device device) {
        Message msg = mControlHandler.obtainMessage(ACTION_SET_DEVICE);
        msg.obj = device;
        mControlHandler.sendMessage(msg);
    }

    public void setDataSource(String uri) {
        mControlHandler.removeMessages(ACTION_SET_DATASOURCE);
        Message msg = mControlHandler.obtainMessage(ACTION_SET_DATASOURCE);
        msg.obj = uri;
        mControlHandler.sendMessage(msg);
    }


    /**
     * 完成内容设置，可以直接播放
     */
    private void onPrepared() {
        //获取时长
        mRenderController.getPosition();
        startRefresh();
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
        if (mRenderController.mLength == 0) {
            startRefresh();
        }
        return mRenderController.mLength;
    }

    public void play() {
        mControlHandler.sendEmptyMessage(ACTION_PLAY);
    }

    public void pause() {
        mControlHandler.sendEmptyMessage(ACTION_PAUSE);
    }

    public void stop() {
        mControlHandler.sendEmptyMessage(ACTION_STOP);
    }

    public int getPosition() {
        return mRenderController.mPosition;
    }

    public void setBrightness(int brightness) {
        Message msg = mControlHandler.obtainMessage(ACTION_SET_BRIGHTNESS);
        msg.obj = brightness;
        mControlHandler.sendMessage(msg);
    }


    public void setVolume(int volume) {
        Message msg = mControlHandler.obtainMessage(ACTION_SET_VOLUME);
        msg.obj = volume;
        mControlHandler.sendMessage(msg);
    }

    public void quit() {
        mControlHandler.getLooper().quit();
        mRefreshHandler.getLooper().quit();
    }


    private class ControllerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_SET_DEVICE:
                    mRenderController.setDevice((Device) msg.obj);
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
                case ACTION_MOCK:
                    mRenderController.mock(msg.obj);
            }
        }
    }

    private void startRefresh() {
        mRefreshHandler.removeMessages(0);
        mRefreshHandler.sendEmptyMessage(0);
    }

    /**
     * 获取时长间隔
     */
    private static final int REFRESH_INTERVAL = 500;

    private class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mRenderController.refreshState();
            sendEmptyMessageDelayed(0, REFRESH_INTERVAL);
        }
    }


    public void mock(Object obj) {
//        getPosition();
        mControlHandler.sendEmptyMessage(ACTION_MOCK);

    }

    public interface RenderCallback {
        void onPrepared();
    }
}
