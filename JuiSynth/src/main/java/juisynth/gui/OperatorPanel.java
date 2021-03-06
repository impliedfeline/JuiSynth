/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juisynth.gui;

import juisynth.gui.listener.OscillatorWaveformListener;
import juisynth.gui.listener.OperatorParameterListener;
import juisynth.logic.Waveform;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import juisynth.gui.listener.OscillatorTuningListener;
import juisynth.gui.listener.SpectrumFilterListener;
import juisynth.logic.Operator;

/**
 * OperatorPanel is a class extending JPanel containing the GUI elements relevant to an
 * Operator.
 * @author juicyp
 */
public class OperatorPanel extends JPanel {
    
    private Operator operator;
    
    /**
     * Constructor for OperatorPanel, instantiates all the GUI components in the panel.
     * @param operator The Operator object to connect to the GUI elements.
     */
    public OperatorPanel(Operator operator) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.operator = operator;
        instantiateComponents();
    }

    private void instantiateComponents() {
        JLabel oscLabel = new JLabel("Oscillator", SwingConstants.CENTER);
        
        JLabel ampLabel = new JLabel("Volume", SwingConstants.CENTER);
        JSlider amp = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (operator.getPatch().getAmp() * 100));
        amp.setMajorTickSpacing(50);
        amp.setPaintTicks(true);
        amp.setPaintLabels(true);
        
        JLabel tuningLabel = new JLabel("Tuning", SwingConstants.CENTER);
        JSlider tuning = new JSlider(JSlider.HORIZONTAL, -7200, 7200, operator.getPatch().getOscillatorSettings().getTuning());
        tuning.setMajorTickSpacing(3600);
        tuning.setPaintTicks(true);
        tuning.setPaintLabels(true);
        tuning.setPaintTrack(false);
        
        JLabel filterLabel = new JLabel("Filter", SwingConstants.CENTER);
        JLabel filterParameterLabel = new JLabel("Cutoff", SwingConstants.CENTER);
        JSlider filter = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (operator.getPatch().getFilterSettings().getDepth() * 100));
        filter.setMajorTickSpacing(100);
        filter.setPaintTicks(true);
        filter.setPaintLabels(true);
        
        JLabel waveformLabel = new JLabel("Waveform", SwingConstants.CENTER);
        JComboBox waveform = new JComboBox(Waveform.values());
        
        JLabel fmLabel = new JLabel("FM Depth", SwingConstants.CENTER);
        JSlider fm = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) (operator.getPatch().getFmDepth() * 100));
        fm.setMajorTickSpacing(100);
        fm.setPaintTicks(true);
        fm.setPaintLabels(true);
        fm.setPaintTrack(false);
        
        JLabel amLabel = new JLabel("AM Depth", SwingConstants.CENTER);
        JSlider am = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) (operator.getPatch().getAmDepth() * 100));
        am.setMajorTickSpacing(100);
        am.setPaintTicks(true);
        am.setPaintLabels(true);
        am.setPaintTrack(false);
        
        add(oscLabel);
        
        amp.addChangeListener(new OperatorParameterListener(operator.getPatch(), amp, OperatorParameter.AMP));
        add(ampLabel);
        add(amp);
        
        tuning.addChangeListener(new OscillatorTuningListener(operator.getPatch().getOscillatorSettings(), tuning));
        add(tuningLabel);
        add(tuning);
        
        waveform.addActionListener(new OscillatorWaveformListener(operator.getPatch().getOscillatorSettings(), waveform));
        add(waveformLabel);
        add(waveform);
        
        add(fmLabel);
        fm.addChangeListener(new OperatorParameterListener(operator.getPatch(), fm, OperatorParameter.FM));
        add(fm);
        
        add(amLabel);
        am.addChangeListener(new OperatorParameterListener(operator.getPatch(), am, OperatorParameter.AM));
        add(am);
        
        add(new OscillatorOptionsPanel(operator.getPatch()));
        
        add(filterLabel);
        filter.addChangeListener(new SpectrumFilterListener(operator.getPatch().getFilterSettings(), filter));
        add(filterParameterLabel);
        add(filter);
        
        add(new ADSRPanel(operator.getPatch().getEnvelopeGeneratorSettings()));
    }
}
