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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void play(String url, float position) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Location", url);
        params.put("Start-Position", String.valueOf(position));
        String result = post(mInfo.getURL() + "/play", null, params);
        Log.i("jy", "play result: " + result);
    }

    /**
     * 协议失效
     */
    @Deprecated
    @Override
    public void rate(float rate) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("rate", String.format("%.7f", rate));
        String result = post(mInfo.getURL() + "/rate", params, null);
        Log.i("jy", "rate result: " + result);
    }

    /**
     * 协议失效
     */
    @Deprecated
    @Override
    public void seek(float position) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("position", String.format("%.7f", position));
        String result = post(mInfo.getURL() + "/scrub", params, null);
        Log.i("jy", "seek result: " + result);
    }

    @Override
    public void stop() throws IOException {
        String result = post(mInfo.getURL() + "/stop", null, null);
        Log.i("jy", "stop result: " + result);
    }

    /**
     * 协议失效
     */
    @Deprecated
    @Override
    public ServerInfoBean getServerInfo() throws IOException {
        String result = get(mInfo.getURL() + "/server-info", null);
        ServerInfoBean bean = ServerInfoBean.parse(PListUtils.parse(result));

        return bean;
    }

    /**
     * 协议失效
     */
    @Deprecated
    @Override
    public PlaybackInfoBean getPlayInfo() throws IOException {
        String result = get(mInfo.getURL() + "/playback-info", null);
        Log.i("jy", "playback-info: " + result);
        PlaybackInfoBean bean = PlaybackInfoBean.parse(PListUtils.parse(result));

        return bean;
    }

    /**
     * 协议失效
     */
    @Deprecated
    @Override
    public float[] getProgress() throws IOException {
        String result = get(mInfo.getURL() + "/scrub", null);

        return getPosition(result);
    }


    public float[] getPosition(String content) {
        float[] values = {0f, 0f};
        if (content == null || content.length() == 0) {
            return values;
        }
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("duration")) {
                values[1] = peekFloat(line);
            } else if (line.contains("position")) {
                values[0] = peekFloat(line);
            }
        }

        return values;
    }

    private final Pattern mFloatPattern = Pattern.compile("-?[\\.\\d]+");

    public float peekFloat(String content) {
        if (content == null || content.length() == 0) {
            return 0;
        }

        Matcher m = mFloatPattern.matcher(content);
        if (m.find()) {
            String num = m.group(0);
            return Float.valueOf(num);
        } else {
            return 0;
        }
    }


    private String get(String uri, Map<String, String> params) throws IOException {
        return request("GET", uri, params, null);
    }

    private String post(String uri, Map<String, String> paramsGet, Map<String, String> paramsPost) throws IOException {
        return request("POST", uri, paramsGet, paramsPost);
    }

    String request(String method, String uri, Map<String, String> paramsGet, Map<String, String> paramsPost) throws IOException {
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
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Length", "" + content.length());
        conn.setRequestProperty("Content-Type", "text/parameters");
        conn.setRequestProperty("X-Apple-AssetKey", UUID.randomUUID().toString());
        conn.setRequestProperty("X-Apple-Session-ID", UUID.randomUUID().toString());
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
                int count;
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
