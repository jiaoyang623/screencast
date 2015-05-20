package org.daniel.android.miracast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import org.daniel.android.miracast.switcher.MiraDiscoverActivity;
import org.daniel.android.miracast.utils.PresentationHelper;

public class MainActivity extends Activity implements PresentationHelper.Listener, View.OnClickListener {
    private PresentationHelper mPresentationHelper;
    private SamplePresentationFragment preso = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPresentationHelper = new PresentationHelper(getApplicationContext(), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start: {
                preso.start("/sdcard/test.mp4");
            }
            break;
            case R.id.stop: {
                preso.stop();
            }
            break;
            case R.id.discover: {
                startActivity(new Intent(this, MiraDiscoverActivity.class));
            }
            break;
            default:
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresentationHelper.onResume();
//        mVideoView.start();
    }

    @Override
    public void onPause() {
        mPresentationHelper.onPause();
        super.onPause();
//        mVideoView.stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void clearPreso(boolean switchToInline) {
        System.out.println("clear Presentation");
        if (preso != null) {
            preso.dismiss();
            preso = null;
        }
    }

    @Override
    public void showPreso(Display display) {
        System.out.println("show Presentation");
        preso = buildPreso(display);
        preso.show(getFragmentManager(), "preso");
    }

    private SamplePresentationFragment buildPreso(Display display) {
        return (SamplePresentationFragment.newInstance(this, display,
                "http://www.baidu.com"));
    }

}
