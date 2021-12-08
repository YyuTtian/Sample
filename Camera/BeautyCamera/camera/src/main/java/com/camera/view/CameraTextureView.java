package com.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.camera.framework.modules.consumers.TextureViewConsumer;

public class CameraTextureView extends TextureView {
    public CameraTextureView(Context context) {
        super(context);
        setTextureViewConsumer();
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextureViewConsumer();
    }

    private void setTextureViewConsumer() {
        setSurfaceTextureListener(new TextureViewConsumer());
    }
}
