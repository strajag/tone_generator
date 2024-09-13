package com.strajag.tone_generator;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

public class ToneGenerator
{
    private boolean is_type_square;

    private int duration;
    private int sampleRate;
    private int numSamples;
    private double[] sample;
    private double freqOfTone;
    private byte[] generatedSnd;

    private AudioTrack audioTrack;

    private Thread thread;
    private Handler handler;

    public ToneGenerator()
    {
        duration = 1; // seconds
        sampleRate = 44100;
        numSamples = duration * sampleRate;
        freqOfTone = 440; // hz
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];
        handler = new Handler();
    }

    public void set_type_square(boolean is_type_square)
    {
        this.is_type_square = is_type_square;
    }

    public void setFrequency(int frequency)
    {
        freqOfTone = frequency;
    }

    public void start()
    {
        if(thread != null)
            return;
        stop();

        thread = new Thread(new Runnable() {
            public void run()
            {
                genTone();
                playSound();
                /*handler.post(new Runnable() {
                    public void run()
                    {
                        playSound();
                    }
                });*/
                thread = null;
            }
        });
        thread.start();
    }

    public void stop()
    {
        if(audioTrack != null)
        {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    private void genTone(){
        if(!is_type_square)
        {
            for (int i = 0; i < numSamples; ++i)
            {
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
            }
        }
        else
        {
            for (int i = 0; i < numSamples; ++i)
            {
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));

                if(sample[i] > 0)
                    sample[i] = 1;
                else
                    sample[i] = -1;
            }
        }

        // convert to 16 bit pcm (assumes sample buffer is normalised)
        int idx = 0;
        for (double dVal : sample)
        {
            // scale to maximum amplitude
            short val = (short) ((dVal * 32767));
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    private void playSound(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.setLoopPoints(0, generatedSnd.length/2, -1);
        audioTrack.play();
    }
}
