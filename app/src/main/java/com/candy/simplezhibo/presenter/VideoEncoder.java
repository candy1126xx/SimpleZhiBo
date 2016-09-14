package com.candy.simplezhibo.presenter;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.View;

import com.candy.simplezhibo.CustomApp;
import com.candy.simplezhibo.R;
import com.candy.simplezhibo.activity.MainActivity;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoEncoder {

    private MainActivity mainActivity;

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Frame dataFrame;

    private SimpleDateFormat format;

    public VideoEncoder(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        format = new SimpleDateFormat("mm:ss", Locale.CHINA);
        initCamera();
        initPreview();
        renderParams();
    }

    public void renderParams(){
        mainActivity.tvVideoCodec.setText(MainActivity.VIDEO_CODEC_NAME);
        mainActivity.tvVideoChannels.setText(String.valueOf(MainActivity.VIDEO_CHANNELS));
        mainActivity.tvVideoFormat.setText(MainActivity.VIDEO_OUTPUT_FORMAT);
        mainActivity.tvSize.setText(String.valueOf(MainActivity.INPUT_WIDTH)+" x "+String.valueOf(MainActivity.INPUT_HEIGHT));
        mainActivity.tvFrameRate.setText(String.valueOf(MainActivity.FRAME_RATE));
        mainActivity.tvTime.setText(format.format(new Date(0)));
    }

    private void initCamera(){
        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(320, 240);
        camera.setParameters(parameters);
        dataFrame = new Frame(MainActivity.INPUT_WIDTH, MainActivity.INPUT_HEIGHT, MainActivity.PIXEL_BYTE, MainActivity.VIDEO_CHANNELS);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                try {
                    if (mainActivity.isLiving) {
                        ((ByteBuffer) dataFrame.image[0].position(0)).put(data);
                        long t = 1000 * (System.currentTimeMillis() - mainActivity.startTime);
                        if (t > mainActivity.recorder.getTimestamp()) {
                            mainActivity.recorder.setTimestamp(t);
                            mainActivity.tvTime.setText(format.format(new Date(t/1000)));
                        }
                        mainActivity.recorder.record(dataFrame);
                    }
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initPreview(){
        surfaceHolder = mainActivity.preView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceHolder.addCallback(null);
                camera.setPreviewCallback(null);
            }
        });
    }

}
