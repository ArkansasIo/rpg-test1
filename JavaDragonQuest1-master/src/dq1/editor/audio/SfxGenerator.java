package dq1.editor.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SfxGenerator {

    // Simple sine wave generator for short sound effects
    public static byte[] generateSine(double freq, double durationSeconds, double sampleRate, double amplitude) {
        int samples = (int) (durationSeconds * sampleRate);
        byte[] data = new byte[samples * 2]; // 16-bit
        double twoPiF = 2 * Math.PI * freq;
        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            short val = (short) (Math.sin(twoPiF * t) * amplitude * Short.MAX_VALUE);
            byte[] b = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(val).array();
            data[i * 2] = b[0];
            data[i * 2 + 1] = b[1];
        }
        return data;
    }

    public static byte[] generateNoise(double durationSeconds, double sampleRate, double amplitude) {
        int samples = (int) (durationSeconds * sampleRate);
        byte[] data = new byte[samples * 2];
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < samples; i++) {
            short val = (short) ((rnd.nextDouble() * 2 - 1) * amplitude * Short.MAX_VALUE);
            byte[] b = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(val).array();
            data[i * 2] = b[0];
            data[i * 2 + 1] = b[1];
        }
        return data;
    }

    public static byte[] mix(byte[] a, byte[] b) {
        int samples = Math.min(a.length, b.length) / 2;
        byte[] out = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            short va = ByteBuffer.wrap(a, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short vb = ByteBuffer.wrap(b, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int v = va + vb;
            if (v > Short.MAX_VALUE) v = Short.MAX_VALUE;
            if (v < Short.MIN_VALUE) v = Short.MIN_VALUE;
            byte[] outb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) v).array();
            out[i * 2] = outb[0];
            out[i * 2 + 1] = outb[1];
        }
        return out;
    }

    public static void saveWav(File file, byte[] pcmData, float sampleRate) throws Exception {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        try (OutputStream os = new FileOutputStream(file)) {
            // RIFF header
            int byteRate = (int) (sampleRate * 2);
            int subChunk2Size = pcmData.length;
            int chunkSize = 36 + subChunk2Size;
            os.write(new byte[]{'R', 'I', 'F', 'F'});
            os.write(intToLittleEndian(chunkSize));
            os.write(new byte[]{'W', 'A', 'V', 'E'});
            // fmt subchunk
            os.write(new byte[]{'f', 'm', 't', ' '});
            os.write(intToLittleEndian(16)); // subchunk1 size
            os.write(shortToLittleEndian((short) 1)); // audio format PCM
            os.write(shortToLittleEndian((short) 1)); // channels
            os.write(intToLittleEndian((int) sampleRate)); // sample rate
            os.write(intToLittleEndian(byteRate));
            os.write(shortToLittleEndian((short) 2)); // block align
            os.write(shortToLittleEndian((short) 16)); // bits per sample
            // data subchunk
            os.write(new byte[]{'d', 'a', 't', 'a'});
            os.write(intToLittleEndian(subChunk2Size));
            os.write(pcmData);
        }
    }

    private static byte[] intToLittleEndian(int v) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(v).array();
    }

    private static byte[] shortToLittleEndian(short v) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(v).array();
    }

    // Some preset fantasy SFX generators
    public static byte[] magicPulse(float sampleRate) {
        byte[] s1 = generateSine(880, 0.25, sampleRate, 0.6);
        byte[] s2 = generateSine(1320, 0.18, sampleRate, 0.45);
        return mix(s1, s2);
    }

    public static byte[] whoosh(float sampleRate) {
        byte[] n = generateNoise(0.35, sampleRate, 0.9);
        // simple fade-out envelope
        applyFade(n, sampleRate, 0.02, 0.35);
        return n;
    }

    public static byte[] sparkle(float sampleRate) {
        byte[] a = generateSine(1760, 0.07, sampleRate, 0.8);
        byte[] b = generateSine(2200, 0.06, sampleRate, 0.6);
        byte[] m = mix(a, b);
        applyFade(m, sampleRate, 0.0, 0.07);
        return m;
    }

    private static void applyFade(byte[] data, double sampleRate, double fadeIn, double fadeOut) {
        int samples = data.length / 2;
        int fi = (int) (fadeIn * sampleRate);
        int fo = (int) (fadeOut * sampleRate);
        for (int i = 0; i < samples; i++) {
            short v = ByteBuffer.wrap(data, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
            double factor = 1.0;
            if (i < fi && fi > 0) factor = (double) i / fi;
            if (i > samples - fo && fo > 0) factor = (double) (samples - i) / fo;
            short nv = (short) (v * factor);
            byte[] b = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(nv).array();
            data[i * 2] = b[0];
            data[i * 2 + 1] = b[1];
        }
    }
}
