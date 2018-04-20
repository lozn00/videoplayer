package cn.qssq666.videoplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.qssq666.videoplayer.util.MeasureUtil;

/**
 * Created by qssq on 2018/4/18 qssq666@foxmail.com
 */
public class VideoImageView extends ImageView {


    private static final String TAG_ = "VideoCorver";
    private int videoWidth;//width
    private int videoHeight;
    private int displayAspectRatio;
    private boolean mInit;

    public VideoImageView(Context context) {
        super(context);
    }

    public VideoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
//        setVisibility(INVISIBLE);
    }

    protected void init(Context context) {

        this.videoHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.videoWidth = context.getResources().getDisplayMetrics().widthPixels;
        ImageView imageView = new ImageView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mInit) {
            MeasureUtil.Size measure = MeasureUtil.measure(displayAspectRatio, widthMeasureSpec, heightMeasureSpec, videoWidth, videoHeight);
            setMeasuredDimension(measure.width, measure.height);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mInit) {
            mInit = true;
            videoWidth = getDrawable().getIntrinsicWidth();
            videoHeight = getDrawable().getIntrinsicHeight();
            requestLayout();

        }
    }

    /*   protected void onMeasure(int var1, int var2) {
//        com.pili.pldroid.player.common.a.a var3 = com.pili.pldroid.player.common.a.a(SystemVideoView.this.getDisplayAspectRatio(), var1, var2, this.videoWidth, this.videoHeight);
//        this.setMeasuredDimension(var3.a, var3.b);
        int var7 = MeasureSpec.getMode(var1);
        int var8 = MeasureSpec.getSize(var1);
        int var9 = MeasureSpec.getMode(var2);
    }*/


    @Override
    public void setImageDrawable( Drawable drawable) {
        mInit = false;
        super.setImageDrawable(drawable);

    }

    public void setDisplayAspectRatio(int var1) {
        displayAspectRatio = var1;
        this.requestLayout();

    }


/*    @Override
    public boolean isPlaying() {
        return false;
    }

    public int getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    public void setCorver(int resource) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resource, opts);

    }*/
}