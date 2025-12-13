// ============================================================================
// FILE: SoundEffect.java
// Menangani efek suara untuk berbagai event
// ============================================================================
package Maze.Support;

import javax.sound.sampled.*;

public class SoundEffect {
    
    // Play sound saat generate maze
    public void playGenerate() {
        playTone(400, 100);
    }
    
    // Play sound saat mulai solve
    public void playStart() {
        playTone(600, 150);
    }
    
    // Play sound untuk setiap step exploration
    public void playStep() {
        playTone(800, 20);
    }
    
    // Play sound saat berhasil menemukan path (3 nada naik)
    public void playSuccess() {
        new Thread(() -> {
            playTone(600, 100);
            sleep(80);
            playTone(800, 100);
            sleep(80);
            playTone(1000, 200);
        }).start();
    }
    
    // Play sound saat gagal menemukan path (2 nada turun)
    public void playFail() {
        new Thread(() -> {
            playTone(400, 150);
            sleep(100);
            playTone(300, 300);
        }).start();
    }
    
    // Generate dan play tone dengan frekuensi dan durasi tertentu
    private void playTone(int hz, int msecs) {
        try {
            byte[] buf = new byte[1];
            AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
            
            // Generate sine wave
            for (int i = 0; i < msecs * 8; i++) {
                double angle = i / (8000f / hz) * 2.0 * Math.PI;
                buf[0] = (byte) (Math.sin(angle) * 80.0);
                sdl.write(buf, 0, 1);
            }
            
            sdl.drain();
            sdl.stop();
            sdl.close();
        } catch (LineUnavailableException e) {
            // Silent fail jika audio tidak tersedia
        }
    }
    
    // Helper method untuk sleep
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

