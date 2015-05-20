package org.daniel.android.miracast.switcher;

import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.view.Display;
import org.daniel.android.miracast.utils.TU;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * DisplayManager包装类，通过反射的方式调用受保护方法
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date Apr 09 2015 5:46 PM
 */
public class DisplayManagerWrapper {
    private DisplayManager mDisplayManager;

    public DisplayManagerWrapper(DisplayManager displayManager) {
        this.mDisplayManager = displayManager;
    }

    public Display getDisplay(int displayId) {
        return mDisplayManager.getDisplay(displayId);
    }

    public Display[] getDisplays() {
        return mDisplayManager.getDisplays();
    }

    public Display[] getDisplays(String category) {
        return mDisplayManager.getDisplays(category);
    }

    public void registerDisplayListener(DisplayManager.DisplayListener listener, Handler handler) {
        mDisplayManager.registerDisplayListener(listener, handler);
    }

    public void unregisterDisplayListener(DisplayManager.DisplayListener listener) {
        mDisplayManager.unregisterDisplayListener(listener);
    }

    public void scanWifiDisplays() {
        invoke("scanWifiDisplays");
    }

    public void startWifiDisplayScan() {
        invoke("startWifiDisplayScan");
    }

    public void connectWifiDisplay(String deviceAddress) {
        invoke("connectWifiDisplay", deviceAddress);

    }

    public void disconnectWifiDisplay() {
        invoke("disconnectWifiDisplay");

    }

    public void renameWifiDisplay(String deviceAddress, String alias) {
        invoke("renameWifiDisplay", deviceAddress, alias);

    }

    public void forgetWifiDisplay(String deviceAddress) {
        invoke("forgetWifiDisplay", deviceAddress);
    }

    private boolean invoke(String methodName, Object... params) {
        boolean result = false;
        try {
            Class clazz = mDisplayManager.getClass();
            Method method;
            if (params != null && params.length > 0) {
                Class<?>[] paramTypes = new Class<?>[params.length];
                int i = 0;
                for (Object param : params) {
                    paramTypes[i++] = param.getClass();
                }
                method = clazz.getDeclaredMethod(methodName, paramTypes);

            } else {
                method = clazz.getDeclaredMethod(methodName);
            }
            method.invoke(mDisplayManager, params);
            result = true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        TU.j("DisplayManagerWrapper.invoke", result, methodName, params);

        return result;
    }


}
