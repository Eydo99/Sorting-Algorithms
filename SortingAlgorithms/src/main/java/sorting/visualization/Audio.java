package sorting.visualization;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Audio {
    // ── Persistent audio line (opened once, reused) ──
    private static final float SAMPLE_RATE = 44100;
    private static final int TONE_DURATION_MS = 50;
    private static final int NUM_SAMPLES = (int) (SAMPLE_RATE * TONE_DURATION_MS / 1000);

    private static SourceDataLine audioLine;
    private static final Object audioLock = new Object();

    private static void ensureAudioLine() {
        if (audioLine != null)
            return;
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();
        } catch (Exception e) {
            audioLine = null;
        }
    }

    public static void playTone(int value, int maxValue) {
        if (maxValue == 0)
            return;

        double frequency = 150 + ((double) value / maxValue) * 1050;

        synchronized (audioLock) {
            try {
                ensureAudioLine();
                if (audioLine == null)
                    return;

                byte[] buffer = new byte[NUM_SAMPLES * 2];
                for (int i = 0; i < NUM_SAMPLES; i++) {
                    double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
                    double envelope = 1.0;

                    if (i < NUM_SAMPLES * 0.1) {
                        envelope = i / (NUM_SAMPLES * 0.1);
                    } else if (i > NUM_SAMPLES * 0.8) {
                        envelope = (NUM_SAMPLES - i) / (NUM_SAMPLES * 0.2);
                    }

                    short sample = (short) (Math.sin(angle) * envelope * 32767 * 0.3);
                    buffer[2 * i] = (byte) (sample & 0xFF);
                    buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
                }

                audioLine.write(buffer, 0, buffer.length);
            } catch (Exception e) {
                // Silently ignore audio errors
            }
        }
    }

    public void closeAudio() {
        synchronized (audioLock) {
            if (audioLine != null) {
                audioLine.drain();
                audioLine.close();
                audioLine = null;
            }
        }
    }
}
