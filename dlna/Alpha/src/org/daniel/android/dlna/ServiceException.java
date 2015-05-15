package org.daniel.android.dlna;

/**
 * 服务出错
 * Created by jiaoyang on 5/15/15.
 */
public class ServiceException extends Exception {
    public ServiceException() {
        super();
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
