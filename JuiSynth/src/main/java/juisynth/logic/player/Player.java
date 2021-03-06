/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juisynth.logic.player;

import juisynth.logic.signal.SignalStatus;
import juisynth.logic.signal.SignalSource;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingWorker;

/**
 * Player is a synthesizer implementation specific class instantiating SignalStatus-objects
 * to be fed information from the signal path consisting of operators.
 * Starts a thread and writes sample data fetched from SignalSource into
 * SourceDataLine at a given amplitude and frequency.
 * @author juicyp
 */
public class Player {

    private final AudioFormat format;
    private final DataLine.Info info;
    private SourceDataLine audioline;

    private SwingWorker worker;

    private byte[] sampleBuffer = new byte[Settings.BUFFER_SIZE];
    private SignalSource signalSource;
    private double frequency = 440;
    private double amp = 1;
    private boolean activeNote = true;

    /**
     * Constructor for Player sets format according to values specified in
     * Settings-class.
     */
    public Player() {
        // The constuctor of AudioFormat takes the sample rate, sample resolution in bits,
        // amount of channels, signedness (true for signed),
        // endianness (true for big endian) in that order as format information.
        format = new AudioFormat(Settings.SAMPLE_RATE, Settings.SAMPLE_SIZE,
                Settings.CHANNELS, Settings.SIGNED, Settings.BIG_ENDIAN);
        info = new DataLine.Info(SourceDataLine.class, format);
    }

    public double getAmp() {
        return amp;
    }

    /**
     * Sets the depth of the amplifier applied to the signal before writing it into
     * the buffer. Value must be between 0 and 1 inclusive.
     * @param amp Depth of amplification to be applied.
     */
    public void setAmp(double amp) {
        if (amp < 0 || amp > 1) {
            return;
        }
        this.amp = amp;
    }

    public double getFrequency() {
        return frequency;
    }

    /**
     * Sets the non negative frequency of the oscillated signal to be generated.
     * @param frequency The frequency of the generated signal.
     */
    public void setFrequency(double frequency) {
        if (frequency < 0) {
            return;
        }
        this.frequency = frequency;
    }
    
    public boolean getActiveNote() {
        return activeNote;
    }
    
    public void setActiveNote(boolean activeNote) {
        this.activeNote = activeNote;
    }

    public void setSignalSource(SignalSource signalSource) {
        this.signalSource = signalSource;
    }

    /**
     * Starts the player on a new thread.
     *
     * @see stopPlayer
     */
    public void startPlayer() {
        activeNote = true;
        if (signalSource != null && worker == null) {
            worker = new SwingWorker() {

                private boolean done;

                @Override
                protected Void doInBackground() {

                    done = false;

                    try {
                        audioline = (SourceDataLine) AudioSystem.getLine(info);
                        audioline.open(format);
                        audioline.start();

                        while (!done) {
                            writeBuffer();
                        }

                    } catch (LineUnavailableException e) {

                    } finally {
                        Player.this.audioline.drain();
                        Player.this.audioline.close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    done = true;
                }
            };
            worker.execute();
        }
    }

    /**
     * Sets a flag to kill the thread.
     *
     * @see startPlayer
     */
    public void stopPlayer() {
        if (worker != null) {
            worker.cancel(true);
            worker = null;
        }
    }

    private byte[] writeBuffer() {

        Arrays.fill(sampleBuffer, (byte) 0);

        int index = 0;

        SignalStatus signal = new SignalStatus(frequency);

        for (int i = 0; i < Settings.SAMPLES_PER_BUFFER; i++) {

            signal.resetSignal();
            signal.setActiveNote(activeNote);
            signal.setFrequency(frequency);

            signalSource.generateSample(signal);
            signal.setAmplitude(signal.getAmplitude() * amp);

            double ds = signal.getAmplitude() * Short.MAX_VALUE;
            short ss = (short) Math.round(ds);
            // Big endian, shift first eight bits and add as first part of sample
            sampleBuffer[index++] = (byte) (ss >> 8);
            sampleBuffer[index++] = (byte) (ss & 0xFF);
            
        }
        audioline.write(sampleBuffer, 0, Settings.BUFFER_SIZE);
        return sampleBuffer;
    }
}
