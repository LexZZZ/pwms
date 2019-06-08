package ru.lex3.pwms.visu;

import ru.lex3.pwms.main.PWM;
import ru.lex3.pwms.moka7.*;
import ru.lex3.pwms.visu.disabledjpanel.*;
import ru.lex3.pwms.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PWMsVisu extends JFrame {

    private S7Client Device = new S7Client();
    static ResourceBundle resources = ResourceBundle.getBundle("ru.lex3.pwms.resources.resources", new UTF8Control());
    static int CountOpenDialogs = 0;
    ArrayList<WorkPanel> workPanels;

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private JComboBox<Locale> cmbLanguage;
    private JLabel lblLanguage;

    private JButton btnEditConnectionSettings;
    private JButton btnClose;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PWMsVisu frame = new PWMsVisu();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public PWMsVisu() {
        // set global lock and feel platform independent
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            @SuppressWarnings("rawtypes")
            java.util.Enumeration keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11));
                }
            }

            // UIManager.setLookAndFeel(
            // "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        setTitle("PWMsVisu welding mashines observer");

        setIconImage(Toolkit.getDefaultToolkit().getImage(PWMsVisu.class.getResource("/ru/lex3/pwms/resources/soldar.png")));

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 850);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent arg0) {
                formWindowOpened(arg0);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }

        });

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        btnEditConnectionSettings = new JButton();
        btnEditConnectionSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnEditConnectionSettings_actionPerformed(e);
            }
        });

        btnEditConnectionSettings.setIcon(
                new ImageIcon(PWMsVisu.class.getResource("/ru/lex3/pwms/resources/btnEditConnectionSettings.Image.png")));
        btnEditConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnEditConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
        btnEditConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
        btnEditConnectionSettings.setBounds(0, 730, 42, 42);
        contentPane.add(btnEditConnectionSettings);

        lblLanguage = new JLabel("\r\nlanguage");
        lblLanguage.setBounds(42, 741, 60, 20);
        contentPane.add(lblLanguage);

        cmbLanguage = new JComboBox<Locale>();
        cmbLanguage.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                cmbLanguage_itemStateChanged(e);
            }
        });
        cmbLanguage.setBounds(102, 741, 91, 20);
        contentPane.add(cmbLanguage);

        btnClose = new JButton("<html><center>close</center></html>");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnClose_actionPerformed(e);
            }
        });
        btnClose.setIcon(new ImageIcon(PWMsVisu.class.getResource("/ru/lex3/pwms/resources/btnClose.Image.png")));
        btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
        btnClose.setBounds(1122, 704, 68, 68);
        contentPane.add(btnClose);
        contentPane.setBackground(new Color(84, 84, 84));


        workPanels = new ArrayList<>();
        int x = 0, y = 0;
        for (int i = 0; i < 20; i++) {
            workPanels.add(new WorkPanel(new PWM(2), "A" + i, x, y));
            if (y + 113 > 680) {
                x = x + 257;
                y = 0;
            } else
                y = y + 115;
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        for (WorkPanel workPanel : workPanels) {
            add(workPanel);
            executor.execute(workPanel.getDevice());
        }
    }

    private void formWindowOpened(WindowEvent arg0) {
        // Set location to center screen
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
        this.setLocation(x, y);

        // set language combobox
        Locale[] allLocale = new Locale[2];
        if (Locale.getDefault().getLanguage().toString().toLowerCase() == "ru") {
            allLocale[0] = Locale.getDefault();
            allLocale[1] = new Locale("en", "US");

        } else if (Locale.getDefault().getLanguage().toString().toLowerCase() == "en") {
            allLocale[0] = Locale.getDefault();
            allLocale[1] = new Locale("ru", "RU");

        } else {
            allLocale[0] = new Locale("en", "US");
            allLocale[1] = new Locale("ru", "RU");
        }
        cmbLanguage.setModel(new DefaultComboBoxModel<Locale>(allLocale));

        // set UI language
        try {
            SetLanguage((Locale) cmbLanguage.getSelectedItem());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        }

        loadSettingsFromFile();
    }

    private void formWindowClosing(WindowEvent e) {
        if (Device != null) {
            Device.disconnect();
        }
    }

    private void btnClose_actionPerformed(ActionEvent e) {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (Device != null) {
                // unload and dispose all objects
                Device.disconnect();
                Device = null;
            }

        } finally {
            this.setCursor(Cursor.getDefaultCursor());
            System.exit(0);
        }
    }


    private void SetLanguage(Locale locale) {
        // set UI-Controls with actual Locale information
        // init ResourceManager

        // set thread locale
        Locale.setDefault(locale);

        ResourceBundle.clearCache();
        resources = ResourceBundle.getBundle("ru.lex3.pwms.resources.resources", locale, new UTF8Control());
        // set controls
        this.setTitle(resources.getString("main_Text"));
        this.lblLanguage.setText(resources.getString("lblLanguage_Text"));
        this.btnClose.setText(resources.getString("btnClose_Text"));
        this.btnEditConnectionSettings.setToolTipText(resources.getString("btnEditConnectionSettings_Text"));


    }

    private void cmbLanguage_itemStateChanged(ItemEvent e) {
        // set UI language
        try {
            SetLanguage((Locale) cmbLanguage.getSelectedItem());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSettingsFromFile() {
        Properties p = new Properties();

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // open file
            FileInputStream fis = new FileInputStream(PWMsVisu.class.getResource("/ru/lex3/pwms/settings/PWMsSettings.xml").getFile());
            p.loadFromXML(fis);

            // load saved settiungs from PLCcomModbusSlaveSettings.xml
/*
            if (p.containsKey("Adress0")) {
                txtAdress0.setText(p.getProperty("Adress0"));
            }

            if (p.containsKey("Adress1")) {
                txtAdress1.setText(p.getProperty("Adress1"));
            }

            if (p.containsKey("Adress2")) {
                txtAdress2.setText(p.getProperty("Adress2"));
            }

            if (p.containsKey("Adress3")) {
                txtAdress3.setText(p.getProperty("Adress3"));
            }

            if (p.containsKey("Adress4")) {
                txtAdress4.setText(p.getProperty("Adress4"));
            }

            if (p.containsKey("chkAsyncConnect")) {
                chkAsyncConnect.setSelected(Boolean.valueOf(p.getProperty("chkAsyncConnect")));
            }

            if (p.containsKey("AutoConnect")) {
                chkAutoConnect.setSelected(Boolean.valueOf(p.getProperty("AutoConnect")));
            }
*/
        } catch (FileNotFoundException ignore) {

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
        } finally {
            //  panConnection.setEnabled(!chkAutoConnect.isSelected());
            //  panAccess.setEnabled(chkAutoConnect.isSelected());
            setCursor(Cursor.getDefaultCursor());
        }

    }


    private void btnEditConnectionSettings_actionPerformed(ActionEvent e) {
        try {
            if (CountOpenDialogs > 0) {
                JOptionPane.showMessageDialog(this, resources.getString("to_many_windows"), "",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Device.disconnect();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}
