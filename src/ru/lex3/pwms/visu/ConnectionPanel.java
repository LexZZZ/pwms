package ru.lex3.pwms.visu;

import ru.lex3.pwms.main.PWM;
import ru.lex3.pwms.main.S7ConnectionParameters;
import ru.lex3.pwms.moka7.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Properties;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import jmri.util.swing.*;


class ConnectionPanel extends JFrame {

    private PWM device;
    private JPanel contentPane;

    private NumberFormat plcPortFormat;

    private JLabel lblAdress0;
    private JLabel lblAdress1;
    private JLabel lblAdress2;
    private JLabel lblAdress3;
    private JLabel lblAdress4;
    private JLabel label;
    private JLabel lblMaxIdleTime;
    private JLabel lblAsyncConnect;
    private JLabel lblAutoConnect;
    private JFormattedTextField txtAdress0;
    private JFormattedTextField txtAdress1;
    private JFormattedTextField txtAdress2;
    private JFormattedTextField txtAdress3;
    private JFormattedTextField txtAdress4;
    private JFormattedTextField txtIdleTimeSpan;
    private JButton btnSaveConnectionSettings;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JCheckBox chkAutoConnect;
    private JCheckBox chkAsyncConnect;

    private String ipAddress;
    //private int plcPort = 102;
    //private int localPort = 0;
    // private int rackID;
    // private int slotID;


    public ConnectionPanel(PWM device) {

        setSize(440, 170);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
        this.setLocation(x, y);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        setLayout(new BorderLayout());
        add(contentPane);
        setResizable(false);

        setIconImage(Toolkit.getDefaultToolkit().getImage(PWMsVisu.class.getResource("/resources/connectionSettings.Image16x16.png")));
        //setIconImage(new darrylbu.icon.AlphaImageIcon(new ImageIcon(PWMsVisu.class.getResource("/ru/lex3/pwms/resources/connectionSettings.Image16x16.png")), 0).getImage());

        contentPane.setSize(getWidth(), getHeight());
        //setVisible(false);
        contentPane.setLayout(null);
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
     /*   contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                "Connection settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));*/

        lblAdress0 = new JLabel("IP");
        lblAdress0.setBounds(10, 15, 139, 14);
        contentPane.add(lblAdress0);

        /*lblAdress1 = new JLabel("PLC Port (usually ISO 102)");
        lblAdress1.setBounds(10, 40, 139, 14);
        contentPane.add(lblAdress1);

        lblAdress2 = new JLabel("Local Port (intern 0)");
        lblAdress2.setBounds(10, 65, 139, 14);
        contentPane.add(lblAdress2);*/

        lblAdress3 = new JLabel("Rack ID");
        //lblAdress3.setBounds(10, 90, 139, 14);
        lblAdress3.setBounds(10, 40, 139, 14);
        contentPane.add(lblAdress3);

        lblAdress4 = new JLabel("Slot ID");
        //lblAdress4.setBounds(10, 115, 139, 14);
        lblAdress4.setBounds(10, 65, 139, 14);
        contentPane.add(lblAdress4);

        // FocusSelect focusSelect = new FocusSelect();

        txtAdress0 = new JFormattedTextField(createFormatter("###.###.###.###"));
        txtAdress0.setFocusLostBehavior(JFormattedTextField.COMMIT);
        //We can't just setText on the formatted text
        //field, since its value will remain set.
        txtAdress0.setValue(device.getPlc().getConnectionParameters().getIpAddres());
        txtAdress0.setColumns(10);
        txtAdress0.setBounds(145, 12, 90, 20);
        txtAdress0.addFocusListener(new FocusSelect());
        contentPane.add(txtAdress0);

      /*  txtAdress1 = new JFormattedTextField(formatter5Digit);
        //We can't just setText on the formatted text
        //field, since its value will remain set.
        txtAdress1.setValue(null);
        txtAdress1.setColumns(10);
        txtAdress1.setBounds(145, 37, 90, 20);
        txtAdress1.addFocusListener(new FocusSelect());
        contentPane.add(txtAdress1);

        txtAdress2 = new JFormattedTextField(formatter2Digit1);
        //We can't just setText on the formatted text
        //field, since its value will remain set.
        txtAdress2.setValue(null);
        txtAdress2.setColumns(10);
        txtAdress2.setBounds(145, 62, 90, 20);
        txtAdress2.addFocusListener(new FocusSelect());
        contentPane.add(txtAdress2);*/

        txtAdress3 = new JFormattedTextField(new NumberFormatterInt(99).getFormatter());
        txtAdress3.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtAdress3.setColumns(10);
        txtAdress3.setText(Integer.toString(((S7ConnectionParameters) device.getPlc().getConnectionParameters()).getRack()));
        //txtAdress3.setBounds(145, 87, 90, 20);
        txtAdress3.setBounds(145, 37, 90, 20);
        txtAdress3.addFocusListener(new FocusSelect());
        contentPane.add(txtAdress3);

        txtAdress4 = new JFormattedTextField(new NumberFormatterInt(99).getFormatter());
        txtAdress4.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtAdress4.setColumns(10);
        txtAdress4.setText(Integer.toString(((S7ConnectionParameters) device.getPlc().getConnectionParameters()).getSlot()));
        txtAdress4.setBounds(145, 62, 90, 20);
        //txtAdress4.setBounds(145, 112, 90, 20);
        txtAdress4.addFocusListener(new FocusSelect());
        contentPane.add(txtAdress4);

        lblAutoConnect = new JLabel("auto connect");
        lblAutoConnect.setBounds(250, 15, 103, 14);
        contentPane.add(lblAutoConnect);

        lblMaxIdleTime = new JLabel("<html>max. idle time until<br/>closing the port</html>");
        lblMaxIdleTime.setBounds(250, 40, 103, 14);
        contentPane.add(lblMaxIdleTime);

        lblAsyncConnect = new JLabel("<html>asynchronous connect</html>");
        lblAsyncConnect.setBounds(250, 65, 103, 14);
        contentPane.add(lblAsyncConnect);

        chkAutoConnect = new JCheckBox("");
        chkAutoConnect.setSelected(device.getPlc().getConnectionParameters().isAutoConnect());
        chkAutoConnect.addItemListener(e -> chkAutoConnect_itemStateChanged(e));
        chkAutoConnect.setBounds(354, 10, 24, 24);
        contentPane.add(chkAutoConnect);

        txtIdleTimeSpan = new JFormattedTextField(new NumberFormatterInt(5000).getFormatter());
        txtIdleTimeSpan.setFocusLostBehavior(JFormattedTextField.COMMIT);
        txtIdleTimeSpan.setColumns(10);
        txtIdleTimeSpan.setText(Long.toString(device.getPlc().getConnectionParameters().getIdleTimeUntilConnect()));
        txtIdleTimeSpan.setBounds(354, 37, 36, 20);
        txtIdleTimeSpan.addFocusListener(new FocusSelect());
        txtIdleTimeSpan.setEnabled(chkAutoConnect.isSelected());
        contentPane.add(txtIdleTimeSpan);

        chkAsyncConnect = new JCheckBox("");
        chkAsyncConnect.setSelected(device.getPlc().getConnectionParameters().isAsyncConnect());
        chkAsyncConnect.addItemListener(e -> chkAsyncConnect_itemStateChanged(e));

        chkAsyncConnect.setBounds(354, 60, 24, 24);
        contentPane.add(chkAsyncConnect);

        label = new JLabel("ms");
        label.setBounds(393, 40, 20, 13);
        contentPane.add(label);

        btnSaveConnectionSettings = new JButton();
        btnSaveConnectionSettings.setToolTipText("<html><center>save</center><center>settings</center></html>");
        btnSaveConnectionSettings.addActionListener(e -> btnSaveConnectionSettings_actionPerformed(e));
        btnSaveConnectionSettings.setIcon(
                new ImageIcon(PWMsVisu.class.getResource("/resources/btnSaveConnectionSettings.Image.png")));
        btnSaveConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnSaveConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
        btnSaveConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
        btnSaveConnectionSettings.setBounds(250, 88, 42, 42);
        contentPane.add(btnSaveConnectionSettings);

        btnConnect = new JButton();
        btnConnect.addActionListener(e -> btnConnect_actionPerformed(e));
        btnConnect.setIcon(new ImageIcon(PWMsVisu.class.getResource("/resources/btnConnect.Image.png")));
        btnConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnConnect.setMargin(new Insets(0, 0, 0, 0));
        btnConnect.setHorizontalTextPosition(SwingConstants.CENTER);
        btnConnect.setBounds(330, 88, 42, 42);
        contentPane.add(btnConnect);

        btnDisconnect = new JButton();
        btnDisconnect.addActionListener(e -> btnDisconnect_actionPerformed(e));
        btnDisconnect.setIcon(new ImageIcon(PWMsVisu.class.getResource("/resources/btnDisconnect.Image.png")));
        btnDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnDisconnect.setMargin(new Insets(0, 0, 0, 0));
        btnDisconnect.setHorizontalTextPosition(SwingConstants.CENTER);
        btnDisconnect.setBounds(380, 88, 42, 42);
        contentPane.add(btnDisconnect);

        setDevice(device);
        setLanguage();
        setAlwaysOnTop(true);

    }

    private void btnConnect_actionPerformed(ActionEvent e) {

        device.getPlc().connectTo();
       /* if (chkAsyncConnect.isSelected()) {
            // asynchronous call
            device.getPlc().connectTo();
        } else {
            // synchronous call (standard)

        }*/
    }

    private void setDevice(PWM device) {
        this.device = device;
    }

    private void btnDisconnect_actionPerformed(ActionEvent e) {
        device.getPlc().disconnect();
    }

    private void btnSaveConnectionSettings_actionPerformed(ActionEvent arg) {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Write Settings in PLCcomCoreExSettings.xml
            Properties p = new Properties();           

           /* if (cmbConnectionType.getSelectedItem() != null) {
                p.setProperty("TypeOfCommunication", cmbConnectionType.getSelectedItem().toString());
            }*/

            p.setProperty("Adress0", txtAdress0.getText());
            //p.setProperty("Adress1", txtAdress1.getText());
            //p.setProperty("Adress2", txtAdress2.getText());
            p.setProperty("Adress3", txtAdress3.getText());
            p.setProperty("Adress4", txtAdress4.getText());
            p.setProperty("txtIdeleTimeSpan", txtIdleTimeSpan.getText());
            p.setProperty("AutoConnect", String.valueOf(chkAutoConnect.isSelected()));
            p.setProperty("chkAsyncConnect", String.valueOf(chkAsyncConnect.isSelected()));

            File file = new File(PWMsVisu.class.getResource("/settings/PWMsSettings.xml").getPath());
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            p.storeToXML(fos, "PWMsVisu Settings", "UTF8");
            fos.close();
            JOptionPane.showMessageDialog(this, PWMsVisu.resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
                            + file.getAbsolutePath(),
                    "", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // btnEditConnectionSettings.setEnabled(true);
            // panAccess.setEnabled(chkAutoConnect.isSelected());
            // panConnectionSettings.setEnabled(false);
            //  panConnection.setEnabled(!chkAutoConnect.isSelected());
            // SetupDevice();
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void chkAutoConnect_itemStateChanged(ItemEvent e) {
        txtIdleTimeSpan.setEnabled(chkAutoConnect.isSelected());
        device.getPlc().getConnectionParameters().setAutoConnect(chkAutoConnect.isSelected());
    }

    private void chkAsyncConnect_itemStateChanged(ItemEvent e) {
        device.getPlc().getConnectionParameters().setAsyncConnect(chkAsyncConnect.isSelected());
    }

    //A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
            formatter.setPlaceholder("192.168.000.000");
        } catch (java.text.ParseException exc) {
            System.err.println("IP address formatter is bad: " + exc.getMessage());
        }
        return formatter;
    }



    void setLanguage() {
        // set controls
        /*contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), PWMsVisu.resources.getString("grbConnection_Text"),
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        */
        //lblAdress1.setText(PWMsVisu.resources.getString("lblPLCPort_Text"));
        //lblAdress2.setText(PWMsVisu.resources.getString("lblLocalPort_Text"));
        lblAdress3.setText(PWMsVisu.resources.getString("lblRack_Text"));
        lblAdress4.setText(PWMsVisu.resources.getString("lblSlot_Text"));
        lblAutoConnect.setText(PWMsVisu.resources.getString("lblAutoConnect2"));
        lblAsyncConnect.setText(PWMsVisu.resources.getString("chkAsyncConnect_TextAsync"));
        lblMaxIdleTime.setText(PWMsVisu.resources.getString("lblmaxIdleTime_Text"));
        btnConnect.setToolTipText(PWMsVisu.resources.getString("btnConnect_Text"));
        btnDisconnect.setToolTipText(PWMsVisu.resources.getString("btnDisconnect_Text"));
        btnSaveConnectionSettings.setToolTipText(PWMsVisu.resources.getString("btnSaveConnectionSettings_Text"));
    }

    /**
     * Called when one of the fields gets the focus so that
     * we can select the focused field.
     */
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

        protected void selectItLater(Component c) {
            if (c instanceof JFormattedTextField) {
                final JFormattedTextField ftf = (JFormattedTextField) c;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ftf.selectAll();
                    }
                });
            }
        }

        /**
         * Invoked when a component loses the keyboard focus.
         *
         * @param e
         */
        @Override
        public void focusLost(FocusEvent e) {
            device.getPlc().getConnectionParameters().setIpAddress(ConnectionPanel.this.txtAdress0.getText().replaceAll(" ", "0"));
            //device.setIpAddress(ConnectionPanel.this.txtAdress1.getText());
            ((S7ConnectionParameters) device.getPlc().getConnectionParameters()).setRack(Integer.parseInt(ConnectionPanel.this.txtAdress3.getText()));
            ((S7ConnectionParameters) device.getPlc().getConnectionParameters()).setSlot(Integer.parseInt(ConnectionPanel.this.txtAdress4.getText()));
            device.getPlc().getConnectionParameters().setIdleTimeUntilConnect(Integer.parseInt((ConnectionPanel.this.txtIdleTimeSpan.getText())));
        }
    }

    private class NumberFormatterInt extends NumberFormatter {
        private NumberFormatter formatter;

        NumberFormatterInt(int maximum) {
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setGroupingUsed(false);
            formatter = new NumberFormatter(nf);
            formatter.setValueClass(Integer.class);
            formatter.setMinimum(0);
            formatter.setMaximum(maximum);
            formatter.setAllowsInvalid(false);
        }

        NumberFormatter getFormatter() {
            return formatter;
        }
    }
}
