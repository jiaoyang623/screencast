package org.daniel.android.airplay;

import android.test.AndroidTestCase;
import android.util.Log;
import org.daniel.android.airplay.protocol.PlaybackInfoBean;
import org.daniel.android.airplay.protocol.ServerInfoBean;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 19 2015 9:52 AM
 */
public class RenderControllerTestCase extends AndroidTestCase {

    private RenderController mController;

    @Override
    protected void setUp() throws Exception {
        Log.i("jy", "setup");
        mController = new RenderController();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        Log.i("jy", "teardown");
        super.tearDown();
    }

    public void testPlay() throws Exception {
        mController.play("http://video19.ifeng.com/video07/2013/11/11/281708-102-007-1138.mp4");
        mController.play(null);
        mController.play("http://www.baidu.com");
        mController.play("http://127.0.0.1/a.b");
    }

    public void testRate() throws Exception {
        mController.rate(0);
        mController.rate(1);
        mController.rate(0.5f);
        mController.rate(-1);
        mController.rate(0.000001f);
        mController.rate(0.999999f);
        mController.rate(10);
        mController.rate(0xffffff);
    }

    public void testSeek() throws Exception {
        mController.seek(10);
        mController.seek(0);
        mController.seek(-10);
        mController.seek(0xffffff);
    }

    public void testStop() throws Exception {
        mController.stop();
        mController.stop();
    }


    public void testGetServerInfo() throws Exception {
        Log.i("jy", "getServerInfoTest");
        ServerInfoBean bean = mController.getServerInfo();
        assertNotNull(bean);
        assertNotNull(bean.srcvers);
        assertNotNull(bean.deviceId);
        assertNotNull(bean.features);
        assertNotNull(bean.model);
        assertNotNull(bean.protovers);
        Log.i("jy", "getServerInfo: " + bean.toString());
    }

    public void testGetPlayInfo() throws Exception {
        PlaybackInfoBean bean = mController.getPlayInfo();
        assertNotNull(bean);

        assertTrue(bean.duration > 0);
        assertTrue(bean.position >= 0);
        assertTrue(bean.position <= 1);
        assertNotNull(bean.loadedTimeRanges);
        assertNotNull(bean.seekableTimeRanges);
        assertTrue(bean.rate >= 0);
        assertTrue(bean.rate <= 1);
    }
}
