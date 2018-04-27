package cn.qssq666.videoplayerdemo.fullscreen1;

import java.util.Random;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class PlayConfig {


    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    boolean autoPlay=new Random().nextInt(2)==1?true:false;
}
