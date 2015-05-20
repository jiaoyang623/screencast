package org.daniel.android.miracast.switcher;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.View;
import org.daniel.android.miracast.R;
import org.daniel.android.miracast.utils.TU;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date Apr 09 2015 3:51 PM
 */
public class MiraDiscoverActivity extends Activity implements View.OnClickListener, DisplayManager.DisplayListener {
    private DisplayManagerWrapper mDisplayerManagerWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        mDisplayerManagerWrapper = new DisplayManagerWrapper((DisplayManager) getSystemService(DISPLAY_SERVICE));

        mDisplayerManagerWrapper.registerDisplayListener(this, null);
    }

    @Override
    protected void onDestroy() {
        mDisplayerManagerWrapper.unregisterDisplayListener(this);
        super.onDestroy();
    }

    private boolean canCallerConfigureWifiDisplay() {
        return checkCallingPermission("android.permission.CONFIGURE_WIFI_DISPLAY")
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover: {
                TU.j("check permission", canCallerConfigureWifiDisplay());
                mDisplayerManagerWrapper.startWifiDisplayScan();
                mDisplayerManagerWrapper.connectWifiDisplay("d");
                mDisplayerManagerWrapper.disconnectWifiDisplay();
                mDisplayerManagerWrapper.renameWifiDisplay("a", "b");
                mDisplayerManagerWrapper.forgetWifiDisplay("c");
            }
            break;
        }
    }

    @Override
    public void onDisplayAdded(int displayId) {
        TU.j("MiraDiscoverActivity", "onDisplayAdded", displayId);
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        TU.j("MiraDiscoverActivity", "onDisplayRemoved", displayId);
    }

    @Override
    public void onDisplayChanged(int displayId) {
        TU.j("MiraDiscoverActivity", "onDisplayChanged", displayId);
    }
}
