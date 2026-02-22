package com.mpcmaid.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mpcmaid.pgm.Element;
import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Parameter;
import com.mpcmaid.pgm.Range;
import com.mpcmaid.pgm.Parameter.EnumType;
import com.mpcmaid.pgm.Parameter.Type;

/**
 * Dedicated JPanel to contain an input together with its label, and the binding
 * logic to/from an Element and a Parameter. Represents
 * 
 * @author cyrille martraire
 */
public abstract class Widget<T extends JComponent> extends JPanel implements ActionListener, FocusListener, BindingCapable {

	@Serial
    private static final long serialVersionUID = -532729841296280652L;

	private static final Logger logger = System.getLogger(Widget.class.getName());

	private static final Font MEDIUM_FONT = new Font("Verdana", Font.PLAIN, 12);

	protected final Element element;

	protected final Parameter parameter;

	protected T value;

	public Widget(Element element, Parameter parameter) {
		super();
		this.element = element;
		this.parameter = parameter;

		this.setupLabel(parameter);
		this.setupValue();
		this.setupToolTip(parameter);
	}

	public void load() {
	}

	public void save() {
	}

	public void setupToolTip(Parameter parameter) {
		final String toolTip = parameter.getType().toolTip();
		if (value != null && toolTip != null) {
			value.setToolTipText(toolTip);
		}
	}

	protected void setupLabel(Parameter parameter) {
		final JLabel label = new JLabel(parameter.getLabel() + ":", JLabel.RIGHT);
		label.setFont(MEDIUM_FONT);
		add(label);
	}

	protected abstract void setupValue();

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent arg0) {
		save();
	}

	public void actionPerformed(ActionEvent e) {
		save();
	}

	public Element getElement() {
		return element;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public String getLabel() {
		return parameter.getLabel();
	}

	public Type getType() {
		return parameter.getType();
	}

	public boolean notValidate(Object o) {
		return !getType().validate(o);
	}

	protected void check(final Object o) {
		if (notValidate(o)) {
			final String msg = "Invalid " + getLabel() + " value: " + o + ".";
			logger.log(Level.ERROR,msg);
			// JOptionPane.showMessageDialog(null, msg, "Invalid value",
			// JOptionPane.ERROR_MESSAGE);
			onError();
		}
	}

	protected void onError() {
		value.requestFocus();
		Toolkit.getDefaultToolkit().beep();
	}

	public String toString() {
		return "Presentation: " + element + " " + parameter;
	}

	public static class IntegerField extends Widget<JTextField> {

		@Serial
        private static final long serialVersionUID = 3709006660547691155L;

		public IntegerField(Element element, Parameter parameter) {
			super(element, parameter);
			setLayout(new GridLayout(1, 2, 10, 10));
		}

		protected void setupValue() {
			value = new JTextField("", 4);
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			getTextField().addActionListener(this);
			getTextField().addFocusListener(this);
			load();
		}

		private JTextField getTextField() {
			return value;
		}

		public void load() {
			final Object v = element.get(parameter);
			getTextField().setText(String.valueOf(v));
		}

		public void save() {
			final String text = getTextField().getText();
			final Integer v = Integer.valueOf(text);
			check(v);
			element.set(parameter, v);
		}

		public String toString() {
			return "IntegerField: " + element + " " + parameter;
		}

	}

	public static class StringField extends Widget<JTextField> {

		@Serial
        private static final long serialVersionUID = 3200524341632520173L;

		public StringField(Element element, Parameter parameter) {
			super(element, parameter);
			setLayout(new GridLayout(1, 2, 10, 10));
		}

		protected void setupValue() {
			value = new JTextField("", 4);
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			getTextField().addActionListener(this);
			getTextField().addFocusListener(this);
			getTextField().setEnabled(false);
			load();
		}

		private JTextField getTextField() {
			return value;
		}

		public void load() {
			final String text = (String) element.get(parameter);
			getTextField().setText(text);
		}

		public void save() {
			final String text = getTextField().getText();
			check(text);
			element.set(parameter, text);
		}

		public String toString() {
			return "StringField: " + element + " " + parameter;
		}
	}

	public static class RangeField extends Widget<JTextField> {

		@Serial
        private static final long serialVersionUID = 5633564907524431966L;

		private JTextField value2;

		public RangeField(Element element, Parameter parameter) {
			super(element, parameter);
			setLayout(new GridLayout(1, 3, 15, 20));
		}

		protected void setupValue() {
			value = new JTextField("", 2);
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			value2 = new JTextField("", 2);
			value2.setAlignmentX(LEFT_ALIGNMENT);
			value2.setFont(MEDIUM_FONT);
			add(value2);

			getTextField().addActionListener(this);
			getTextField2().addActionListener(this);
			getTextField().addFocusListener(this);
			getTextField2().addFocusListener(this);
			load();
		}

		protected void check(final Object o) {
			final Range range = (Range) o;
			if (notValidate(range.low())) {
				final String msg = "Invalid " + getLabel() + " value: " + range.low() + ".";
				logger.log(Level.ERROR,msg);

				value.requestFocus();
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			if (notValidate(range.high())) {
				final String msg = "Invalid " + getLabel() + " value: " + range.high() + ".";
				logger.log(Level.ERROR,msg);

				value2.requestFocus();
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			if (range.isReversed()) {
				element.set(parameter, range.reverse());
				load();
			}
		}

		public void setupToolTip(Parameter parameter) {
			super.setupToolTip(parameter);
			final String toolTip = parameter.getType().toolTip();
			if (value2 != null && toolTip != null) {
				value2.setToolTipText(toolTip);
			}
		}

		private JTextField getTextField() {
			return value;
		}

		private JTextField getTextField2() {
			return value2;
		}

		public void load() {
			final Range range = (Range) element.get(parameter);
			getTextField().setText(String.valueOf(range.low()));
			getTextField2().setText(String.valueOf(range.high()));
		}

		public void save() {
			final String low = getTextField().getText();
			final String high = getTextField2().getText();
			final Range range = new Range(Integer.parseInt(low), Integer.parseInt(high));
			check(range);
			element.set(parameter, range);
		}

	}

	public static class ComboField extends Widget<JComboBox<String>> {

		@Serial
        private static final long serialVersionUID = 1116472605960451997L;

		public ComboField(Element element, Parameter parameter) {
			super(element, parameter);
			setLayout(new GridLayout(1, 3, 15, 20));
		}

		protected void setupValue() {
			final EnumType type = (EnumType) parameter.getType();
			value = new JComboBox<>(type.getValues());
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			getComboBox().addActionListener(this);
			load();
		}

		private JComboBox<String> getComboBox() {
			return value;
		}

		public void load() {
			final Integer selection = (Integer) element.get(parameter);
			getComboBox().setSelectedIndex(selection);
		}

		public void save() {
            final Integer sel = getComboBox().getSelectedIndex();
			check(sel);
			element.set(parameter, sel);
		}

		public String toString() {
			return "ComboField: " + element + " " + parameter;
		}
	}

	public static class OffIntegerField extends Widget<JComboBox<String>> {

		@Serial
        private static final long serialVersionUID = -454582028253676511L;
		
		private final String[] values;

		public OffIntegerField(Element element, Parameter parameter, String[] values) {
			super(element, parameter);
			setLayout(new GridLayout(1, 3, 15, 20));
			this.values = values;
			setupValuePost();
			// setupToolTip(parameter);
		}

		protected void setupValue() {
		}

		protected void setupValuePost() {
			value = new JComboBox<>(values);
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			getComboBox().addActionListener(this);
			getComboBox().addFocusListener(this);
			load();
		}

		private JComboBox<String> getComboBox() {
			return value;
		}

		public void load() {
			final Integer selection = (Integer) element.get(parameter);
			getComboBox().setSelectedIndex(selection);
		}

		public void save() {
            final Integer sel = getComboBox().getSelectedIndex();
			check(sel);
			element.set(parameter, sel);
		}

		public String toString() {
			return "OffIntegerField: " + element + " " + parameter;
		}
	}

	public static class TuningField extends Widget<JTextField> {

		@Serial
        private static final long serialVersionUID = -1579155864749957680L;

		public TuningField(Layer element, Parameter parameter) {
			super(element, parameter);
			setLayout(new GridLayout(1, 2, 10, 10));
		}

		public void setupToolTip(Parameter parameter) {
			if (value != null) {
				value.setToolTipText("Min = -36.00, max = 36.00");
			}
		}

		protected void setupValue() {
			value = new JTextField("", 4);
			value.setAlignmentX(LEFT_ALIGNMENT);
			value.setFont(MEDIUM_FONT);
			add(value);

			getTextField().addActionListener(this);
			getTextField().addFocusListener(this);
			load();
		}

		private JTextField getTextField() {
			return value;
		}

		private Layer getSampleElement() {
			return (Layer) element;
		}

		public void load() {
			final double tuning = getSampleElement().getTuning();
			final NumberFormat decimalFormat = NumberFormat.getInstance(Locale.US);
			decimalFormat.setMinimumIntegerDigits(1);
			decimalFormat.setMaximumFractionDigits(2);
			getTextField().setText(decimalFormat.format(tuning));
		}

		public void save() {
			final String text = getTextField().getText();
			final Double v = Double.valueOf(text);

			if (parameter.getType().validate(v)) {
				onError();
				return;
			}

			getSampleElement().setTuning(Double.parseDouble(text));
		}

		public String toString() {
			return "TuningField: " + element + " " + parameter;
		}
	}

}