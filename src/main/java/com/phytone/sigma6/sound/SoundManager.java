package com.phytone.sigma6.sound;

import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {

    public static void EnableSound() {
        try {
            String soundPath = "/resources/assets/sounds/enable.wav";
            InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(soundPath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(inputStream));
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void DisableSound() {
        try {
            String soundPath = "/resources/assets/sounds/disable.wav";
            InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(soundPath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(inputStream));
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
