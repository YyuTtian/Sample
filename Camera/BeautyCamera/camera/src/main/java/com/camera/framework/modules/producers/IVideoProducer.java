package com.camera.framework.modules.producers;


import com.camera.camera.VideoCaptureFrame;

public interface IVideoProducer {
    void connectChannel(int channelId);
    void pushVideoFrame(VideoCaptureFrame frame);
    void disconnect();
}
