package com.candy.simplezhibo.module;

import com.candy.simplezhibo.activity.MainActivity;
import com.candy.simplezhibo.presenter.VideoEncoder;

import dagger.Module;
import dagger.Provides;

@Module
public class VideoEncoderModule {

    private MainActivity mainActivity;

    public VideoEncoderModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    VideoEncoder provideVideoEncoder() {
        return new VideoEncoder(mainActivity);
    }

}