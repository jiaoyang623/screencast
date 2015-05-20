package org.daniel.android.airplay.protocol;

import com.longevitysoft.android.xml.plist.domain.Array;
import com.longevitysoft.android.xml.plist.domain.Dict;

import java.util.Arrays;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 5:42 PM
 */
public class PlaybackInfoBean {
    /*
        <key>duration</key>		<real>1801</real>

		<key>loadedTimeRanges</key>
		<array>
			<dict>
				<key>duration</key>				<real>51.541130402</real>
				<key>start</key>				<real>18.118717650000001</real>
			</dict>
		</array>

		<key>playbackBufferEmpty</key>		<true/>
		<key>playbackBufferFull</key>		<false/>
		<key>playbackLikelyToKeepUp</key>		<true/>
		<key>position</key>		<real>18.043869775000001</real>
		<key>rate</key>		<real>1</real>
		<key>readyToPlay</key>		<true/>

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
    public float duration;
    public boolean playbackBufferEmpty;
    public boolean playbackBufferFull;
    public boolean playbackLikelyToKeepUp;
    public float position;
    public float rate;
    public boolean readyToPlay;
    public TimeRanges[] loadedTimeRanges;
    public TimeRanges[] seekableTimeRanges;

    public static class TimeRanges {
        public float duration;
        public float start;

        public static TimeRanges parse(Dict dict) {
            TimeRanges ranges = new TimeRanges();

            ranges.duration = dict.getFloat("duration");
            ranges.start = dict.getFloat("start");

            return ranges;
        }

        @Override
        public String toString() {
            return "TimeRanges{" +
                    "duration=" + duration +
                    ", start=" + start +
                    '}';
        }
    }

    public static PlaybackInfoBean parse(Dict dict) {
        PlaybackInfoBean bean = new PlaybackInfoBean();
        bean.duration = dict.getFloat("duration");
        bean.playbackBufferEmpty = dict.getBoolean("playbackBufferEmpty");
        bean.playbackBufferFull = dict.getBoolean("playbackBufferFull");
        bean.playbackLikelyToKeepUp = dict.getBoolean("playbackLikelyToKeepUp");
        bean.position = dict.getFloat("position");
        bean.rate = dict.getFloat("rate");
        bean.readyToPlay = dict.getBoolean("readyToPlay");
        bean.loadedTimeRanges = getArray(dict.getArray("loadedTimeRanges"));
        bean.seekableTimeRanges = getArray(dict.getArray("seekableTimeRanges"));

        return bean;
    }

    private static TimeRanges[] getArray(Array array) {
        if (array == null || array.size() == 0) {
            return new TimeRanges[0];
        }
        final int size = array.size();
        TimeRanges[] ranges = new TimeRanges[size];

        for (int i = 0; i < size; i++) {
            ranges[i] = TimeRanges.parse((Dict) array.get(i));
        }

        return ranges;
    }

    @Override
    public String toString() {
        return "PlaybackInfoBean{" +
                "duration=" + duration +
                ", playbackBufferEmpty=" + playbackBufferEmpty +
                ", playbackBufferFull=" + playbackBufferFull +
                ", playbackLikelyToKeepUp=" + playbackLikelyToKeepUp +
                ", position=" + position +
                ", rate=" + rate +
                ", readyToPlay=" + readyToPlay +
                ", loadedTimeRanges=" + Arrays.toString(loadedTimeRanges) +
                ", seekableTimeRanges=" + Arrays.toString(seekableTimeRanges) +
                '}';
    }
}
