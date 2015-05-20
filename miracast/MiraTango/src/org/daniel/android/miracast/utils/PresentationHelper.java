
package org.daniel.android.miracast.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;
import org.daniel.android.miracast.utils.TU;

public class PresentationHelper implements
        DisplayManager.DisplayListener {
    public interface Listener {
        void showPreso(Display display);

        void clearPreso(boolean switchToInline);
    }

    private Listener mListener = null;
    private DisplayManager mDisplayManager = null;
    private Display mCurrentDisplay = null;
    private boolean isFirstRun = true;
    private boolean isEnabled = true;

    public PresentationHelper(Context context, Listener listener) {
        this.mListener = listener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mDisplayManager =
                    (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        }
    }

    public void onResume() {
        TU.j("onResume", Build.VERSION.SDK_INT, ">=", Build.VERSION_CODES.JELLY_BEAN_MR1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            handleRoute();
            mDisplayManager.registerDisplayListener(this, null);
        }
    }

    public void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mListener.clearPreso(false);
            mCurrentDisplay = null;

            mDisplayManager.unregisterDisplayListener(this);
        }
    }

    public void enable() {
        isEnabled = true;
        handleRoute();
    }

    public void disable() {
        isEnabled = false;

        if (mCurrentDisplay != null) {
            mListener.clearPreso(true);
            mCurrentDisplay = null;
        }
    }

    public boolean isEnabled() {
        return (isEnabled);
    }

    private void handleRoute() {
        if (isEnabled()) {
            Display[] displays =
                    mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

            if (displays.length == 0) {
                if (mCurrentDisplay != null || isFirstRun) {
                    //清除屏幕
                    mListener.clearPreso(true);
                    mCurrentDisplay = null;
                }
            } else {
                Display display = displays[0];

                if (display != null && display.isValid()) {
                    if (mCurrentDisplay == null) {
                        //显示当前的屏幕
                        mListener.showPreso(display);
                        mCurrentDisplay = display;
                    } else if (mCurrentDisplay.getDisplayId() != display.getDisplayId()) {
                        //屏幕切换
                        mListener.clearPreso(true);
                        mListener.showPreso(display);
                        mCurrentDisplay = display;
                    } else {
                        // no-op: should already be set
                    }
                } else if (mCurrentDisplay != null) {
                    //清除屏幕
                    mListener.clearPreso(true);
                    mCurrentDisplay = null;
                }
            }

            isFirstRun = false;
        }
    }

    @Override
    public void onDisplayAdded(int displayId) {
        TU.j("onDisplayAdded", displayId);
        handleRoute();
    }

    @Override
    public void onDisplayChanged(int displayId) {
        TU.j("onDisplayChanged", displayId);
        handleRoute();
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        TU.j("onDisplayRemoved", displayId);
        handleRoute();
    }
}