package com.camera;

import android.content.Context;
import android.util.Log;

import com.camera.camera.VideoCaptureFrame;
import com.camera.framework.modules.channels.VideoChannel;
import com.camera.framework.modules.processors.IPreprocessor;
import sdk.android.innshortvideo.innimageprocess.filter.engine.EngineFilter;

/**
 * QTT美颜
 */
public class QTTBeautyProcessor implements IPreprocessor {
    private boolean mEnabled = true; //是否启用美颜
    private Context mContext;
    private EngineFilter mEngine;


    public enum InnoEngineEffectType {
        InnoEngineEffect_SMOOTH(0),
        InnoEngineEffect_TWINKLE(1),
        InnoEngineEffect_GHOSTING(2),
        InnoEngineEffect_SHARPEN(3),
        InnoEngineEffect_BOKEN(4),
        InnoEngineEffect_SKYBOX(5),
        InnoEngineEffect_LUT(6);

        public final int id;

        InnoEngineEffectType(int id) {
            this.id = id;
        }
    }

    public QTTBeautyProcessor(Context context) {
        mContext = context;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame outFrame, VideoChannel.ChannelContext context) {
        try {
            if (!mEnabled) {
                return outFrame;
            }
            if (mEngine != null) {
                mEngine.addReferenceTexture(outFrame.image, 0, outFrame.textureId, outFrame.format.getWidth(), outFrame.format.getHeight(), 0, 0);
                outFrame.textureId = mEngine.runRenderer();
            }
        } catch (Throwable throwable) {

        }
        return outFrame;

    }

    @Override
    public void initPreprocessor() {
        if (!mEnabled) {
            return;
        }

        try {
            mEngine = new EngineFilter();
            mEngine.ndkInit(mContext);
            mEngine.initialize();
            mEngine.addEffectType(InnoEngineEffectType.InnoEngineEffect_SMOOTH.id);//打开磨皮功能
            mEngine.addEffectType(InnoEngineEffectType.InnoEngineEffect_LUT.id);//打开美白滤镜
            float blurValue = 0.5f;//0.7f;
            mEngine.setSmoothDegree(blurValue);
            float whitenValue = 0.8f;
            mEngine.setWhiteStrength(whitenValue);

            mEngine.addEffectType(EngineFilter.InnoEngineEffectType.InnoEngineEffect_EYESBIG.id);//打开大眼功能
            mEngine.addEffectType(EngineFilter.InnoEngineEffectType.InnoEngineEffect_FACETHIN.id);//打开瘦脸滤镜
            float eyesBigValue = 0.7f; //大眼
            float faceThinValue = 0.5f; //瘦脸
            mEngine.addCtrlParam(EngineFilter.InnoEngineEffectType.InnoEngineEffect_EYESBIG.id, eyesBigValue);
            mEngine.addCtrlParam(EngineFilter.InnoEngineEffectType.InnoEngineEffect_FACETHIN.id, faceThinValue);
        } catch (Throwable throwable) {

        }
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext context) {
        //TODO: 销毁美颜库
        try {
            if (mEngine != null) {
                mEngine.finalize();
            }
        } catch (Throwable e) {
            Log.e("releasePreprocessor err", e.getMessage());
        }
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void setBlurValue(float blur) {
        try {
            // 磨皮
            mEngine.setSmoothDegree(blur);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public void setWhitenValue(float whiten) {
        try {
            // 美白
            mEngine.setWhiteStrength(whiten);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public void setCheekValue(float cheek) {
        // 瘦脸
    }

    @Override
    public void setEyeValue(float eye) {
        // 大眼
    }
}
