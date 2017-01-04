/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.impliedfeline.juisynth;

import fi.impliedfeline.juisynth.logic.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
import java.util.Arrays;

/**
 *
 * @author juicyp
 */
public class JuiSynth {

    // BEWARE! Software is LOUD
    // Prototype modeled after Dr. Dobb's synth articles 
    // http://www.drdobbs.com/jvm/creating-music-components-in-java/229700113?pgno=1
    // Testing
    public static void main(String[] args) {

        int centsInOctave = 1200;
        
        Oscillator carrier = new Oscillator();
        carrier.setAdd(true);
        
        //FM doesn't work as intended. Sounds cool as hell though
        Oscillator modulator = new Oscillator();
        modulator.setFm(true);
        modulator.setFmDepth(0.1);
        modulator.setAm(true);
        modulator.setAmDepth(1);
        modulator.setTuning(centsInOctave);
        modulator.setSignalSource(carrier);
        
        Player player = new Player();
        player.setSignalSource(modulator);
        player.startPlayer();
        
        delay(10000);
        
        player.stopPlayer();
    }
    
    private static void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            
        }
    }
}
