package cn.qssq666.videoplayer.playermanager.ui;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import cn.qssq666.videoplayer.playermanager.Config;
import cn.qssq666.videoplayer.playermanager.utils.Logger;

/**
 * This class encapsulates {@link MediaPlayer}
 * and follows this use-case diagram:
 * <p>
 * http://developer.android.com/reference/android/media/MediaPlayer.html
 */
public abstract class MediaPlayerWrapper


        implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener {

    public static final int ERROR_CODE_SET_SURFACE_FAIL = 9500;
    private Context context;
    private String TAG;
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    public static final int POSITION_UPDATE_NOTIFYING_PERIOD = 1000;         // milliseconds
    private ScheduledFuture<?> mFuture;
    private Surface mSurface;

    public void setVideoUUID(String mVideo) {
        this.mVideoUUID = mVideo;
    }

    private String mVideoUUID = "";
    private boolean enterTempPauseMode;


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mListener != null) {
            mListener.onPrepared(mp);
        }
    }

    public boolean isCurrent(String video) {
        if (mVideoUUID == null) {
            return false;
        }
        if (video.equals(mVideoUUID)) {

            return true;
        }
        return false;
    }

    public enum State {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        PLAYBACK_COMPLETED,
        END,
        ERROR
    }

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private final MediaPlayer mMediaPlayer;
    private final AtomicReference<State> mState = new AtomicReference<>();//https://www.cnblogs.com/charlesblc/p/5994162.html 这里是重量级所

    private MainThreadMediaPlayerListener mListener;
    private VideoStateListener mVideoStateListener;

    private ScheduledExecutorService mPositionUpdateNotifier = Executors.newScheduledThreadPool(1);

    protected MediaPlayerWrapper(Context context, MediaPlayer mediaPlayer) {
        TAG = "" + this;
        if (SHOW_LOGS) Logger.v(TAG, "constructor of MediaPlayerWrapper");
        if (SHOW_LOGS)
            Logger.v(TAG, "constructor of MediaPlayerWrapper, main Looper " + Looper.getMainLooper());
        if (SHOW_LOGS)
            Logger.v(TAG, "constructor of MediaPlayerWrapper, my Looper " + Looper.myLooper());

        if (Looper.myLooper() != null) {

            if (!Config.ENABLE_THROW_ERR) {
                notifyErrorCallBack("my looper not null ,a bug in some mediaplayer implementation cause that listeners are not called at all please use a threa without looper ");

            } else {
                throw new RuntimeException("myLooper not null, a bug in some MediaPlayer implementation cause that listeners are not called at all. Please use a thread without Looper");

            }
        }
        this.context = context;
        mMediaPlayer = mediaPlayer;

        mState.set(State.IDLE);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);


    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {

                    if (SHOW_LOGS)
                        Logger.v(TAG, "onAudioFocusChange focusChange " + focusChange);


                    if (true) {
                        return;
                    }

                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            ;//PauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            ;//PauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS):
                            doTempPauseLogic();
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            doTempContinueLogic();
                            break;

                        default:
                            break;

                    }
                }
            };


    private void doTempContinueLogic() {
        if (isPlayCheck()) {//如果是暂停的 而且是临时暂停模式

            start();
            //enterTempPauseMode 进入了这个模式怎么退出呢？只能用户操作的时候退出了，
        }
    }

    public boolean isPlayCheck() {
        return getCurrentState() == State.STARTED || getCurrentState() == State.PREPARED;
    }

    public boolean isPauseCheck() {
        return getCurrentState() == State.PAUSED;
    }

    private void doTempPauseLogic() {
        if (isPlayCheck()) {
            enterTempPauseMode = true;
            pause();
        } else {//如果你手动暂停了 且已经 进入了临时模式 我不管， 如果你在播放 ，那么照样走上面的逻辑。只要你暂停了 ，我就会取消临时模式 这样就保证不会再别的app暂停的情况下又收到的开始的。然后本来暂停的音乐又开始播放了。
            if (enterTempPauseMode) {
                enterTempPauseMode = false;
            }
        }
    }

    private final Runnable mOnVideoPreparedMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPreparedMainThread");
            mListener.onVideoPreparedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPreparedMainThread");
        }
    };


    /**
     * 在调用prepare之前先回调一个东西，
     */
    private final Runnable mOnVideoPrepareMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, 即将调用onPrepare");
            mListener.onPrepare();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, 即将调用onPrepare");
        }
    };


    public void prepare() {


        if (SHOW_LOGS) Logger.v(TAG, ">> prepare, mState " + mState);

        synchronized (mState) {
            switch (mState.get()) {
                case STOPPED:
                case INITIALIZED:
                    try {

                        if (mListener != null) {
                            mMainThreadHandler.post(mOnVideoPrepareMessage);
                        }

                        requestAudioFocus(context);
                        mMediaPlayer.prepare();

                        mState.set(State.PREPARED);

                        if (mListener != null) {
                            mMainThreadHandler.post(mOnVideoPreparedMessage);
                        }

                    } catch (IllegalStateException ex) {
                        /** we should not call {@link MediaPlayerWrapper#prepare()} in wrong state so we fall here*/
                        if (!Config.ENABLE_THROW_ERR) {
                            notifyErrorCallBack("prepare fail state " + mState + "," + ex.toString());
                            onPrepareError(new IOException("prepare fail state " + ex.toString()));

                            return;
                        } else {

                            throw new RuntimeException(ex);
                        }

                    } catch (IOException ex) {
                        onPrepareError(ex);
                    }
                    break;
                case IDLE:
                case PREPARING:
                case PREPARED:

                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("prepare called from illegal state " + mState);

                    } else {
                        throw new IllegalStateException("prepare, called from illegal state " + mState);

                    }
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< prepare, mState " + mState);
    }


    public boolean requestAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(focusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }


    public void abandonAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(focusChangeListener);
    }


/*    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {

            if (mListener != null) {
                try {
                    if (mMediaPlayer == null || !mMediaPlayer.isPlaying() || mMediaPlayer.getDuration() == 0) {
                        return;
                    }

                } catch (IllegalStateException e) {
                    Log.e(TAG, "fetch progress fail");
                    return;
                }
                long currentPosition = mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration() * 100l;
                mListener.onProgressUpdate((int) currentPosition);
                if (SHOW_LOGS) Logger.v(TAG, "<< run, onProgressUpdate " + currentPosition);
                mMainThreadHandler.postDelayed(this, 1000);
            }
        }
    };*/


    /**
     * This method propagates error when {@link IOException} is thrown during synchronous {@link #prepare()}
     *
     * @param ex
     */
    private void onPrepareError(IOException ex) {
        if (SHOW_LOGS) Logger.err(TAG, "catch IO exception [" + ex + "]");
        // might happen because of lost internet connection
//      TODO: if (SHOW_LOGS) Logger.err(TAG, "catch exception, is Network Connected [" + Utils.isNetworkConnected());
        mState.set(State.ERROR);
 /*       if (mListener != null) {//提示在主线程
            mListener.onErrorMainThread(1, -1004); //TODO: remove magic numbers. Find a way to get actual error
        }*/
        if (mListener != null) {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPreparedMainThread");
                    mListener.onErrorMainThread(1, -1004); //TODO: remove magic numbers. Find a way to get actual error
                    if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPreparedMainThread");
                }
            });
        }
    }


    /**
     * @see MediaPlayer#setDataSource(Context, Uri)
     */
    public void setDataSource(String filePath) throws IOException {
        synchronized (mState) {
            if (SHOW_LOGS)
                Logger.v(TAG, "setDataSource, filePath " + filePath + ", mState " + mState);

            switch (mState.get()) {
                case IDLE:
                    mVideoUUID = filePath;

                    try {

                        mMediaPlayer.setDataSource(filePath);

                    } catch (Exception e) {

                        if (!Config.ENABLE_THROW_ERR) {
                            notifyErrorCallBack("ssetDataSource called  " + mState + "," + e.toString());

                        } else {
                            throw new IllegalStateException("setDataSource called  " + mState + "," + e.toString());
                        }

                    }
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("setDataSource called in state " + mState);

                    } else {
                        throw new IllegalStateException("setDataSource called in state " + mState);
                    }
            }
        }
    }

    public void notifyErrorCallBack(String s) {

        if (SHOW_LOGS) {
            Logger.err(TAG, s, new Throwable());
        }

    }

    /**
     * @see MediaPlayer#setDataSource(FileDescriptor fd, long offset, long length)
     */
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) throws IOException {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:

                    mVideoUUID = assetFileDescriptor.getFileDescriptor().toString();
                    mMediaPlayer.setDataSource(
                            assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(),
                            assetFileDescriptor.getLength());
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("setDataSource called in state " + mState);

                    } else {
                        throw new IllegalStateException("setDataSource called in state " + mState);

                    }
            }
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, final int width, final int height) {
        if (SHOW_LOGS) Logger.v(TAG, "onVideoSizeChanged, width " + width + ", height " + height);
        if (!inUiThread()) {
            if (mListener != null) {
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onVideoSizeChangedMainThread(width, height);
                    }
                });
            }

//            throw new RuntimeException("this should be called in Main Thread");
        } else {

            if (mListener != null) {
                mListener.onVideoSizeChangedMainThread(width, height);
            }

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (SHOW_LOGS) Logger.v(TAG, "onVideoCompletion, mState " + mState);

        synchronized (mState) {
            mState.set(State.PLAYBACK_COMPLETED);
        }

        if (mListener != null) {
            mListener.onVideoCompletionMainThread();
        }
    }

    public interface CallBack {
        void onCallBack();
    }

    public void postEvent(Runnable runnable) {


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, what " + what + ", extra " + extra);


        synchronized (mState) {//TODO 死机无响应了.
            mState.set(State.ERROR);
        }

        doOnErrorLogic(what, extra);
        // We always return true, because after Error player stays in this state.
        // See here http://developer.android.com/reference/android/media/MediaPlayer.html
        return true;
    }

    private void doOnErrorLogic(int what, int extra) {
        if (positionUpdaterIsWorking()) {
            stopPositionUpdateNotifier();
        }
        if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, mListener " + mListener);

        if (mListener != null) {
            mListener.onErrorMainThread(what, extra);
        }
    }

    private boolean positionUpdaterIsWorking() {
        return mFuture != null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mListener != null) {
            mListener.onBufferingUpdateMainThread(percent);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (SHOW_LOGS) Logger.v(TAG, "onInfo");
        printInfo(what);

        if (mListener != null) {
            mListener.onInfo(mp, what, extra);
        }
        return false;


    }

    private void printInfo(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://视频帧开始

                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_END");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_SUBTITLE_TIMED_OUT");
                break;
        }
    }

    /**
     * Listener trigger 'onVideoPreparedMainThread' and `onVideoCompletionMainThread` events
     */
    public void setMainThreadMediaPlayerListener(MainThreadMediaPlayerListener listener) {
        mListener = listener;
    }

    public void setVideoStateListener(VideoStateListener listener) {
        mVideoStateListener = listener;
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     * <p/>
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void start() {
        if (SHOW_LOGS) Logger.v(TAG, ">> start");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "start, mState " + mState);

            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case STARTED:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("start called from illegal state called in state " + mState);

                        break;
                    } else {
                        throw new IllegalStateException("start, called from illegal state " + mState);

                    }

/*
                    requestAudioFocus(context);
                    mMediaPlayer.start();*/
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PAUSED:

                    if (SHOW_LOGS)
                        Logger.v(TAG, "start, video is " + mState + ", starting playback.");
                    requestAudioFocus(context);
                    mMediaPlayer.start();
                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoStartMessage);
                    }
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);

                    break;
                case ERROR:
                case END:

                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("start,called from illegal state " + mState);

                    } else {
                        throw new IllegalStateException("start, called from illegal state " + mState);

                    }

            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< start");
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (SHOW_LOGS) Logger.v(TAG, ">> pause");

        synchronized (mState) {
            if (SHOW_LOGS)
                Logger.v(TAG, "pause, mState " + mState);

            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case PREPARED:
                case END:
                    Log.e(TAG, "pause FAIL not support state " + mState);
                    break;

                case STARTED:
                    abandonAudioFocus(context);
                    mMediaPlayer.pause();
                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoPauseMessage);
                    }
                    mState.set(State.PAUSED);
                    break;
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< pause");
    }

    private final Runnable mOnVideoStopMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoStoppedMainThread");
            mListener.onVideoStoppedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoStoppedMainThread");
        }
    };


    private final Runnable mOnVideoPauseMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPausedMainThread");
            mListener.onVideoPausedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPausedMainThread");
        }
    };

    private final Runnable mOnVideoStartMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoStartedMainThread");
            mListener.onVideoStartedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoStartedMainThread");
        }
    };

    public void stop() {
        if (SHOW_LOGS) Logger.v(TAG, ">> stop");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "stop, mState " + mState);

            switch (mState.get()) {

                case STARTED:
                case PAUSED:
                    stopPositionUpdateNotifier();
                    // should stop only if paused or started
                    // FALL-THROUGH
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING: // This is evaluation of http://developer.android.com/reference/android/media/MediaPlayer.html. Canot stop when preparing

                    if (SHOW_LOGS) Logger.v(TAG, ">> stop");
                    abandonAudioFocus(context);
                    mMediaPlayer.stop();

                    if (SHOW_LOGS) Logger.v(TAG, "<< stop");

                    mState.set(State.STOPPED);

                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoStopMessage);
                    }
                    break;
                case STOPPED:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("sstop, already stopped state " + mState);

                        return;
                    } else {

                        throw new IllegalStateException("stop, already stopped");
                    }

                case IDLE:
                case INITIALIZED:
                case END:
                case ERROR:
                    Log.w(TAG, "cannot stop. Player in mState " + mState);
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< stop");
    }

    public void reset() {
        if (SHOW_LOGS) Logger.v(TAG, ">> reset , mState " + mState);

        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                    mMediaPlayer.reset();
                    mState.set(State.IDLE);
                    break;
                case PREPARING:
                case END:
                    if (!Config.ENABLE_THROW_ERR) {
                        notifyErrorCallBack("canot call reset from  state " + mState.get());

                    } else {

                        throw new IllegalStateException("cannot call reset from state " + mState.get());
                    }
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< reset , mState " + mState);
    }

    public void release() {
        if (SHOW_LOGS) Logger.v(TAG, ">> release, mState " + mState);
        synchronized (mState) {
            abandonAudioFocus(context);
            mMediaPlayer.release();

            mState.set(State.END);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< release, mState " + mState);
    }

    public void clearAll() {
        if (SHOW_LOGS) Logger.v(TAG, ">> clearAll, mState " + mState);

        synchronized (mState) {
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer.setOnPreparedListener(null);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< clearAll, mState " + mState);
    }


    public void setLooping(boolean looping) {
        if (SHOW_LOGS) Logger.v(TAG, "setLooping " + looping);
        mMediaPlayer.setLooping(looping);
    }


    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (SHOW_LOGS) Logger.v(TAG, ">> setSurfaceTexture " + surfaceTexture);
        if (SHOW_LOGS) Logger.v(TAG, "setSurfaceTexture mSurface " + mSurface);


        if (surfaceTexture != null) {
            mSurface = new Surface(surfaceTexture);
            try {
                mMediaPlayer.setSurface(mSurface); // TODO fix illegal state exception

            } catch (IllegalStateException e) {
                if (SHOW_LOGS)
                    Logger.v(TAG, "MediaPlayer attch surface fail  " + Log.getStackTraceString(e));

                mState.set(State.ERROR);
                doOnErrorLogic(0, ERROR_CODE_SET_SURFACE_FAIL);//死锁了.
            }
        } else {
            mMediaPlayer.setSurface(null);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< setSurfaceTexture " + surfaceTexture);

    }

    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isReadyForPlayback() {
        boolean isReadyForPlayback = false;
        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "isReadyForPlayback, mState " + mState);
            State state = mState.get();

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case END:
                    isReadyForPlayback = false;
                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    isReadyForPlayback = true;
                    break;
            }

        }
        return isReadyForPlayback;
    }

    public int getDuration() {
        int duration = 0;
        synchronized (mState) {
            switch (mState.get()) {
                case END:
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case ERROR:
                    duration = 0;

                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                    duration = mMediaPlayer.getDuration();
            }
        }
        return duration;
    }

    public void seekToPercent(int percent) {
        synchronized (mState) {
            State state = mState.get();

            if (SHOW_LOGS) Logger.v(TAG, "seekToPercent, percent " + percent + ", mState " + state);

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case END:
                case STOPPED:
                    if (SHOW_LOGS) Logger.w(TAG, "seekToPercent, illegal state");
                    break;

                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    int positionMillis = (int) ((float) percent / 100f * getDuration());
                    mMediaPlayer.seekTo(positionMillis);
                    notifyPositionUpdated();
                    break;
            }
        }
    }

    private final Runnable mNotifyPositionUpdateRunnable = new Runnable() {
        @Override
        public void run() {

            notifyPositionUpdated();
        }
    };

    private void startPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Logger.v(TAG, "startPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);
        mFuture = mPositionUpdateNotifier.scheduleAtFixedRate(
                mNotifyPositionUpdateRunnable,
                0,
                POSITION_UPDATE_NOTIFYING_PERIOD,
                TimeUnit.MILLISECONDS);
    }

    private void stopPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Logger.v(TAG, "stopPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);

        mFuture.cancel(true);
        mFuture = null;
    }

    private void notifyPositionUpdated() {
        synchronized (mState) { //todo: remove
//            if (SHOW_LOGS) Logger.v(TAG, "notifyPositionUpdated, mVideoStateListener " + mVideoStateListener);

            if (mVideoStateListener != null && mState.get() == State.STARTED) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                Log.w(TAG, "progress update:" + currentPosition + ",total process:" + mMediaPlayer.getDuration());
                mVideoStateListener.onVideoPlayTimeChanged(currentPosition);
            }
        }
    }

    public State getCurrentState() {
        synchronized (mState) {
            return mState.get();
        }
    }

    public static int positionToPercent(int progressMillis, int durationMillis) {
        float percentPrecise = (float) progressMillis / (float) durationMillis * 100f;
        return Math.round(percentPrecise);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode();
    }

    public interface MainThreadMediaPlayerListener {
        void onVideoSizeChangedMainThread(int width, int height);

        void onVideoPreparedMainThread();

        /**
         * 请使用VideoStateListener.onVideoPlayTimeChanged
         * onVideoPlayTimeChanged
         *
         * @{ VideoStateListener#onVideoPlayTimeChanged}
         */
        @Deprecated
        void onProgressUpdate(int percent);

        void onVideoCompletionMainThread();

        void onErrorMainThread(int what, int extra);

        void onBufferingUpdateMainThread(int percent);

        void onVideoStoppedMainThread();

        void onPrepared(MediaPlayer mp);

        void onPrepare();

        void onVideoPausedMainThread();

        void onVideoStartedMainThread();

        void onInfo(MediaPlayer mp, int what, int extra);
    }

    public interface VideoStateListener {
        /**
         * 更新貌似不靠谱
         *
         * @param positionInMilliseconds
         */
        void onVideoPlayTimeChanged(int positionInMilliseconds);

    }

    private boolean inUiThread() {
        return Thread.currentThread().getId() == 1;
    }
}
