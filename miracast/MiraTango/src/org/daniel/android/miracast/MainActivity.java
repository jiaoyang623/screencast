package org.daniel.android.miracast;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import org.daniel.android.miracast.player.DemoSurfaceView;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener, MiracastController.MiracastListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener {
    public static final String ACTION_WIFI_DISPLAY_SETTINGS = "android.settings.WIFI_DISPLAY_SETTINGS";
    private TextView mStatusText;
    private MiracastService mService;
    private DemoSurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekbar;
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
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mStatusText = (TextView) findViewById(R.id.status);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mSeekbar.setOnSeekBarChangeListener(this);
        mSurfaceView = new DemoSurfaceView(getApplicationContext());
        mMediaPlayer = mSurfaceView.getPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        bindService(new Intent(getApplicationContext(), MiracastService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {
                case R.id.startDisplay:
                    if (mService != null) {
                        mService.setView(mSurfaceView);
                        mService.start();
                    }
                    break;
                case R.id.stopDisplay:
                    if (mService != null) {
                        mService.stop();
                    }
                    break;
                case R.id.setData:
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse("http://video19.ifeng.com/video07/2013/11/11/281708-102-007-1138.mp4"));
                    mMediaPlayer.prepareAsync();
                    break;
                case R.id.play:
                    mMediaPlayer.start();
                    mHandler.sendEmptyMessage(0);
                    break;
                case R.id.pause:
                    mMediaPlayer.pause();
                    mHandler.removeMessages(0);
                    break;
                case R.id.stop:
                    mMediaPlayer.stop();
                    mHandler.removeMessages(0);
                    break;
                case R.id.settings:
                    Intent intent = new Intent(ACTION_WIFI_DISPLAY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShow() {
        mStatusText.setText("onShow");
    }

    @Override
    public void onDismissed() {
        mStatusText.setText("onDismissed");
        mMediaPlayer.stop();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        int duration = mp.getDuration();
        mStatusText.setText("onPrepared: " + duration);
        mSeekbar.setMax(duration);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mStatusText.setText("PlayerError(" + what + "): " + extra);
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.seekTo(seekBar.getProgress());
    }

}
