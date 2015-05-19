package org.daniel.android.airplay;

import org.daniel.android.airplay.protocol.PlaybackInfoBean;
import org.daniel.android.airplay.protocol.ServerInfoBean;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:26 PM
 */
public interface IRenderController {
    void play(String url);

    /**
     * from 0 to 1
     */
    void rate(float rate);

    void seek(float position);

    void stop();

    ServerInfoBean getServerInfo();

    PlaybackInfoBean getPlayInfo();

}
