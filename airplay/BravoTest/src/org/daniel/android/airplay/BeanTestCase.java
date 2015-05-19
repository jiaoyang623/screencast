package org.daniel.android.airplay;

import android.test.AndroidTestCase;
import android.util.Log;
import com.longevitysoft.android.xml.plist.domain.Dict;
import org.daniel.android.airplay.protocol.PlaybackInfoBean;
import org.daniel.android.airplay.protocol.ServerInfoBean;
import org.junit.Test;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 5:29 PM
 */
public class BeanTestCase extends AndroidTestCase {

    @Test
    public void testServerInfoBean() {
        Dict dict = Utils.getDictFromAsset("server-info.plist", getContext());
        assertNotNull(dict);
        ServerInfoBean bean = ServerInfoBean.parse(dict);

        assertEquals(bean.deviceId, "58:55:CA:1A:E2:88");
        assertEquals(bean.features, 14839);
        assertEquals(bean.model, "AppleTV2,1");
        assertEquals(bean.protovers, "1.0");
        assertEquals(bean.srcvers, "120.2");
    }

    /*
        <key>duration</key>		<real>1801</real>

		<key>playbackBufferEmpty</key>		<true/>
		<key>playbackBufferFull</key>		<false/>
		<key>playbackLikelyToKeepUp</key>		<true/>
		<key>position</key>		<real>18.043869775000001</real>
		<key>rate</key>		<real>1</real>
		<key>readyToPlay</key>		<true/>

        <key>loadedTimeRanges</key>
		<array>
			<dict>
				<key>duration</key>				<real>51.541130402</real>
				<key>start</key>				<real>18.118717650000001</real>
			</dict>
		</array>

		<key>seekableTimeRanges</key>
		<array>
			<dict>
				<key>duration</key>
				<real>1801</real>
				<key>start</key>
				<real>0.0</real>
			</dict>
		</array>
    * */

    @Test
    public void testPlaybackInfoBean() {
        Dict dict = Utils.getDictFromAsset("playback-info.plist", getContext());
        assertNotNull(dict);
        PlaybackInfoBean bean = PlaybackInfoBean.parse(dict);

        Log.i("jy", bean.toString());

        assertEquals(bean.duration, 1801f);
        assertEquals(bean.playbackBufferEmpty, true);
        assertEquals(bean.playbackBufferFull, false);
        assertEquals(bean.playbackLikelyToKeepUp, true);
        assertEquals(bean.position, 18.043869775000001f);
        assertEquals(bean.rate, 1f);
        assertEquals(bean.readyToPlay, true);
        assertEquals(bean.loadedTimeRanges.length, 1);
        assertEquals(bean.loadedTimeRanges[0].duration, 51.541130402f);
        assertEquals(bean.loadedTimeRanges[0].start, 18.118717650000001f);

        assertEquals(bean.seekableTimeRanges.length, 1);
        assertEquals(bean.seekableTimeRanges[0].duration, 1801f);
        assertEquals(bean.seekableTimeRanges[0].start, 0.0f);
    }
}
