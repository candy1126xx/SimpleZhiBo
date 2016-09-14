package com.candy.simplezhibo.component;

import com.candy.simplezhibo.activity.MainActivity;
import com.candy.simplezhibo.module.AudioEncoderModule;
import com.candy.simplezhibo.module.VideoEncoderModule;

import dagger.Component;

@Component(modules = { VideoEncoderModule.class, AudioEncoderModule.class })
public interface MainActivityComponent {
    MainActivity inject(MainActivity mainActivity);
}
