package org.daniel.android.dlna;

import android.os.Handler;
import android.os.Looper;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 4:25 PM
 */
public class UIHandler extends Handler {
    private static UIHandler INSTANCE = new UIHandler(Looper.getMainLooper());

    public static Handler getInstance() {
        return INSTANCE;
    }

    private UIHandler(Looper looper) {
        super(looper);
    }
}
