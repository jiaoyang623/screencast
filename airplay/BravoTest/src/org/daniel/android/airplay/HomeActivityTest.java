package org.daniel.android.airplay;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.longevitysoft.android.xml.plist.domain.Dict;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.daniel.android.airplay.HomeActivityTest \
 * org.daniel.android.airplay.tests/android.test.InstrumentationTestRunner
 */
public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {

    public HomeActivityTest() {
        super(HomeActivity.class);
    }


    @Test
    public void testParse() throws Exception {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = OneApp.INSTANCE.getAssets().open("server-info.plist");
            reader = new InputStreamReader(is);
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, count);
            }
            Log.i("jy", builder.toString());

            Dict dict = PListUtils.parse(builder.toString());

            Assert.assertNotNull(dict);

            Log.i("jy", dict.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
