package org.daniel.android.miracast;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, MiracastController.MiracastListener {
    private TextView mStatusText;
    private MiracastService mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MiracastService.MiraBinder) service).getService();
            mService.setMiracastListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mStatusText = (TextView) findViewById(R.id.status);
        bindService(new Intent(getApplicationContext(), MiracastService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (mService != null) {
                    mService.start();
                }
                break;
            case R.id.stop:
                if (mService != null) {
                    mService.stop();
                }
                break;
        }
    }

    @Override
    public void onShow() {
        mStatusText.setText("onShow");
    }

    @Override
    public void onDismissed() {
        mStatusText.setText("onDismissed");
    }
}
