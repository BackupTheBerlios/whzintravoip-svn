package de.fh_zwickau.pti.whzintravoip.rtp_comm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import java.awt.*;

/**
 * <p>Title: VoIP_RTP_Client</p>
 *
 * <p>Description: Voice communication over RTP</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author H. Seidel (hs@fh-zwickau.de)
 * @version 1.0
 */
public class VoIP_main extends JFrame {
    JPanel contentPane;
    JTextField jTextField_IP = new JTextField();
    JLabel jLabel_IP = new JLabel();
    JButton jButton_Init = new JButton();
    JButton jButton_Start = new JButton();
    JButton jButton_Stop = new JButton();
    JButton jButton_Close = new JButton();

    // self declarated
    private VoIP_RTP_Interface m_RTP_Interface = new VoIP_RTP_Interface(); // the Interface
    private VoIP_Status m_Status = new VoIP_Status(); // for Debugging Messages
    private boolean m_bDebug = false, m_bError = false;
    // end self
    XYLayout xYLayout1 = new XYLayout();
    JLabel jLabel_Port = new JLabel();
    JTextField jTextField_Port = new JTextField();
    JButton jButton_Error = new JButton();
    JButton jButton_Debug = new JButton();
    public VoIP_main() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // initialize Window
        initWindow();
    }

    public static void main(String args[]) {
        new VoIP_main().setVisible(true);
    }


    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(xYLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setResizable(false);
        setSize(new Dimension(500, 170));
        setTitle("VoIP_RTP_Client");
        jLabel_IP.setText("IP to connect:");
        jButton_Init.setText("Init Connection");
        jButton_Init.addActionListener(new VoIP_main_jButton_Init_actionAdapter(this));
        jButton_Start.setText("Start");
        jButton_Start.addActionListener(new
                                        VoIP_main_jButton_Start_actionAdapter(this));
        jButton_Stop.setText("Stop");
        jButton_Stop.addActionListener(new VoIP_main_jButton_Stop_actionAdapter(this));
        jButton_Close.setText("Close Connection");
        jButton_Close.addActionListener(new
                                        VoIP_main_jButton_Close_actionAdapter(this));
        jLabel_Port.setText("Port:");
        jTextField_Port.setText("");
        jButton_Error.setMnemonic('0');
        jButton_Error.setText("Enable Error Messages");
        jButton_Error.addActionListener(new
                                        VoIP_main_jButton_Error_actionAdapter(this));
        jButton_Debug.setMnemonic('0');
        jButton_Debug.setText("Enable Debugging");
        jButton_Debug.addActionListener(new
                                        VoIP_main_jButton_Debug_actionAdapter(this));
        contentPane.add(jTextField_IP, new XYConstraints(90, 10, 120, -1));
        contentPane.add(jLabel_IP, new XYConstraints(10, 10, 65, -1));
        contentPane.add(jLabel_Port, new XYConstraints(10, 40, 65, -1));
        contentPane.add(jTextField_Port, new XYConstraints(90, 40, 120, -1));
        contentPane.add(jButton_Close, new XYConstraints(230, 40, 120, -1));
        contentPane.add(jButton_Init, new XYConstraints(230, 10, 120, -1));
        contentPane.add(jButton_Debug, new XYConstraints(310, 70, 170, -1));
        contentPane.add(jButton_Error, new XYConstraints(310, 100, 170, -1));
        contentPane.add(jButton_Start, new XYConstraints(230, 70, 70, -1));
        contentPane.add(jButton_Stop, new XYConstraints(230, 100, 70, -1));
    }

// Initial Window actions
    public void initWindow() {
        jButton_Init.setEnabled(true);
        jButton_Start.setEnabled(false);
        jButton_Stop.setEnabled(false);
        jButton_Close.setEnabled(false);
        jButton_Error.setEnabled(false);
    }

// buttons klicked actions
    public void jButton_Init_actionPerformed(ActionEvent e) {
        String ip = null, port = null;
        // get the Input
        if (jTextField_IP.getText().length() != 0) {
            ip = jTextField_IP.getText();
        }
        if (jTextField_Port.getText().length() != 0) {
            port = jTextField_Port.getText();
        }
        m_RTP_Interface.initRtpSession(ip, port);
        jButton_Init.setEnabled(false);
        jButton_Start.setEnabled(true);
        jButton_Stop.setEnabled(false);
        jButton_Close.setEnabled(true);

    }

    public void jButton_Close_actionPerformed(ActionEvent e) {
        try {
            m_RTP_Interface.closeRtpSession();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
        jButton_Init.setEnabled(true);
        jButton_Start.setEnabled(false);
        jButton_Stop.setEnabled(false);
        jButton_Close.setEnabled(false);

    }

    public void jButton_Start_actionPerformed(ActionEvent e) {
        try {
            m_RTP_Interface.startRtpSession();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
        jButton_Init.setEnabled(false);
        jButton_Start.setEnabled(false);
        jButton_Stop.setEnabled(true);
        jButton_Close.setEnabled(false);
    }

    public void jButton_Stop_actionPerformed(ActionEvent e) {
        try {
            m_RTP_Interface.stopRtpSession();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
        jButton_Init.setEnabled(false);
        jButton_Start.setEnabled(true);
        jButton_Stop.setEnabled(false);
        jButton_Close.setEnabled(true);
    }

    public void jButton_Error_actionPerformed(ActionEvent e) {
        if (m_bError) {
            m_RTP_Interface.DebugErrorMessages(false);
            jButton_Error.setText("Enable Error Messages");
            m_bError = false;
        } else {
            m_RTP_Interface.DebugErrorMessages(true);
            jButton_Error.setText("Disable Error Messages");
            m_bError = true;
        }
    }

    public void jButton_Debug_actionPerformed(ActionEvent e) {
        if (m_bDebug) {
            // disable Debugging
            m_RTP_Interface.disableDebugging();
            jButton_Error.setEnabled(false);
            m_bDebug = false;
            jButton_Debug.setText("Enable Debugging");
        } else {
            m_RTP_Interface.enableDebugging();
            jButton_Error.setEnabled(true);
            m_bDebug = true;
            jButton_Debug.setText("Disable Debugging");
        }
    }
}


class VoIP_main_jButton_Debug_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Debug_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Debug_actionPerformed(e);
    }
}


class VoIP_main_jButton_Error_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Error_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Error_actionPerformed(e);
    }
}


class VoIP_main_jButton_Stop_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Stop_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Stop_actionPerformed(e);
    }
}


class VoIP_main_jButton_Start_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Start_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Start_actionPerformed(e);
    }
}


class VoIP_main_jButton_Close_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Close_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Close_actionPerformed(e);
    }
}


class VoIP_main_jButton_Init_actionAdapter implements ActionListener {
    private VoIP_main adaptee;
    VoIP_main_jButton_Init_actionAdapter(VoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_Init_actionPerformed(e);
    }
}
