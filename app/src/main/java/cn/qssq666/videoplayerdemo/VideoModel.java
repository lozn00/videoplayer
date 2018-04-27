package cn.qssq666.videoplayerdemo;

import cn.qssq666.videoplayer.playermanager.meta.MetaData;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class VideoModel implements MetaData {

    public String getImage() {
        return image;
    }

    public VideoModel setImage(String image) {
        this.image = image;
        return this;
    }

    public String getPath() {
        return path;
    }

    public VideoModel setPath(String path) {
        this.path = path;
        return this;
    }

    public String getName() {
        return name;
    }

    public VideoModel setName(String name) {
        this.name = name;
        return this;
    }

    String image;
    String path;

    String name;

}


