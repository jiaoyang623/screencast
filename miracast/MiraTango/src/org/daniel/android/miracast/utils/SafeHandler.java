package org.daniel.android.miracast.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date Apr 09 2015 2:43 PM
 */

public abstract class SafeHandler<T> extends Handler {
    protected WeakReference<T> ref = null;

    public SafeHandler(T t) {
        ref = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        T t = ref.get();
        if (t != null) {
            handlerMessage(msg, t);
        }
    }

    protected abstract void handlerMessage(Message msg, T t);
}
