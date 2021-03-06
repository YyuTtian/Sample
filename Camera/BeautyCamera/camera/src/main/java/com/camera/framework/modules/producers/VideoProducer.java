package com.camera.framework.modules.producers;

import android.os.Handler;
import android.util.Log;

import com.camera.camera.VideoCaptureFrame;
import com.camera.camera.VideoModule;
import com.camera.framework.modules.channels.VideoChannel;

public abstract class VideoProducer implements IVideoProducer {
    private static final String TAG = VideoProducer.class.getSimpleName();

    private VideoChannel videoChannel;
    protected volatile Handler pChannelHandler;

    @Override
    public void connectChannel(int channelId) {
        videoChannel = VideoModule.instance().connectProducer(this, channelId);
        pChannelHandler = videoChannel.getHandler();
    }

    @Override
    public void pushVideoFrame(final VideoCaptureFrame frame) {
        if (pChannelHandler == null) {
            return;
        }

        pChannelHandler.post(() -> {
            try {
                // The capture utilizes the environment OpenGL
                // context for preview texture, so the capture
                // thread and video channel thread use their
                // shared OpenGL context.
                // Thus updateTexImage() is valid here.
                frame.surfaceTexture.updateTexImage();
                if (frame.textureTransform == null) frame.textureTransform = new float[16];
                frame.surfaceTexture.getTransformMatrix(frame.textureTransform);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (videoChannel != null) {
                videoChannel.pushVideoFrame(frame);
            }
        });
    }

    @Override
    public void disconnect() {
        Log.i(TAG, "disconnect");

        if (videoChannel != null) {
            videoChannel.disconnectProducer();
            videoChannel = null;
        }
    }
}
