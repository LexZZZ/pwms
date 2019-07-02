package ru.lex3.pwms.visu;


import ru.lex3.pwms.interfaces.PLCData;
import ru.lex3.pwms.main.PWM;
import ru.lex3.pwms.main.S7Data;
import ru.lex3.pwms.main.S7ServiceData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

class ParameterPanel extends JFrame {

    private PWM device;
    private JPanel contentPane;

    private JLabel lblSensor;
    private JLabel lblTopSensor;
    private JLabel lblBottomSensor;
    private JLabel lblMin;
    private JLabel lblMax;
    private JLabel lblTolerance1;
    private JLabel lblTolerance2;
    private JLabel lblTolerance3;
    private JLabel lblTolerance4;
    private JFormattedTextField txtTopSensorMin;
    private JFormattedTextField txtBottomSensorMin;
    private JFormattedTextField txtTopSensorMax;
    private JFormattedTextField txtBottomSensorMax;
    private JFormattedTextField txtTopSensorTolerance1;
    private JFormattedTextField txtBottomSensorTolerance1;
    private JFormattedTextField txtTopSensorTolerance2;
    private JFormattedTextField txtBottomSensorTolerance2;
    private JFormattedTextField txtTopSensorTolerance3;
    private JFormattedTextField txtBottomSensorTolerance3;
    private JFormattedTextField txtTopSensorTolerance4;
    private JFormattedTextField txtBottomSensorTolerance4;
    private JButton btnOk;
    private JButton btnCancel;


    ParameterPanel(PWM device) {

        setSize(335, 165);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
        this.setLocation(x, y);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        setLayout(new BorderLayout());
        add(contentPane);
        setResizable(false);

        setIconImage(Toolkit.getDefaultToolkit().getImage(PWMsVisu.class.getResource("/resources/parameterSettings.Image16x16.png")));
        //setIconImage(new darrylbu.icon.AlphaImageIcon(new ImageIcon(PWMsVisu.class.getResource("/ru/lex3/pwms/resources/connectionSettings.Image16x16.png")), 0).getImage());

        contentPane.setSize(getWidth(), getHeight());
        //setVisible(false);
        contentPane.setLayout(null);
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
     /*   contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                "Connection settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));*/

        lblSensor = new JLabel("Sensor");
        lblSensor.setBounds(10, 15, 60, 14);
        contentPane.add(lblSensor);

        lblTopSensor = new JLabel("Top");
        lblTopSensor.setBounds(10, 40, 60, 14);
        contentPane.add(lblTopSensor);

        lblBottomSensor = new JLabel("Bottom");
        lblBottomSensor.setBounds(10, 60, 60, 14);
        contentPane.add(lblBottomSensor);

        lblMin = new JLabel("Min", SwingConstants.CENTER);
        lblMin.setBounds(70, 15, 60, 14);
        contentPane.add(lblMin);

        txtTopSensorMin = new JFormattedTextField(new NumberFormatterFloat(-9.99, 9.99).getFormatter());
        txtTopSensorMin.setHorizontalAlignment(SwingConstants.CENTER);
        txtTopSensorMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtTopSensorMin.setValue(((S7Data) device.getSensors().get(0)).scaleMin);
        txtTopSensorMin.setBounds(70, 37, 60, 20);
        txtTopSensorMin.addFocusListener(new FocusSelect());
        contentPane.add(txtTopSensorMin);

        txtBottomSensorMin = new JFormattedTextField(new NumberFormatterFloat(-9.99, 9.99).getFormatter());
        txtBottomSensorMin.setHorizontalAlignment(SwingConstants.CENTER);
        txtBottomSensorMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtBottomSensorMin.setValue(((S7Data) device.getSensors().get(1)).scaleMin);
        txtBottomSensorMin.setBounds(70, 57, 60, 20);
        txtBottomSensorMin.addFocusListener(new FocusSelect());
        contentPane.add(txtBottomSensorMin);

        lblMax = new JLabel("Max", SwingConstants.CENTER);
        lblMax.setBounds(130, 15, 60, 14);
        contentPane.add(lblMax);

        txtTopSensorMax = new JFormattedTextField(new NumberFormatterFloat(99.99).getFormatter());
        txtTopSensorMax.setHorizontalAlignment(SwingConstants.CENTER);
        txtTopSensorMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtTopSensorMax.setValue(((S7Data) device.getSensors().get(0)).scaleMax);
        txtTopSensorMax.setBounds(130, 37, 60, 20);
        txtTopSensorMax.addFocusListener(new FocusSelect());
        contentPane.add(txtTopSensorMax);

        txtBottomSensorMax = new JFormattedTextField(new NumberFormatterFloat(99.99).getFormatter());
        txtBottomSensorMax.setHorizontalAlignment(SwingConstants.CENTER);
        txtBottomSensorMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtBottomSensorMax.setValue(((S7Data) device.getSensors().get(1)).scaleMax);
        txtBottomSensorMax.setBounds(130, 57, 60, 20);
        txtBottomSensorMax.addFocusListener(new FocusSelect());
        contentPane.add(txtBottomSensorMax);

        lblTolerance1 = new JLabel("Tolerance 1", SwingConstants.CENTER);
        lblTolerance1.setBounds(195, 8, 60, 28);
        contentPane.add(lblTolerance1);

        txtTopSensorTolerance1 = new JFormattedTextField(new NumberFormatterFloat(3.99).getFormatter());
        txtTopSensorTolerance1.setHorizontalAlignment(SwingConstants.CENTER);
        txtTopSensorTolerance1.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtTopSensorTolerance1.setValue(((S7Data) device.getSensors().get(0)).tollerance[0]);
        txtTopSensorTolerance1.setBounds(195, 37, 60, 20);
        txtTopSensorTolerance1.addFocusListener(new FocusSelect());
        contentPane.add(txtTopSensorTolerance1);

        txtBottomSensorTolerance1 = new JFormattedTextField(new NumberFormatterFloat(3.99).getFormatter());
        txtBottomSensorTolerance1.setHorizontalAlignment(SwingConstants.CENTER);
        txtBottomSensorTolerance1.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtBottomSensorTolerance1.setValue(((S7Data) device.getSensors().get(1)).tollerance[0]);
        txtBottomSensorTolerance1.setBounds(195, 57, 60, 20);
        txtBottomSensorTolerance1.addFocusListener(new FocusSelect());
        contentPane.add(txtBottomSensorTolerance1);

        lblTolerance2 = new JLabel("Tolerance 2", SwingConstants.CENTER);
        lblTolerance2.setBounds(255, 8, 60, 28);
        contentPane.add(lblTolerance2);
        txtTopSensorTolerance2 = new JFormattedTextField(new NumberFormatterFloat(3.99).getFormatter());
        txtTopSensorTolerance2.setHorizontalAlignment(SwingConstants.CENTER);
        txtTopSensorTolerance2.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtTopSensorTolerance2.setValue(((S7Data) device.getSensors().get(0)).tollerance[1]);
        txtTopSensorTolerance2.setBounds(255, 37, 60, 20);
        txtTopSensorTolerance2.addFocusListener(new FocusSelect());
        contentPane.add(txtTopSensorTolerance2);

        txtBottomSensorTolerance2 = new JFormattedTextField(new NumberFormatterFloat(3.99).getFormatter());
        txtBottomSensorTolerance2.setHorizontalAlignment(SwingConstants.CENTER);
        txtBottomSensorTolerance2.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtBottomSensorTolerance2.setValue(((S7Data) device.getSensors().get(1)).tollerance[1]);
        txtBottomSensorTolerance2.setBounds(255, 57, 60, 20);
        txtBottomSensorTolerance2.addFocusListener(new FocusSelect());
        contentPane.add(txtBottomSensorTolerance2);

        btnOk = new JButton();
        btnOk.setToolTipText("<html><center>save</center><center>and exit</center></html>");
        btnOk.addActionListener(e -> btnOk_actionPerformed(e));
        btnOk.setIcon(
                new ImageIcon(PWMsVisu.class.getResource("/resources/btnOk.Image.24x24.png")));
        btnOk.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnOk.setMargin(new Insets(0, 0, 0, 0));
        btnOk.setHorizontalTextPosition(SwingConstants.CENTER);
        btnOk.setBounds(226, 83, 42, 42);
        contentPane.add(btnOk);

        btnCancel = new JButton();
        btnCancel.addActionListener(e -> btnCancel_actionPerformed(e));
        btnCancel.setIcon(new ImageIcon(PWMsVisu.class.getResource("/resources/btnClose.Image.24x24.png")));
        btnCancel.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCancel.setMargin(new Insets(0, 0, 0, 0));
        btnCancel.setHorizontalTextPosition(SwingConstants.CENTER);
        btnCancel.setBounds(273, 83, 42, 42);
        contentPane.add(btnCancel);

        setDevice(device);
        setLanguage();
        setAlwaysOnTop(true);
        for (PLCData s7Data : device.getSensors()) {
            System.out.println("DB: " + ((S7ServiceData)((S7Data) s7Data).serviceData).getDB());
            System.out.println("startRead byte: " + ((S7ServiceData)((S7Data) s7Data).serviceData).startRead());
            System.out.println("quantity bytes: " + ((S7ServiceData)((S7Data) s7Data).serviceData).qtyRead());
        }


    }

    private void btnOk_actionPerformed(ActionEvent e) {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ((S7Data) device.getSensors().get(0)).scaleMin = Float.parseFloat(ParameterPanel.this.txtTopSensorMin.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(1)).scaleMin = Float.parseFloat(ParameterPanel.this.txtBottomSensorMin.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(0)).scaleMax = Float.parseFloat(ParameterPanel.this.txtTopSensorMax.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(1)).scaleMax = Float.parseFloat(ParameterPanel.this.txtBottomSensorMax.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(0)).tollerance[0] = Float.parseFloat(ParameterPanel.this.txtTopSensorTolerance1.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(1)).tollerance[0] = Float.parseFloat(ParameterPanel.this.txtBottomSensorTolerance1.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(0)).tollerance[1] = Float.parseFloat(ParameterPanel.this.txtTopSensorTolerance2.getText().replace(",", "."));
            ((S7Data) device.getSensors().get(1)).tollerance[1] = Float.parseFloat(ParameterPanel.this.txtBottomSensorTolerance2.getText().replace(",", "."));
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
            device.write();
            dispose();
        }
    }

    private void setDevice(PWM device) {
        this.device = device;
    }

    private void btnCancel_actionPerformed(ActionEvent e) {
        dispose();
    }


    private void txtIdleTimeSpan_TextChanged(DocumentEvent e) {
        // Try convert text value
        int us = 0;
        try {
            us = Integer.valueOf(txtBottomSensorTolerance1.getText());
            if (us < 0)
                us = 0;
        } catch (Exception ex) {

        }
        txtBottomSensorTolerance1.setText(String.valueOf(us));
    }

    private class FocusSelect extends FocusAdapter {
        /**
         * Invoked when a component gains the keyboard focus.
         *
         * @param e
         */
        @Override
        public void focusGained(FocusEvent e) {
            Component c = e.getComponent();
            if (c instanceof JFormattedTextField) {
                selectItLater(c);
            } else if (c instanceof JTextField) {
                ((JTextField) c).selectAll();
            }
        }

        //Workaround for formatted text field focus side effects.

        void selectItLater(Component c) {
            if (c instanceof JFormattedTextField) {
                final JFormattedTextField ftf = (JFormattedTextField) c;
                SwingUtilities.invokeLater(() -> ftf.selectAll());
            }
        }

        /**
         * Invoked when a component loses the keyboard focus.
         *
         * @param e
         */
        @Override
        public void focusLost(FocusEvent e) {


        }

    }

    /**
     * Creates specified float format for the formatedTextField
     */
    private class NumberFormatterFloat extends NumberFormatter {
        private NumberFormatter formatter;

        NumberFormatterFloat(double maximum) {
            createFormatter(0.0, maximum);
        }

        NumberFormatterFloat(double minimum, double maximum) {
            createFormatter(minimum, maximum);
        }

        private void createFormatter(double minimum, double maximum) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(2);
            nf.setGroupingUsed(false);
            formatter = new NumberFormatter(nf);
            //formatter.setValueClass(Number.class);
            formatter.setMinimum(minimum);
            formatter.setMaximum(maximum);
            formatter.setAllowsInvalid(false);
        }

        NumberFormatter getFormatter() {
            return formatter;
        }
    }

    private void setLanguage() {
        // set controls
        /*contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), PWMsVisu.resources.getString("grbConnection_Text"),
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        */
  /*      lblTopSensor.setText(PWMsVisu.resources.getString("lblPLCPort_Text"));
        lblBottomSensor.setText(PWMsVisu.resources.getString("lblLocalPort_Text"));
        lblMin.setText(PWMsVisu.resources.getString("lblRack_Text"));
        lblMax.setText(PWMsVisu.resources.getString("lblSlot_Text"));
        lblTolerance4.setText(PWMsVisu.resources.getString("lblAutoConnect2"));
        lblTolerance3.setText(PWMsVisu.resources.getString("chkAsyncConnect_TextAsync"));
        lblTolerance2.setText(PWMsVisu.resources.getString("lblmaxIdleTime_Text"));
        btnCancel.setToolTipText(PWMsVisu.resources.getString("btnConnect_Text"));
        btnDisconnect.setToolTipText(PWMsVisu.resources.getString("btnDisconnect_Text"));
        btnOk.setToolTipText(PWMsVisu.resources.getString("btnSaveConnectionSettings_Text"));
*/
    }
}