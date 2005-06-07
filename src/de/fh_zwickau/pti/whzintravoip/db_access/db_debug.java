package de.fh_zwickau.pti.whzintravoip.db_access;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import de.fh_zwickau.pti.whzintravoip.rtp_comm.VoIP_Status;

/**
 * <p>Überschrift: </p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class db_debug extends JFrame {
    JPanel contentPane;
    XYLayout xYLayout1 = new XYLayout();
    JScrollPane jScrollPane = new JScrollPane();
    JTextArea jTextArea = new JTextArea();

    public db_debug() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(xYLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setResizable(false);
        setSize(new Dimension(650, 300));
        setTitle("DB Debugging Window");
        setLocation(0, 350);
        jTextArea.setEditable(false);
        jTextArea.setText("");
        jTextArea.setLineWrap(true);
        contentPane.setAlignmentX((float) 100.0);
        jScrollPane.getViewport().add(jTextArea);
        contentPane.add(jScrollPane, new XYConstraints(13, 10, 620, 251));
    }

    public void errMsg(String msg) {
        jTextArea.append("ERROR:\n" + msg + "\n");
    }

    public void infMsg(String msg) {
        jTextArea.append("INFO:\n" + msg + "\n");
    }
}
