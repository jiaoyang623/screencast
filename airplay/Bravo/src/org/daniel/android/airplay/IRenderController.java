package org.daniel.android.airplay;

import org.daniel.android.airplay.protocol.PlaybackInfoBean;
import org.daniel.android.airplay.protocol.ServerInfoBean;

import java.io.IOException;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:26 PM
 */
public interface IRenderController {
    void play(String url, float position) throws IOException;

    /**
     * from 0 to 1
     */
    void rate(float rate) throws IOException;

    void seek(float position) throws IOException;

    void stop() throws IOException;

    ServerInfoBean getServerInfo() throws IOException;

    PlaybackInfoBean getPlayInfo() throws IOException;

    float[] getProgress() throws IOException;

}
