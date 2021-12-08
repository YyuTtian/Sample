package com.camera.framework.modules.consumers;

import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.camera.camera.VideoCaptureFrame;
import com.camera.camera.VideoModule;
import com.camera.framework.helpers.gles.core.EglCore;
import com.camera.framework.helpers.gles.core.GlUtil;
import com.camera.framework.modules.channels.ChannelManager;
import com.camera.framework.modules.channels.VideoChannel;


public abstract class BaseWindowConsumer implements IVideoConsumer {
    static final int CHANNEL_ID = ChannelManager.ChannelID.CAMERA;
    public static boolean DEBUG = false;

    VideoModule videoModule;
    VideoChannel videoChannel;

    private EGLSurface drawingEglSurface;
    volatile boolean needResetSurface = true;
    volatile boolean surfaceDestroyed;
    private float[] mMVPMatrix = new float[16];
    protected boolean mvpInit;

    BaseWindowConsumer(VideoModule videoModule) {
        this.videoModule = videoModule;
    }

    @Override
    public void connectChannel(int channelId) {
        videoChannel = videoModule.connectConsumer(this, channelId, IVideoConsumer.TYPE_ON_SCREEN);
    }

    @Override
    public void disconnectChannel(int channelId) {
        videoModule.disconnectConsumer(this, channelId);
    }

    @Override
    public void onConsumeFrame(VideoCaptureFrame frame, VideoChannel.ChannelContext context) {
        drawFrame(frame, context);
    }

    private void drawFrame(VideoCaptureFrame frame, VideoChannel.ChannelContext context) {
        if (surfaceDestroyed) {
            return;
        }

        EglCore eglCore = context.getEglCore();
        if (needResetSurface) {
            if (drawingEglSurface != null && drawingEglSurface != EGL14.EGL_NO_SURFACE) {
                eglCore.releaseSurface(drawingEglSurface);
                eglCore.makeNothingCurrent();
                drawingEglSurface = null;
            }

            Object surface = onGetDrawingTarget();
            if (surface != null) {
                try {
                    drawingEglSurface = eglCore.createWindowSurface(onGetDrawingTarget());
                    needResetSurface = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (drawingEglSurface != null && !eglCore.isCurrent(drawingEglSurface)) {
            try {
                eglCore.makeCurrent(drawingEglSurface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int surfaceWidth = onMeasuredWidth();
        int surfaceHeight = onMeasuredHeight();
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);

        if (!mvpInit) {
            mMVPMatrix = GlUtil.changeMVPMatrix(
                    GlUtil.IDENTITY_MATRIX,
                    surfaceWidth, surfaceHeight,
                    frame.format.getWidth(),
                    frame.format.getHeight());
            mvpInit = true;
        }

        if (frame.format.getTexFormat() == GLES20.GL_TEXTURE_2D) {
            context.getProgram2D().drawFrame(
                    frame.textureId, frame.textureTransform, mMVPMatrix);
        } else if (frame.format.getTexFormat() == GLES11Ext.GL_TEXTURE_EXTERNAL_OES) {
            context.getProgramOES().drawFrame(
                    frame.textureId, frame.textureTransform, mMVPMatrix);
        }

        if (drawingEglSurface != null) {
            eglCore.swapBuffers(drawingEglSurface);
        }
    }
}
