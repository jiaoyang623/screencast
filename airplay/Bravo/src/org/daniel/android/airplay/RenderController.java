package org.daniel.android.airplay;

import android.text.TextUtils;
import android.util.Log;
import org.daniel.android.airplay.protocol.PlaybackInfoBean;
import org.daniel.android.airplay.protocol.ServerInfoBean;

import javax.jmdns.ServiceInfo;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 19 2015 9:51 AM
 */
public class RenderController implements IRenderController {
    private final ServiceInfo mInfo;

    public RenderController(ServiceInfo info) {
        mInfo = info;
    }

    @Override
    public void play(String url) {

    }

    @Override
    public void rate(float rate) {

    }

    @Override
    public void seek(float position) {

    }

    @Override
    public void stop() {

    }

    @Override
    public ServerInfoBean getServerInfo() {
        return null;
    }

    @Override
    public PlaybackInfoBean getPlayInfo() {
        return null;
    }

    String request(String uri, Map<String, String> paramsGet, Map<String, String> paramsPost) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException("Uri: " + uri);
        }
        StringBuilder content = new StringBuilder();
        if (paramsPost != null && paramsPost.size() > 0) {
            for (String key : paramsPost.keySet()) {
                content.append(key).append(": ").append(paramsPost.get(key)).append("\n");
            }
        }

        uri = parseParamsGet(uri, paramsGet);
        Log.i("jy", "uri: " + uri);
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url: " + uri);
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(15 * 1000);
        conn.setReadTimeout(15 * 1000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Length", "" + content.length());
        conn.setRequestProperty("Content-Type", "text/parameters");
        conn.setRequestProperty("User-Agent", "MediaControl/1.0");
        BufferedOutputStream out = null;
        InputStream is = null;
        StringBuilder result = new StringBuilder();
        try {
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(content.toString().getBytes());
            out.close();
            int status = conn.getResponseCode();
            if (status == 200) {
                byte[] buffer = new byte[256];
                int count = 0;
                is = conn.getInputStream();
                while ((count = is.read(buffer)) != -1) {
                    result.append(new String(buffer, 0, count));
                }
            } else {
                throw new IllegalStateException(String.format("status code [%d]: %s", status, uri));
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return result.toString();
    }

    private String parseParamsGet(String uri, Map<String, String> params) {
        StringBuilder uriBuilder = new StringBuilder(uri);
        if (params != null && params.size() > 0) {
            uriBuilder.append("?");
            int size = params.size();
            int i = 0;
            for (String key : params.keySet()) {
                i++;
                String value = params.get(key);
                if (!TextUtils.isEmpty(value)) {
                    uriBuilder.append(key).append('=').append(URLEncoder.encode(params.get(key)));
                    if (i != size) {
                        uriBuilder.append('&');
                    }
                }
            }
            //去掉最后的&
            uriBuilder.setLength(uriBuilder.length() - 1);
        }
        return uriBuilder.toString();
    }
}
