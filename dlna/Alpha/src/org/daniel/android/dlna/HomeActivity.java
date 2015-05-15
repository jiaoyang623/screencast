package org.daniel.android.dlna;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.util.Debug;

public class HomeActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, DeviceChangeListener, AdapterView.OnItemClickListener, ThreadRenderController.RenderCallback {
    private DeviceController mDeviceController;
    private ThreadRenderController mRenderController;
    //    private static final String SAMPLE_URL = "http://video19.ifeng.com/video07/2013/11/11/281708-102-007-1138.mp4";
    private static final String SAMPLE_URL = "http://10.18.175.68:8080/i/1.mp4";
    private ListView mListView;
    private HomeAdapter mAdapter = new HomeAdapter();
    private SeekBar mPositionbar, mBrightnessbar, mVolumebar;
    private TextView mVolumeText, mBrightnessText;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Debug.off();

        setContentView(R.layout.main);
        mDeviceController = new DeviceController();
        mDeviceController.setOnDeviceChangedListener(this);

        mPositionbar = (SeekBar) findViewById(R.id.position);
        mVolumebar = (SeekBar) findViewById(R.id.volume);
        mBrightnessbar = (SeekBar) findViewById(R.id.brightness);
        mVolumeText = (TextView) findViewById(R.id.volumeText);
        mBrightnessText = (TextView) findViewById(R.id.brightnessText);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPositionbar.setOnSeekBarChangeListener(this);
        mVolumebar.setOnSeekBarChangeListener(this);
        mBrightnessbar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        mDeviceController.stopScan();
        mRenderController.quit();
        UIHandler.getInstance().removeCallbacks(mPositionRunnable);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                mDeviceController.scan();
                break;
            case R.id.play:
                mRenderController.play();
                break;
            case R.id.setDataSource:
                setDataSource();
                break;
            case R.id.pause:
                pause();
                break;
            case R.id.stop:
                stop();
                break;
            case R.id.position:
                Log.i("jy", "getPosition: " + mRenderController.getPosition());
                break;
            case R.id.volume:
//                Log.i("jy", "getVolume: " + mRenderController.getVolume());
                break;
            case R.id.brightness:
//                Log.i("jy", "getBrightness: " + mRenderController.getBrightness());
                break;
            case R.id.mock:
                mRenderController.mock(null);
//                if (mDLNAController.getLength() != 0) {
//                    mPositionbar.setMax(mDLNAController.getLength());
//                }
            default:
                break;
        }

    }

    private void setDataSource() {
        mRenderController.setDataSource(SAMPLE_URL);
    }

    private void pause() {
        mRenderController.pause();
    }

    private void stop() {
        mRenderController.stop();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Log.i("jy", "to seek: " + progress);
        switch (seekBar.getId()) {
            case R.id.position: {
                mRenderController.seek(progress);
            }
            break;
            case R.id.brightness: {
                mRenderController.setBrightness(progress);
            }
            break;
            case R.id.volume: {
                mRenderController.setVolume(progress);
            }
            break;
        }
    }


    @Override
    public void deviceAdded(Device dev) {
        Toast.makeText(this, "deviceAdded", Toast.LENGTH_LONG).show();
        mAdapter.setData(mDeviceController.getDeviceList());
    }

    @Override
    public void deviceRemoved(Device dev) {
        Toast.makeText(this, "deviceRemoved", Toast.LENGTH_LONG).show();
        mAdapter.setData(mDeviceController.getDeviceList());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device device = mDeviceController.getDeviceList().get(position);
        if (mRenderController != null) {
            mRenderController.quit();
        }
        mRenderController = new ThreadRenderController(device);
        mRenderController.setCallback(this);
    }

    @Override
    public void onPrepared() {
        mPositionbar.setMax(mRenderController.getLength());
        mPositionRunnable = new PositionRunnable(UIHandler.getInstance());
        UIHandler.getInstance().post(mPositionRunnable);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "DLNA error", Toast.LENGTH_LONG).show();
    }

    private PositionRunnable mPositionRunnable;

    private class PositionRunnable implements Runnable {
        private Handler handler;

        public PositionRunnable(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            mPositionbar.setMax(mRenderController.getLength());
            mPositionbar.setProgress(mRenderController.getPosition());
//            Log.i("jy", "position: " + mRenderController.getPosition() + " / " + mRenderController.getLength());
            if (mRenderController.isBrightnessEnabled()) {
                mBrightnessbar.setProgress(mRenderController.getBrightness());
                mBrightnessText.setText("Brightness(enabled)");
            } else {
                mBrightnessText.setText("Brightness(disabled)");
            }
            if (mRenderController.isVolumeEnabled()) {
                mVolumebar.setProgress(mRenderController.getVolume());
                mVolumeText.setText("Volume(enabled)");
            } else {
                mVolumeText.setText("Volume(disabled)");
            }
            mVolumebar.setProgress(mRenderController.getVolume());
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000);
        }
    }
}
