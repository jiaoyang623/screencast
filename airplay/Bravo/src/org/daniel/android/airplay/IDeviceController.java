package org.daniel.android.airplay;

import android.content.Context;

import javax.jmdns.ServiceInfo;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:25 PM
 */
public interface IDeviceController {
    void scan(Context context);

    void stopScan();

    void setDeviceListener(DeviceListener listener);

    interface DeviceListener {
        void onDeviceAdded(ServiceInfo info);

        void onDeviceRemoved(ServiceInfo info);
    }
}
