package Maze;

import javax.sound.sampled.*;


// SoundUtils (Audio Generator)
class SoundUtils {
    // Menghasilkan bunyi 'blip' pendek secara programatis (Sine Wave)
    public static void playStepSound() {
        try {
            tone(400, 20, 0.1); // Frekuensi 400Hz, durasi 20ms
        } catch (Exception e) { /* Ignore */ }
    }
    
    public static void playFinishSound() {
        new Thread(() -> {
            try {
                tone(600, 100, 0.5);
                Thread.sleep(100);
                tone(800, 200, 0.5);
            } catch (Exception e) { /* Ignore */ }
        }).start();
    }

    // Generator nada sederhana menggunakan SourceDataLine
    private static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
        float SAMPLE_RATE = 8000f;
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat(SAMPLE_RATE,8,1,true,false);       
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        
        for (int i=0; i < msecs*8; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
            sdl.write(buf,0,1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
}
