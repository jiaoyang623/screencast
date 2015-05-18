package org.daniel.android.airplay;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:25 PM
 */
public interface IServer {
    void start();

    void stop();

    void addResource();

    void removeResource();
}
