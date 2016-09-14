package com.candy.simplezhibo.activity;

import android.app.Activity;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.simplezhibo.CustomApp;
import com.candy.simplezhibo.R;
import com.candy.simplezhibo.component.DaggerMainActivityComponent;
import com.candy.simplezhibo.module.AudioEncoderModule;
import com.candy.simplezhibo.module.VideoEncoderModule;
import com.candy.simplezhibo.presenter.AudioEncoder;
import com.candy.simplezhibo.presenter.VideoEncoder;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @BindView(R.id.camera_view)
    public SurfaceView preView;

    @BindView(R.id.btn_record)
    public ImageView btnRecord;

    @BindView(R.id.tv_server_url)
    public TextView tvServerUrl;

    @BindView(R.id.tv_status)
    public TextView tvStatus;

    @BindView(R.id.tv_video_codec)
    public TextView tvVideoCodec;

    @BindView(R.id.tv_video_channels)
    public TextView tvVideoChannels;

    @BindView(R.id.tv_video_format)
    public TextView tvVideoFormat;

    @BindView(R.id.tv_size)
    public TextView tvSize;

    @BindView(R.id.tv_frame_rate)
    public TextView tvFrameRate;

    @BindView(R.id.tv_time)
    public TextView tvTime;

    @BindView(R.id.tv_audio_codec)
    public TextView tvAudioCodec;

    @BindView(R.id.tv_audio_channels)
    public TextView tvAudioChannels;

    @BindView(R.id.tv_audio_sample)
    public TextView tvAudioSample;

    @Inject
    VideoEncoder videoEncoder;
    @Inject
    AudioEncoder audioEncoder;

    public FFmpegFrameRecorder recorder;
    public boolean isLiving;
    public long startTime;

    public static final String SERVER_IP = "rtmp://192.168.1.15:1935/live/livestream";

    public static final String VIDEO_CODEC_NAME = "H264";
    public static final int VIDEO_CODEC = avcodec.AV_CODEC_ID_H264;
    public static final int FRAME_RATE = 30;
    public static final int INPUT_WIDTH = 320;
    public static final int INPUT_HEIGHT = 240;
    public static final int VIDEO_CHANNELS = 2;
    public static final String VIDEO_OUTPUT_FORMAT = "flv";
    public static final int PIXEL_BYTE = Frame.DEPTH_UBYTE;

    public static final String AUDIO_CODEC_NAME = "PCM_16BIT";
    public static final int AUDIO_CODEC = AudioFormat.ENCODING_PCM_16BIT;
    public static final int AUDIO_CHANNELS_FORMAT = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_CHANNELS = 1;
    public static final int SAMPLE_RATE = 44100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DaggerMainActivityComponent.builder()
                .videoEncoderModule(new VideoEncoderModule(this))
                .audioEncoderModule(new AudioEncoderModule(this))
                .build()
                .inject(this);

        tvServerUrl.setText(SERVER_IP);
        tvStatus.setText("停止");
        initRecorder();
        initBtnRecord();
    }

    public void initBtnRecord(){
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiving){
                    isLiving = false;
                    btnRecord.setImageResource(R.drawable.btn_record);
                    tvStatus.setText("停止");
                }else {
                    isLiving = true;
                    startTime = System.currentTimeMillis();
                    audioEncoder.startLiving();
                    btnRecord.setImageResource(R.drawable.btn_stop);
                    tvStatus.setText("正在直播");
                }
            }
        });
    }

    public void initRecorder(){
        try {
            if (recorder == null) {
                recorder = new FFmpegFrameRecorder(SERVER_IP, INPUT_WIDTH, INPUT_HEIGHT, AUDIO_CHANNELS);
                recorder.setVideoCodecName(VIDEO_CODEC_NAME);
                recorder.setVideoCodec(VIDEO_CODEC);
                recorder.setFrameRate(FRAME_RATE);
                recorder.setFormat(VIDEO_OUTPUT_FORMAT);
                recorder.setSampleRate(SAMPLE_RATE);
                recorder.start();
            }
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

}
