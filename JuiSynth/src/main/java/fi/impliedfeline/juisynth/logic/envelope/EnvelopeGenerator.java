/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.impliedfeline.juisynth.logic.envelope;

import fi.impliedfeline.juisynth.logic.signal.SignalStatus;

/**
 *
 * @author juicyp
 */
public abstract class EnvelopeGenerator {
    
    public abstract double generateEnvelope(SignalStatus signal);
}