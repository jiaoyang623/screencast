package org.daniel.android.miracast.utils;

import android.util.Log;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date Apr 09 2015 11:31 AM
 */
public class TU {

    public static void j(Object... objs) {
        StringBuilder sBuilder = new StringBuilder();

        for (Object obj : objs) {
            if (obj != null) {
                sBuilder.append(obj);
                sBuilder.append(", ");
            }
        }
        Log.i("jy", sBuilder.toString());
    }
}
