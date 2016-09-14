package com.candy.simplezhibo.module;

import com.candy.simplezhibo.activity.MainActivity;
import com.candy.simplezhibo.presenter.AudioEncoder;

import dagger.Module;
import dagger.Provides;

@Module
public class AudioEncoderModule {

    private MainActivity mainActivity;

    public AudioEncoderModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    AudioEncoder provideAudioEncoder() {
        return new AudioEncoder(mainActivity);
    }

}
