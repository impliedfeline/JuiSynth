/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juisynth.logic;

import juisynth.logic.envelope.ADSR;
import juisynth.logic.envelope.EnvelopeGenerator;
import juisynth.logic.filter.Filter;
import juisynth.logic.filter.SpectrumFilter;
import juisynth.logic.oscillator.Oscillator;
import juisynth.logic.signal.SignalSource;
import juisynth.logic.signal.SignalStatus;

/**
 * Operator defines the implementation of a synthesizer component, consisting of
 * an oscillator, a spectrum filter and an ADSR envelope.
 * @author juicyp
 */
public class Operator implements SignalSource {

    private Oscillator oscillator;
    private EnvelopeGenerator eg;
    private Filter filter;
    private SignalSource signalSource = null;
    private Patch patch;

    /**
     * Constructor for Operator.
     * @param patch The settings of the operator
     * @see juisynth.logic.Operator#loadPatch(juisynth.logic.Patch)
     */
    public Operator(Patch patch) {
        loadPatch(patch);
    }

    @Override
    public void generateSample(SignalStatus signal) {

        if (patch.isBypass()) {
            if (signalSource != null) {
                signalSource.generateSample(signal);
            }
            return;
        }

        double amplitude = oscillator.generateWaveAmplitude(signal);
        amplitude *= patch.getAmp();

        if (patch.isInvert()) {
            amplitude = -amplitude;
        }

        amplitude = filter.generateFilter(oscillator.getPhase(), amplitude);
        amplitude *= eg.generateEnvelope(signal.getActiveNote());

        if (patch.isFm()) {
            applyFM(amplitude, signal);
        }

        // Prevent self-AM modulation
        if (patch.isAdd()) {
            signal.setActiveOperatorCount(signal.getActiveOperatorCount() + 1);
        }

        if (signalSource != null) {
            signalSource.generateSample(signal);
        }

        if (patch.isAm()) {
            applyAM(amplitude, signal);
        }

        if (patch.isAdd()) {
            if (signalSource != null) {
                signal.setAmplitude(signal.getAmplitude() + amplitude / signal.getActiveOperatorCount());
            } else {
                // Active operator count will always be non zero
                signal.setAmplitude(amplitude / signal.getActiveOperatorCount());
            }
        }
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public void setOscillator(Oscillator oscillator) {
        this.oscillator = oscillator;
    }

    public EnvelopeGenerator getEG() {
        return eg;
    }

    public void setEG(EnvelopeGenerator eg) {
        this.eg = eg;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public SignalSource getSignalSource() {
        return signalSource;
    }

    public void setSignalSource(SignalSource signalSource) {
        this.signalSource = signalSource;
    }

    public Patch getPatch() {
        return patch;
    }

    /**
     * Loads the settings for oscillator, spectrum filter and ADSR envelope contained
     * in the provided Patch-object into the fields of this particular instance of Operator.
     * @param patch A patch containing the settings for an operator.
     */
    public void loadPatch(Patch patch) {
        this.patch = patch;
        this.oscillator = new Oscillator(patch.getOscillatorSettings());
        this.eg = new ADSR(patch.getEnvelopeGeneratorSettings());
        this.filter = new SpectrumFilter(patch.getFilterSettings());
    }

    private void applyFM(double amplitude, SignalStatus signal) {
        signal.setFrequency(signal.getFrequency() * Math.pow(2, amplitude * patch.getFmDepth()));
    }

    private void applyAM(double amplitude, SignalStatus signal) {
        signal.setAmplitude(signal.getAmplitude() * Math.pow(2, (amplitude - 1) * patch.getAmDepth()));
    }
}
