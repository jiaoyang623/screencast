package org.daniel.android.dlna;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
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
    private SeekBar mPositionbar;

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

        mRenderController = new ThreadRenderController();
        mRenderController.setCallback(this);
        mPositionbar = (SeekBar) findViewById(R.id.position);
        SeekBar volumebar = (SeekBar) findViewById(R.id.volume);
        SeekBar brightnessbar = (SeekBar) findViewById(R.id.brightness);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPositionbar.setOnSeekBarChangeListener(this);
        volumebar.setOnSeekBarChangeListener(this);
        brightnessbar.setOnSeekBarChangeListener(this);
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
        mRenderController.setDevice(device);
    }

    @Override
    public void onPrepared() {
        mPositionbar.setMax(mRenderController.getLength());
        mPositionRunnable = new PositionRunnable(UIHandler.getInstance());
        UIHandler.getInstance().post(mPositionRunnable);
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
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000);
        }
    }
}
