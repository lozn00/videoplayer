#### 使用方法

列表播放则添加依赖如下
```groovy
compile 'cn.qssq666:video-player-manager:0.1'

```

只需要处理缩放问题使用系统播放器省空间则如下
```groovy
compile 'cn.qssq666:videoplayer:0.1'
```
#### videoplayer
videoplayer 模块是纯粹为了解锁缩放模式问题和第一帧图片和实现无黑白屏图片模式吻合播放所写的项目


本项目模块 video-player-manager 是基于com.danylo.volokh.video_player_manager的项目进行修改
解决了许许多多的问题,包括播放刚开始白屏问题,包括无法暂停继续等等问题.
有一定概率的死锁问题暂时没有找到方法，目前我发生过一次anr异常, 这个项目大量用到消息队列和线程锁、 学习有一定难度，请慎重，坑死不偿命。

#### video-player-manager
如果要做列表播放器，请研究video-player-manager项目
如果只是解决缩放问题，请使用videoplayer模块，

本项目中的模块，我会陆续上传到jcenter，f方便大家直接下载使用！






#### other

本文档编写于 2018-4-20 21:27:31

#### 附加信息
2018-4-26 23:54:23
为了防止维护声明周期带来的麻烦问题,增加错乱声明周期可不崩溃处理,Config.ENABLE_THROW_ERR可防止错误