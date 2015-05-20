package org.daniel.android.airplay;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import javax.jmdns.ServiceInfo;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 19 2015 2:33 PM
 */
public class DeviceControllerTestCase extends AndroidTestCase {
    private DeviceController mController;

    @Override
    protected void setUp() throws Exception {
        if (mController == null) {
            mController = new DeviceController();
        }
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testScan() {
        ServiceInfo info = getServiceInfo(getContext());
        assertNotNull(info);
        Log.i("jy", info.toString());
    }

    public void testGetWifiInetAddress() throws SocketException {
        InetAddress address = mController.getWifiInetAddress();
        assertNotNull(address);
        Log.i("jy", "address: " + address);
    }

    public void testStopScan() {
        mController.stopScan();
    }


    public static ServiceInfo getServiceInfo(Context context) {
        final DeviceController controller = new DeviceController();
        final ServiceInfo[] container = new ServiceInfo[1];
        controller.setDeviceListener(new IDeviceController.DeviceListener() {
            @Override
            public void onDeviceAdded(ServiceInfo info) {
                container[0] = info;
                synchronized (container) {
                    container.notify();
                }
            }

            @Override
            public void onDeviceRemoved(ServiceInfo info) {

            }
        });
        controller.scan(context);
        synchronized (container) {
            try {
                container.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ServiceInfo info = container[0];
        Log.i("jy", "info: " + info.getURL());

        controller.stopScan();

        return info;
    }
}
