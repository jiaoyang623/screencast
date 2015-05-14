package org.daniel.android.dlna;

import org.cybergarage.upnp.Device;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 11 2015 2:43 PM
 */
public interface IRenderController {
    void setDevice(Device device);

    void setDataSource(String uri);

    int getLength();

    void seek(int position);

    int getPosition();

    void play();

    void pause();

    void stop();

    int getBrightness();

    void setBrightness(int brightness);

    int getVolume();

    void setVolume(int volume);

    void mock(Object obj);
}
