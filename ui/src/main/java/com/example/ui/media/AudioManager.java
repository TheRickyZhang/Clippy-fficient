package com.example.ui.media;


import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    static AudioManager instance = new AudioManager();
    private static final String FOLDER = "/com/example/ui/media/";
    Map<String, AudioClip> mp = new HashMap<>();

    private AudioManager() {}

    public static AudioManager get() { return instance; }

    public void play(String fileName) {
        AudioClip ac = mp.get(fileName);
        if(ac != null) {
            ac.play();
        } else {
            URL url = getClass().getResource(FOLDER + fileName + ".mp3");
            if(url == null) {
                System.out.println("Huh");
                throw new RuntimeException("Cannot get audio file");
            }
            AudioClip nac = new AudioClip(url.toExternalForm());
            mp.put(fileName, nac);
        }
    }
}
