package org.daniel.android.miracast.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 22 2015 5:45 PM
 */
public class DemoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private MediaPlayer mMediaPlayer;

    public DemoSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public DemoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DemoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mMediaPlayer = new MediaPlayer();
        getHolder().setKeepScreenOn(true);
        getHolder().addCallback(this);
    }

    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("jy", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("jy", "surfaceChanged");
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("jy", "surfaceDestroyed");
        mMediaPlayer.stop();
    }
}
