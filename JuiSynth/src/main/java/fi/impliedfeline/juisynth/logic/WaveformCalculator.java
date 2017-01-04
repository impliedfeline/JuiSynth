/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.impliedfeline.juisynth.logic;

import java.util.Random;

/**
 * Contains calculations relevant to waves.
 * @author juicyp
 */
public class WaveformCalculator {
    
    private static final Random noiseGenerator = new Random();

    public static double calculateWaveformY(double x, Waveform waveform) {

        double y;

        switch (waveform) {
            default:
            case SIN:
                y = Math.sin(2.0 * Math.PI * x);
                break;

            case SQU:
                if (x < 0.5) {
                    y = 1.0;
                } else {
                    y = -1.0;
                }
                break;

            case SAW:
                y = 2.0 * (x - Math.floor(x + 0.5));
                break;

            case TRI:
                y = 2.0 * Math.abs(2.0 * (x - Math.floor(x + 0.5))) - 1;
                break;

            case NOI:
                y = 2 * noiseGenerator.nextDouble() - 1;
        }
        return y;
    }
    
}
