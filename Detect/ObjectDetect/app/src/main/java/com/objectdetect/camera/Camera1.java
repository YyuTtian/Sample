/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.objectdetect.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.collection.SparseArrayCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@SuppressWarnings("deprecation")
class Camera1 extends CameraViewImpl {

    private static final int INVALID_CAMERA_ID = -1;

    private static final SparseArrayCompat<String> FLASH_MODES = new SparseArrayCompat<>();

    static {
        FLASH_MODES.put(Constants.FLASH_OFF, Camera.Parameters.FLASH_MODE_OFF);
        FLASH_MODES.put(Constants.FLASH_ON, Camera.Parameters.FLASH_MODE_ON);
        FLASH_MODES.put(Constants.FLASH_TORCH, Camera.Parameters.FLASH_MODE_TORCH);
        FLASH_MODES.put(Constants.FLASH_AUTO, Camera.Parameters.FLASH_MODE_AUTO);
        FLASH_MODES.put(Constants.FLASH_RED_EYE, Camera.Parameters.FLASH_MODE_RED_EYE);
    }

    private int mCameraId;

    private final AtomicBoolean isPictureCaptureInProgress = new AtomicBoolean(false);

    Camera mCamera;

    private Camera.Parameters mCameraParameters;

    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    private final List<Size> mPreviewSizes = new ArrayList<>();

    private final List<Size> mPictureSizes = new ArrayList<>();

    private boolean mShowingPreview;

    private boolean mAutoFocus;

    private int mFacing;

    private int mFlash;

    private int mDisplayOrientation;

    Camera1(Callback callback, PreviewImpl preview) {
        super(callback, preview);
        preview.setCallback(new PreviewImpl.Callback() {
            @Override
            public void onSurfaceChanged() {
                if (mCamera != null) {
                    setUpPreview();
                    adjustCameraParameters();
                }
            }

            @Override
            public void onBitmapChange(Bitmap bitmap) {
                callback.onBitmapChange(bitmap);
            }
        });
    }

    Bitmap getBitmap() {
        return mPreview.getBitmap();
    }

    @Override
    boolean start() {
        chooseCamera();
        openCamera();
        if (mPreview.isReady()) {
            setUpPreview();
        }
        mShowingPreview = true;
        mCamera.startPreview();
        return true;
    }

    @Override
    void stop() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        mShowingPreview = false;
        releaseCamera();
    }

    // Suppresses Camera#setPreviewTexture
    @SuppressLint("NewApi")
    void setUpPreview() {
        try {
            if (mPreview.getOutputClass() == SurfaceHolder.class) {
                mCamera.setPreviewDisplay(mPreview.getSurfaceHolder());
            } else {
                mCamera.setPreviewTexture((SurfaceTexture) mPreview.getSurfaceTexture());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    boolean isCameraOpened() {
        return mCamera != null;
    }

    @Override
    void setFacing(int facing) {
        if (mFacing == facing) {
            return;
        }
        mFacing = facing;
        if (isCameraOpened()) {
            stop();
            start();
        }
    }

    @Override
    int getFacing() {
        return mFacing;
    }

//    @Override
//    Set<AspectRatio> getSupportedAspectRatios() {
//        SizeMap idealAspectRatios = mPreviewSizes;
//        for (AspectRatio aspectRatio : idealAspectRatios.ratios()) {
//            if (mPictureSizes.sizes(aspectRatio) == null) {
//                idealAspectRatios.remove(aspectRatio);
//            }
//        }
//        return idealAspectRatios.ratios();
//    }

//    @Override
//    boolean setAspectRatio(AspectRatio ratio) {
//        if (mAspectRatio == null || !isCameraOpened()) {
//            // Handle this later when camera is opened
//            mAspectRatio = ratio;
//            return true;
//        } else if (!mAspectRatio.equals(ratio)) {
//            final Set<Size> sizes = mPreviewSizes.sizes(ratio);
//            if (sizes == null) {
//                throw new UnsupportedOperationException(ratio + " is not supported");
//            } else {
//                mAspectRatio = ratio;
//                adjustCameraParameters();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    AspectRatio getAspectRatio() {
//        return mAspectRatio;
//    }

    @Override
    void setAutoFocus(boolean autoFocus) {
        if (mAutoFocus == autoFocus) {
            return;
        }
        if (setAutoFocusInternal(autoFocus)) {
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    boolean getAutoFocus() {
        if (!isCameraOpened()) {
            return mAutoFocus;
        }
        String focusMode = mCameraParameters.getFocusMode();
        return focusMode != null && focusMode.contains("continuous");
    }


    @Override
    void setZoomPercent(float percent) {
        int value = (int) (mCameraParameters.getMaxZoom() * percent);
        mCameraParameters.setZoom(value);
        mCamera.setParameters(mCameraParameters);
    }

    @Override
    float getZoomPercent() {
        return mCameraParameters.getZoom() * 1.0f / mCameraParameters.getMaxZoom();
    }

    @Override
    void setFlash(int flash) {
        if (flash == mFlash) {
            return;
        }
        if (setFlashInternal(flash)) {
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    int getFlash() {
        return mFlash;
    }

    @Override
    void takePicture() {
        if (!isCameraOpened()) {
            return;
        }
        if (getAutoFocus()) {
            try {
                mCamera.cancelAutoFocus();
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        takePictureInternal();
                    }
                });
            } catch (Throwable throwable) {
                takePictureInternal();
            }

        } else {
            takePictureInternal();
        }
    }

    void takePictureInternal() {
        if (!isPictureCaptureInProgress.getAndSet(true)) {
            mCamera.enableShutterSound(true);
            try {
                mCamera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            isPictureCaptureInProgress.set(false);
                            mCallback.onPictureTaken(data);
                            camera.cancelAutoFocus();
                            camera.startPreview();
                        } catch (Throwable throwable) {

                        }
                    }
                });
            } catch (Throwable throwable) {
            }
        }
    }

    @Override
    void setDisplayOrientation(int displayOrientation) {
        if (mDisplayOrientation == displayOrientation) {
            return;
        }
        mDisplayOrientation = displayOrientation;
        if (isCameraOpened()) {
            mCameraParameters.setRotation(calcCameraRotation(displayOrientation));
            mCamera.setParameters(mCameraParameters);
            mCamera.setDisplayOrientation(calcDisplayOrientation(displayOrientation));
        }
    }

    /**
     * This rewrites {@link #mCameraId} and {@link #mCameraInfo}.
     */
    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == mFacing) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = INVALID_CAMERA_ID;
    }

    private void openCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        mCamera = Camera.open(mCameraId);
        mCameraParameters = mCamera.getParameters();

        // Supported picture sizes;
        mPictureSizes.clear();
        mPreviewSizes.clear();

        boolean picScreenWidth = false;
        boolean preScreenWidth = false;
        boolean allOkWidth = false;

        for (Camera.Size size : mCameraParameters.getSupportedPictureSizes()) {
            if (size.height == Utils.screenWidth(getView().getContext())) {
                picScreenWidth = true;
            }
        }
        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
            if (size.height == Utils.screenWidth(getView().getContext())) {
                preScreenWidth = true;
            }
        }

        allOkWidth = picScreenWidth && preScreenWidth;

        allOkWidth = false;

        for (Camera.Size size : mCameraParameters.getSupportedPictureSizes()) {
            if (allOkWidth) {
                if (size.height == Utils.screenWidth(getView().getContext())) {
                    mPictureSizes.add(new Size(size.width, size.height));
                }
            } else {
                mPictureSizes.add(new Size(size.width, size.height));
            }
        }


        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
            if (allOkWidth) {
                if (size.height == Utils.screenWidth(getView().getContext())) {
                    mPreviewSizes.add(new Size(size.width, size.height));
                }
            } else {
                mPreviewSizes.add(new Size(size.width, size.height));
            }

//            for (int i = 0; i < mPictureSizes.size(); i++) {
//                float picScale = mPictureSizes.get(i).getWidth() * 1f / mPictureSizes.get(i).getHeight();
//                if (scale == picScale) {
//                    mPreviewSizes.add(new Size(size.width, size.height));
//                    break;
//                }
//            }
        }

        adjustCameraParameters();
        mCamera.setDisplayOrientation(calcDisplayOrientation(mDisplayOrientation));
        mCallback.onCameraOpened();
    }

    void adjustCameraParameters() {
        Size size = chooseOptimalSize(mPreviewSizes);
        Size pictureSize;

        if (size == null) {
            size = mPreviewSizes.get(0);
            pictureSize = mPictureSizes.get(0);
        } else {
            pictureSize = choosePicSize(mPictureSizes,
                    size.getWidth() * 1f / size.getHeight());
        }

        if (pictureSize == null) {
            pictureSize = mPictureSizes.get(0);
        }

//        System.out.println("qglog screenWidth=" + Util.screenHeight() + " screenHeight=" + Util.screenWidth() +
//                " screenScale=" + (Util.screenHeight() * 1f / Util.screenWidth()) +
//                " pic size.width=" + pictureSize.getWidth() + " size.height=" + pictureSize.getHeight() + " scale=" + (pictureSize.getWidth() * 1f / pictureSize.getHeight()) +
//                " pre size.width=" + size.getWidth() + " size.height=" + size.getHeight() + " scale=" + (size.getWidth() * 1f / size.getHeight()));


        mCallback.onCameraSizeChange(size);

        if (mShowingPreview) {
            mCamera.stopPreview();
        }
        mCameraParameters.setPreviewSize(size.getWidth(), size.getHeight());
        mCameraParameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        mCameraParameters.setRotation(calcCameraRotation(mDisplayOrientation));
        setAutoFocusInternal(mAutoFocus);
        setFlashInternal(mFlash);
        mCamera.setParameters(mCameraParameters);
        if (mShowingPreview) {
            mCamera.startPreview();
        }
    }


    private Size chooseOptimalSize(List<Size> sizes) {

        Size result = null;

        float screen_width_height = Utils.screenHeight(getView().getContext()) * 1f / Utils.screenWidth(getView().getContext());

//        Collections.sort(sizes, new Comparator<Size>() {
//            @Override
//            public int compare(Size o1, Size o2) {
//                return o1.getWidth() - o2.getWidth(); // 从小到大排序
//            }
//        });

        float min = Float.MAX_VALUE;

        for (Size size : sizes) { // Iterate from small to large
            float size_width_height = size.getWidth() * 1f / size.getHeight();
            if (size_width_height < screen_width_height &&
                    (screen_width_height - size_width_height) < min &&
                    size.getHeight() > 1000) {
                min = screen_width_height - size_width_height;
                result = size;
            }
        }
        return result;
    }


    private Size choosePicSize(List<Size> sizes, float scale) {

        Size result = null;
        int maxWidth = 0;
        for (Size size : sizes) { // Iterate from small to large
            if (size.getWidth() * 1f / size.getHeight() == scale) {
                if (size.getWidth() > maxWidth) {
                    maxWidth = size.getWidth();
                    result = size;
                }
            }
        }


        return result;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mCallback.onCameraClosed();
        }
    }

    /**
     * Calculate display orientation https://developer.android
     * .com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     * <p>
     * This calculation is used for orienting the preview
     * <p>
     * Note: This is not the same calculation as the camera rotation
     *
     * @param screenOrientationDegrees Screen orientation in degrees
     * @return Number of degrees required to rotate preview
     */
    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {  // back-facing
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    /**
     * Calculate camera rotation
     * <p>
     * This calculation is applied to the output JPEG either via Exif Orientation tag or by actually
     * transforming the bitmap. (Determined by vendor camera API implementation)
     * <p>
     * Note: This is not the same calculation as the display orientation
     *
     * @param screenOrientationDegrees Screen orientation in degrees
     * @return Number of degrees to rotate image in order for it to view correctly.
     */
    private int calcCameraRotation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (mCameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (mCameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Constants.LANDSCAPE_90 ||
                orientationDegrees == Constants.LANDSCAPE_270);
    }

    /**
     * @return {@code true} if {@link #mCameraParameters} was modified.
     */
    private boolean setAutoFocusInternal(boolean autoFocus) {
        mAutoFocus = autoFocus;
        if (isCameraOpened()) {
            final List<String> modes = mCameraParameters.getSupportedFocusModes();
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else {
                mCameraParameters.setFocusMode(modes.get(0));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return {@code true} if {@link #mCameraParameters} was modified.
     */
    private boolean setFlashInternal(int flash) {
        if (isCameraOpened()) {
            List<String> modes = mCameraParameters.getSupportedFlashModes();
            String mode = FLASH_MODES.get(flash);
            if (modes != null && modes.contains(mode)) {
                mCameraParameters.setFlashMode(mode);
                mFlash = flash;
                return true;
            }
            String currentMode = FLASH_MODES.get(mFlash);
            if (modes == null || !modes.contains(currentMode)) {
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mFlash = Constants.FLASH_OFF;
                return true;
            }
            return false;
        } else {
            mFlash = flash;
            return false;
        }
    }

}
