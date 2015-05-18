package org.daniel.android.airplay;

import java.net.URL;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 18 2015 2:26 PM
 */
public interface IRenderController {
    void play(URL url);

    void rate(int rate);

    void seek(int position);

    void stop();

    void getInfo();

}
