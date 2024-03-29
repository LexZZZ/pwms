package ru.lex3.pwms.visu;

import ru.lex3.pwms.interfaces.UICallback;
import ru.lex3.pwms.main.PWM;
import ru.lex3.pwms.main.S7Data;
import ru.lex3.pwms.visu.disabledjpanel.DisabledJPanel;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WorkPanel extends JPanel implements UICallback {

    private PWM device;
    private static ConnectionPanel grbConnectionSettings;
    private static ParameterPanel grbParameterSettings;
    private DisabledJPanel disabledMainPanel;
    private JPanel subPanel;

    private JLabel lblElectrode;
    private JLabel lblTopElectrode;
    private JLabel lblBottomElectrode;
    private JLabel lblCurrentMeasure;
    private JTextField txtTopCurrentMeasure;
    private JTextField txtBottomCurrentMeasure;
    private JLabel lblLastMeasure;
    private JTextField txtTopLastMeasure;
    private JTextField txtBottomLastMeasure;

    private JPanel statusBar;
    private JTextField lblConnectionState;
    private JTextField lblDeviceState;

    private JButton btnParameterSettings;
    private JButton btnConnectionSettings;

    WorkPanel(PWM device, int x, int y) {
        this.device = device;
        this.device.setUICallback(this);

        setBounds(x, y, 255, 113);
        setLayout(null);
        setBorder(null);

        subPanel = new JPanel();
        subPanel.setBounds(0, 0, getWidth(), getHeight());
        subPanel.setLayout(null);
        subPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), device.getDeviceName(), TitledBorder.LEADING, TitledBorder.TOP,
                null, new Color(0, 0, 0)));
        add(subPanel);

        disabledMainPanel = new DisabledJPanel(subPanel);
        disabledMainPanel.setBounds(subPanel.getBounds());
        disabledMainPanel.setDisabledColor(new Color(240, 240, 240, 100));
        disabledMainPanel.setEnabled(true);
        add(disabledMainPanel);

        lblElectrode = new JLabel("Electrode");
        lblElectrode.setBounds(10, 20, 60, 14);
        subPanel.add(lblElectrode);

        lblTopElectrode = new JLabel("Top");
        lblTopElectrode.setBounds(10, 45, 60, 14);
        subPanel.add(lblTopElectrode);

        lblBottomElectrode = new JLabel("Bottom");
        lblBottomElectrode.setBounds(10, 65, 60, 14);
        subPanel.add(lblBottomElectrode);

        lblCurrentMeasure = new JLabel("Current", SwingConstants.CENTER);
        subPanel.add(lblCurrentMeasure);
        lblCurrentMeasure.setBounds(80, 20, 60, 14);

        txtTopCurrentMeasure = new JTextField(SwingConstants.CENTER);
        txtTopCurrentMeasure.setEditable(false);
        txtTopCurrentMeasure.setBorder(BorderFactory.createEtchedBorder());
        txtTopCurrentMeasure.setBounds(80, 42, 60, 20);
        subPanel.add(txtTopCurrentMeasure);

        txtBottomCurrentMeasure = new JTextField(SwingConstants.CENTER);
        txtBottomCurrentMeasure.setEditable(false);
        txtBottomCurrentMeasure.setBorder(BorderFactory.createEtchedBorder());
        txtBottomCurrentMeasure.setBounds(80, 62, 60, 20);
        subPanel.add(txtBottomCurrentMeasure);

        lblLastMeasure = new JLabel("Last", SwingConstants.CENTER);
        lblLastMeasure.setBounds(140, 20, 60, 14);
        subPanel.add(lblLastMeasure);

        txtTopLastMeasure = new JTextField(SwingConstants.CENTER);
        txtTopLastMeasure.setEditable(false);
        txtTopLastMeasure.setBorder(BorderFactory.createEtchedBorder());
        txtTopLastMeasure.setBounds(140, 42, 60, 20);
        subPanel.add(txtTopLastMeasure);

        txtBottomLastMeasure = new JTextField(SwingConstants.CENTER);
        txtBottomLastMeasure.setEditable(false);
        txtBottomLastMeasure.setBorder(BorderFactory.createEtchedBorder());
        txtBottomLastMeasure.setBounds(140, 62, 60, 20);
        subPanel.add(txtBottomLastMeasure);

        // Creating the StatusBar.
        // statusBar.setLayout(new BorderLayout());
        statusBar = new JPanel();
        statusBar.setBounds(3, getHeight() - 25, getWidth() - 6, 22);
        statusBar.setLayout(null);
        statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        subPanel.add(statusBar);

        lblDeviceState = new JTextField("Disconnected");
        lblDeviceState.setHorizontalAlignment(SwingConstants.CENTER);
        lblDeviceState.setBackground(Color.WHITE);
        lblDeviceState.setFocusable(false);
        lblDeviceState.setEditable(false);
        lblDeviceState.setSize(172, 18);
        lblDeviceState.setLocation(1, 2);
        statusBar.add(lblDeviceState);

        btnParameterSettings = new JButton();
        btnParameterSettings.addActionListener(e -> btnParameterSettings_actionPerformed(e));
        btnParameterSettings.setIcon(new ImageIcon(PWMsVisu.class.getResource("/resources/btnParameterSettings.Image24x24.png")));
        btnParameterSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnParameterSettings.setMargin(new Insets(0, 0, 0, 0));
        btnParameterSettings.setHorizontalTextPosition(SwingConstants.CENTER);
        btnParameterSettings.setBounds(210, 16, 30, 30);
        subPanel.add(btnParameterSettings);

        btnConnectionSettings = new JButton();
        btnConnectionSettings.addActionListener(e -> btnConnectionSettings_actionPerformed(e));
        btnConnectionSettings.setIcon(new ImageIcon(PWMsVisu.class.getResource("/resources/btnConnectionSettings.Image24x24.png")));
        btnConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
        btnConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
        btnConnectionSettings.setBounds(210, 51, 30, 30);
        subPanel.add(btnConnectionSettings);


    }

    private void btnConnectionSettings_actionPerformed(ActionEvent e) {
        grbConnectionSettings = new ConnectionPanel(device);
        grbConnectionSettings.setTitle("Connection settings " + device.getDeviceName());
        grbConnectionSettings.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window has been opened.
             *
             * @param e
             */
            @Override
            public void windowOpened(WindowEvent e) {
                disabledMainPanel.setEnabled(false);
            }

            /**
             * Invoked when a window has been closed.
             *
             * @param e
             */
            @Override
            public void windowClosed(WindowEvent e) {
                disabledMainPanel.setEnabled(true);
            }
        });
        grbConnectionSettings.setVisible(true);
    }

    private void btnParameterSettings_actionPerformed(ActionEvent e) {
        grbParameterSettings = new ParameterPanel(device);
        grbParameterSettings.setTitle("Parameter settings " + device.getDeviceName());
        grbParameterSettings.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window has been opened.
             *
             * @param e
             */
            @Override
            public void windowOpened(WindowEvent e) {
                disabledMainPanel.setEnabled(false);
            }

            /**
             * Invoked when a window has been closed.
             *
             * @param e
             */
            @Override
            public void windowClosed(WindowEvent e) {
                disabledMainPanel.setEnabled(true);
            }
        });
        grbParameterSettings.setVisible(true);
    }

    PWM getDevice() {
        return device;
    }

    @Override
    public void refreshValues() {
        //System.out.println("EDT? :" + SwingUtilities.isEventDispatchThread());
        if (device.getPlc().isConnected()) {
            txtTopCurrentMeasure.setText(String.valueOf(((S7Data) device.getSensors().get(0)).currentData));
            txtBottomCurrentMeasure.setText(String.valueOf(((S7Data) device.getSensors().get(1)).currentData));
            txtTopLastMeasure.setText(String.valueOf(((S7Data) device.getSensors().get(0)).lastMeasure));
            txtBottomLastMeasure.setText(String.valueOf(((S7Data) device.getSensors().get(1)).lastMeasure));
            lblDeviceState.setText("Connected to " + device.getPlc().getConnectionParameters().getAddress());
            lblDeviceState.setBackground(new Color(0,255,0));
        } else {
            txtTopCurrentMeasure.setText("");
            txtBottomCurrentMeasure.setText("");
            txtTopLastMeasure.setText("");
            txtBottomLastMeasure.setText("");
            lblDeviceState.setText("Disconnected");
            lblDeviceState.setBackground(new Color(255,0,0));
        }
    }
}
