package org.daniel.android.miracast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 21 2015 3:44 PM
 */
public class MiracastService extends Service {
    private MiracastController mController;

    @Override
    public IBinder onBind(Intent intent) {
        return new MiraBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mController = new MiracastController(getApplicationContext());
    }

    public void start() {
        mController.start();
    }

    public void stop() {
        mController.stop();
    }

    public void setView(View view) {
        mController.setContentView(view);
    }

    public void setMiracastListener(MiracastController.MiracastListener listener) {
        mController.setListener(listener);
    }

    public class MiraBinder extends Binder {
        public MiracastService getService() {
            return MiracastService.this;
        }
    }
}
