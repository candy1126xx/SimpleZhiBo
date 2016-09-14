package com.candy.simplezhibo.presenter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.candy.simplezhibo.activity.MainActivity;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.nio.ShortBuffer;

public class AudioEncoder {

    private MainActivity mainActivity;

    private AudioRecord audioRecord;
    private ShortBuffer audioData;
    private Thread audioSampleThread;

    public AudioEncoder(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initAudioRecord();
        initAudioSample();
        renderParams();
    }

    private void initAudioRecord() {
        int bufferSize = AudioRecord.getMinBufferSize(MainActivity.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, MainActivity.AUDIO_CODEC);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, MainActivity.SAMPLE_RATE, MainActivity.AUDIO_CHANNELS_FORMAT, MainActivity.AUDIO_CODEC, bufferSize);
        audioData = ShortBuffer.allocate(bufferSize);
        audioRecord.startRecording();
    }

    private void initAudioSample() {
        audioSampleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mainActivity.isLiving) {
                    int bufferLength = audioRecord.read(audioData.array(), 0, audioData.capacity());
                    if (bufferLength > 0) {
                        try {
                            mainActivity.recorder.recordSamples(audioData);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void renderParams(){
        mainActivity.tvAudioCodec.setText(MainActivity.AUDIO_CODEC_NAME);
        mainActivity.tvAudioChannels.setText(String.valueOf(MainActivity.AUDIO_CHANNELS));
        mainActivity.tvAudioSample.setText(String.valueOf(MainActivity.SAMPLE_RATE)+" Hz");
    }

    public void startLiving(){
        audioSampleThread.start();
    }

}
