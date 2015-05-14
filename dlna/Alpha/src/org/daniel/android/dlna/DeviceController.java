package org.daniel.android.dlna;

import android.util.Log;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 11 2015 2:43 PM
 */
public class DeviceController implements DeviceChangeListener {
    private ControlPoint mControlPoint;
    private SearchThread mThread;
    private List<Device> mDeviceList;

    private DeviceChangeListener mListener;

    private static final String MEDIARENDER = "urn:schemas-upnp-org:device:MediaRenderer:1";

    public DeviceController() {
        mControlPoint = new ControlPoint();
        mControlPoint.addDeviceChangeListener(this);
        mDeviceList = new ArrayList<Device>();
    }

    public void scan() {
        if (mThread == null) {
            mDeviceList.clear();
            mThread = new SearchThread();
            mThread.start();
        }
    }

    public void stopScan() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    public List<Device> getDeviceList() {
        return new ArrayList<Device>(mDeviceList);
    }

    @Override
    public void deviceAdded(final Device dev) {
        Log.i("jy", Thread.currentThread().getId() + " deviceAdded: " + dev.getDeviceType() + " -> " + dev.getUDN());

        if (dev == null || !MEDIARENDER.equals(dev.getDeviceType())) {
            return;
        }

        if (!mDeviceList.contains(dev)) {
            mDeviceList.add(dev);
            if (mListener != null) {
                UIHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.deviceAdded(dev);
                    }
                });
            }
        }
    }

    @Override
    public void deviceRemoved(final Device dev) {
        Log.i("jy", Thread.currentThread().getId() + " deviceRemoved: " + dev.getUDN());
        if (mDeviceList.contains(dev)) {
            mDeviceList.remove(dev);
            if (mListener != null) {
                UIHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.deviceRemoved(dev);
                    }
                });
            }
        }
    }


    private class SearchThread extends Thread {
        @Override
        public void run() {
            Thread t = Thread.currentThread();
            Log.i("jy", "Search thread started " + t.getId());
            boolean startComplete = false;
            while (!t.isInterrupted()) {
                try {
                    if (startComplete) {
                        mControlPoint.search();
//                        Log.i("jy", "controlpoint search...");
                    } else {
                        mControlPoint.stop();
                        boolean startRet = mControlPoint.start();
//                        Log.i("jy", "controlpoint start:" + startRet);
                        if (startRet) {
                            startComplete = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    t.sleep(15 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            mThread = null;
        }
    }

    public void setOnDeviceChangedListener(DeviceChangeListener listener) {
        mListener = listener;
    }
}
