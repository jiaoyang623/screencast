package org.daniel.android.miracast.widgets;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date Apr 09 2015 3:03 PM
 */
public class VideoSurface extends SurfaceView implements SurfaceHolder.Callback {
    private MediaPlayer mMediaPlayer;

    public VideoSurface(Context context) {
        super(context);
        init(context);
    }

    public VideoSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        mMediaPlayer = new MediaPlayer();
        getHolder().setKeepScreenOn(true);
        getHolder().addCallback(this);
    }

    public void start(String path) throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    public void stop() {
        mMediaPlayer.stop();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
