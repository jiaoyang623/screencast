package org.daniel.android.airplay;

import android.test.AndroidTestCase;
import android.util.Log;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 19 2015 9:52 AM
 */
@FixMethodOrder(MethodSorters.DEFAULT)
public class RenderControllerTestCase extends AndroidTestCase {

    public RenderControllerTestCase() {
        Log.i("jy", "new instance RenderControllerTestCase");
    }

    private RenderController mController;

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        Log.i("jy", "teardown");
    }

    public void testAll() throws Exception {
        mController = new RenderController(DeviceControllerTestCase.getServiceInfo(getContext()));

        play();
        seek();
        rate();
    }

    public void play() throws Exception {
        Log.i("test", "play");

        mController.play("http://video19.ifeng.com/video07/2013/11/11/281708-102-007-1138.mp4", 0);
        Thread.sleep(1000 * 10);

//        mController.play(null, 0);
//        mController.play("http://www.baidu.com", 0);
//        mController.play("http://127.0.0.1/a.b", 0);
    }

    public void rate() throws Exception {
        Log.i("test", "pause");

        mController.rate(0);
        Thread.sleep(1000 * 5);

        Log.i("test", "rate");
        mController.rate(1);
        Thread.sleep(1000 * 5);

//        mController.rate(0.5f);
//        mController.rate(-1);
//        mController.rate(0.000001f);
//        mController.rate(0.999999f);
//        mController.rate(10);
//        mController.rate(0xffffff);
    }

    public void seek() throws Exception {
        Log.i("test", "seek");

        mController.seek(0);
//        mController.seek(0);
//        mController.seek(-10);
//        mController.seek(0xffffff);
        Thread.sleep(1000 * 5);
    }

//    public void test6Stop() throws Exception {
//        Log.i("test", "stop");
//        mController.stop();
//    }


//    public void test3GetServerInfo() throws Exception {
//        Log.i("jy", "getServerInfoTest");
//        ServerInfoBean bean = mController.getServerInfo();
//        assertNotNull(bean);
//        assertNotNull(bean.srcvers);
//        assertNotNull(bean.deviceId);
//        assertNotNull(bean.features);
//        assertNotNull(bean.model);
//        assertNotNull(bean.protovers);
//        Log.i("jy", "getServerInfo: " + bean.toString());
//    }

//    public void test4GetPlayInfo() throws Exception {
//        PlaybackInfoBean bean = mController.getPlayInfo();
//        assertNotNull(bean);
//        Log.i("jy", bean.toString());
//
//        assertTrue(bean.duration > 0);
//        assertTrue(bean.position >= 0);
//        assertTrue(bean.position <= 1);
//        assertNotNull(bean.loadedTimeRanges);
//        assertNotNull(bean.seekableTimeRanges);
//        assertTrue(bean.rate >= 0);
//        assertTrue(bean.rate <= 1);
//    }

//    public void test5GetProgress() throws IOException {
//        float[] position = mController.getProgress();
//        assertTrue(position[0] > 0);
//        assertTrue(position[1] > 0);
//    }

//    public void testRequest() throws Exception {
//        Log.i("jy", mController.request("http://10.18.175.130:7000/playback-info", null, null));

//        Map<String, String> mapGet = new HashMap<>();
//        Map<String, String> mapPost = new HashMap<>();
//
//        mapGet.put("abc", "def");
//        String content = mController.request("http://www.baidu.com", mapGet, mapPost);
//        Log.i("jy", content);
//
//        mapGet.put("haha", null);
//        mapPost.put("where", null);
//        content = mController.request("http://www.baidu.com", mapGet, mapPost);
//        Log.i("jy", content);
//
//        try {
//            mController.request("127.0.0.1", null, null);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            mController.request("http://127.0.0.1", null, null);
//        } catch (ConnectException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            mController.request(null, null, null);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//    }
}
