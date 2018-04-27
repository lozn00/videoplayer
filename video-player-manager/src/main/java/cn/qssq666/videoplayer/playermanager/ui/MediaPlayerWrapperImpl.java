package cn.qssq666.videoplayer.playermanager.ui;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerWrapperImpl extends MediaPlayerWrapper{

    public MediaPlayerWrapperImpl(Context context) {
        super(context,new MediaPlayer());
    }


}
