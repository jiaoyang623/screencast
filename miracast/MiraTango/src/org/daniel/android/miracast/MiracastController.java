package org.daniel.android.miracast;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * 用于Miracast的生命周期控制和事件分发
 * Howto:<br/>
 * 1. new MiracastController()<br/>
 * 2. setMiracastListener()<br/>
 * 3. setContentView()<br/>
 * 4. start()<br/>
 * 5. stop()<br/>
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 20 2015 5:38 PM
 */
public class MiracastController {
    private static final String TAG = "jy";

    private Context mContext;
    private MediaRouter mMediaRouter;
    private DisplayManager mDisplayManager;
    private View mContentView;
    private boolean mEnabled = false;
    /**
     * mPresentation 为null，则不在播放
     * 不为null，则上一个状态是在播放
     */
    private WindowController mPresentation;
    private MiracastListener mListener;

    private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() {
        @Override
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteSelected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
            updatePresentation();
        }
    };

    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            Log.d(TAG, "onDisplayAdded: id=" + displayId);
            updatePresentation();
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            Log.d(TAG, "onDisplayRemoved: id=" + displayId);
            updatePresentation();
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.d(TAG, "onDisplayChanged: id=" + displayId);
            updatePresentation();
        }
    };

    public MiracastController(Context context) {
        if (context == null || context instanceof Activity) {
            throw new IllegalArgumentException("context should not be null or instance of Activity");
        }
        mContext = context;
        mMediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        mContentView = new View(context);
        mEnabled = false;
        mPresentation = null;
    }

    /**
     * 启动投屏监听
     */
    public void start() {
        mEnabled = true;
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);
        updatePresentation();
    }

    /**
     * 停止投屏
     *
     * @test <br/>
     * 1. 投射中调用停止
     * 2. 没有投射时调用停止
     */
    public void stop() {
        mEnabled = false;
        mMediaRouter.removeCallback(mMediaRouterCallback);
        mDisplayManager.unregisterDisplayListener(mDisplayListener);
        updatePresentation();
    }

    /**
     * 设置绘屏<br/>
     * 1. set之后会不会马上刷新屏幕？切换到当前View<br/>
     * 2. set null的时候，会不会马上把正在显示的View删除？
     */
    public void setContentView(View contentView) {
        //TODO
        if (mContentView != contentView) {
            if (mPresentation != null) {
                mPresentation.removeView(mContentView);
                mPresentation.addView(contentView);
            }
            mContentView = contentView;
        }
    }

    /**
     * @test <br>
     * 1. mPresentation{null, obj} <br>
     * a. 正在播放
     * b. 不在播放
     * 2. presentationDisplay{null, obj} <br>
     * a. 没有远程显示器
     * b. 有远程播放器
     * 3. presentationDisplay==mPresentation.getDisplay() <br>
     * a. 远程播放器不一致
     * b. 远程播放器一致
     * 4. enable
     * a. true 允许播放
     * b. false 不许播放
     */
    private void updatePresentation() {
        // Get the current route and its presentation display.
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // 正在播放，调用停止或者屏幕变化时，停止播放
        if (mPresentation != null && !(mEnabled && mPresentation.getDisplay() == presentationDisplay)) {
            Log.i(TAG, "清除显示");
            mPresentation.removeView(mContentView);
            mPresentation = null;
            if (mListener != null) {
                mListener.onDismissed();
            }
        }

        // 显示播放器
        if (mPresentation == null && mEnabled && presentationDisplay != null) {
            Log.i(TAG, "显示: " + presentationDisplay);
            mPresentation = new WindowController(mContext, presentationDisplay);
            mPresentation.addView(mContentView);
            if (mListener != null) {
                mListener.onShow();
            }
        }
    }

    /**
     * 1. set空之后回怎么样？<br/>
     * 2. 替换对象回怎么样？<br/>
     */
    public void setListener(MiracastListener listener) {
        mListener = listener;
    }

    public interface MiracastListener {
        void onShow();

        void onDismissed();
    }


}
