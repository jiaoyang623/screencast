package org.daniel.android.airplay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 19 2015 2:16 PM
 */
public class DeviceController implements IDeviceController, ServiceListener {
    private static final String SERVICE_TYPE = "_airplay._tcp.local.";
    private WifiManager.MulticastLock mLock;
    private InetAddress mDeviceAddress;
    private JmDNS jmdns;
    private DeviceListener mListener;


    @Override
    public void scan(Context context) {
        startScan(context, this);
    }

    @Override
    public void stopScan() {
        if (jmdns != null) {
            try {
                jmdns.removeServiceListener(SERVICE_TYPE, this);
                jmdns.close();
            } catch (Exception e) {
                Log.e("jy", "Error: " + e.getMessage());
            }
        }

        // release multicast lock
        if (mLock != null) {
            mLock.release();
        }
    }

    @Override
    public void setDeviceListener(DeviceListener listener) {
        mListener = listener;
    }


    void startScan(Context context, ServiceListener listener) {
        WifiManager wifi = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
        mLock = wifi.createMulticastLock("JmDNSLock");
        mLock.setReferenceCounted(true);
        mLock.acquire();

        // JmDNS
        try {
            // device address
            // local ip address
            mDeviceAddress = getWifiInetAddress();
            if (mDeviceAddress == null) {
                Log.e("jy", "Error: Unable to get local IP address");
                return;
            }

            // init jmdns
            jmdns = JmDNS.create(mDeviceAddress);
            jmdns.addServiceListener(SERVICE_TYPE, listener);
            Log.e("jy", "Using local address " + mDeviceAddress.getHostAddress());
        } catch (Exception e) {
            Log.e("jy", "Error: " + e.getMessage() == null ? "Unable to initialize discovery service" : e.getMessage());
        }
    }


    InetAddress getWifiInetAddress() throws SocketException {
        for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            //TODO wifi情况下, ethernet或者不叫wlan的时候，就会出问题
            if (!networkInterface.getName().contains("wlan")) {
                continue;
            }
            for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                System.out.println(inetAddress);
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
            }
        }
        return null;
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        Log.i("jy", "serviceAdded " + event);
        jmdns.requestServiceInfo(event.getType(), event.getName(), 1000);
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Log.i("jy", "serviceRemoved " + event);
        if (mListener != null) {
            mListener.onDeviceAdded(event.getInfo());
        }
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        Log.i("jy", "serviceRemoved " + event);
        if (mListener != null) {
            mListener.onDeviceAdded(event.getInfo());
        }
    }
}
