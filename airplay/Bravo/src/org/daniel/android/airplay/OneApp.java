package org.daniel.android.airplay;

import android.app.Application;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 18 2015 3:02 PM
 */
public class OneApp extends Application {
    public static OneApp INSTANCE;

    public OneApp() {
        INSTANCE = this;
    }
}
