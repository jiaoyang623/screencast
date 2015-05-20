/**
 * Copyright (c) 2013 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.daniel.android.miracast;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.daniel.android.miracast.widgets.VideoSurface;

import java.io.IOException;

public class SamplePresentationFragment extends PresentationFragment {
    private static final String ARG_URL = "url";
//    VideoView mVideoView;

    public static SamplePresentationFragment newInstance(
            Context context, Display display, String url) {
        SamplePresentationFragment frag = new SamplePresentationFragment();

        frag.setDisplay(context, display);

        Bundle bundle = new Bundle();

        bundle.putString(ARG_URL, url);
        frag.setArguments(bundle);

        return (frag);
    }

    public void start(String path) {
        try {
            mVideoSurface.start(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mVideoSurface.stop();
    }

    private VideoSurface mVideoSurface;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mVideoSurface = new VideoSurface(getActivity());
        return mVideoSurface;
    }
}
