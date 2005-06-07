package de.fh_zwickau.pti.whzintravoip.db_access;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class db_test extends JFrame {
    JPanel contentPane;
    XYLayout xYLayout1 = new XYLayout();
    JButton jButton_addUser = new JButton();
    JButton jButton_addBuddy = new JButton();
    private static db_interface inter = null;
    private static db_debug debug = null;

    //properties
    private static String init = "user.initial";
    private static String mail = "user.mail";
    private static String matri = "user.matrikel";
    private static String fname = "user.fname";
    private static String lname = "user.lname";
    private static String nick = "user.nick";
    JTextField jTextField_initial = new JTextField();
    JTextField jTextField_mail = new JTextField();
    JTextField jTextField_matrikel = new JTextField();
    JTextField jTextField_fname = new JTextField();
    JTextField jTextField_lname = new JTextField();
    JTextField jTextField_nick = new JTextField();
    JLabel jLabel_initial = new JLabel();
    JLabel jLabel_mail = new JLabel();
    JLabel jLabel_matrikel = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel_lname = new JLabel();
    JLabel jLabel_nick = new JLabel();
    JButton jButton_getAllUser = new JButton();
    JLabel jLabel_user = new JLabel();
    JLabel jLabel_buddy = new JLabel();
    JTextField jTextField_userInitial = new JTextField();
    JTextField jTextField_buddyInitial = new JTextField();
    JButton jButton_getBuddys = new JButton();
    JButton jButton_delUser = new JButton();
    JButton jButton_delBuddy = new JButton();


    public db_test() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(xYLayout1);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        setSize(new Dimension(500, 320));
        setTitle("DB-Test");
        jButton_addUser.setActionCommand("addUser");
        jButton_addUser.setText("addUser");
        jButton_addUser.addActionListener(new
                                          db_test_jButton_addUser_actionAdapter(this));
        jButton_addBuddy.setText("addBuddy");
        jButton_addBuddy.addActionListener(new
                                           db_test_jButton_addBuddy_actionAdapter(this));
        jTextField_initial.setText("");
        jTextField_mail.setText("");
        jTextField_matrikel.setText("");
        jTextField_fname.setText("");
        jTextField_lname.setText("");
        jTextField_nick.setText("");
        jLabel_initial.setText("Initial:");
        jLabel_mail.setText("Mail:");
        jLabel_matrikel.setText("Matrikel:");
        jLabel4.setText("Fname:");
        jLabel_lname.setText("Lname:");
        jLabel_nick.setText("Nick:");
        jButton_getAllUser.setActionCommand("getAllUser");
        jButton_getAllUser.setText("getAllUser");
        jButton_getAllUser.addActionListener(new
                                             db_test_jButton_getAllUser_actionAdapter(this));
        jLabel_user.setText("User Initial:");
        jLabel_buddy.setText("Buddy Initial:");
        jTextField_userInitial.setText("");
        jButton_getBuddys.setActionCommand("getBuddys");
        jButton_getBuddys.setText("getBuddys");
        jButton_getBuddys.addActionListener(new
                                            db_test_jButton_getBuddys_actionAdapter(this));
        jButton_delUser.setActionCommand("delUser");
        jButton_delUser.setText("delUser");
        jButton_delUser.addActionListener(new
                                          db_test_jButton_delUser_actionAdapter(this));
        jButton_delBuddy.setActionCommand("delBuddy");
        jButton_delBuddy.setText("delBuddy");
        jButton_delBuddy.addActionListener(new
                                           db_test_jButton_delBuddy_actionAdapter(this));
        contentPane.add(jLabel_initial, new XYConstraints(10, 10, 70, -1));
        contentPane.add(jLabel_mail, new XYConstraints(10, 40, 70, -1));
        contentPane.add(jLabel_matrikel, new XYConstraints(10, 70, 70, -1));
        contentPane.add(jLabel4, new XYConstraints(10, 100, 70, -1));
        contentPane.add(jLabel_lname, new XYConstraints(10, 130, 70, -1));
        contentPane.add(jLabel_nick, new XYConstraints(10, 160, 70, -1));
        contentPane.add(jTextField_initial, new XYConstraints(100, 10, 170, -1));
        contentPane.add(jTextField_mail, new XYConstraints(100, 40, 170, -1));
        contentPane.add(jTextField_matrikel, new XYConstraints(100, 70, 170, -1));
        contentPane.add(jTextField_fname, new XYConstraints(100, 100, 170, -1));
        contentPane.add(jTextField_lname, new XYConstraints(100, 130, 170, -1));
        contentPane.add(jTextField_nick, new XYConstraints(100, 160, 170, -1));
        contentPane.add(jButton_addUser, new XYConstraints(300, 10, 100, -1));
        contentPane.add(jButton_getAllUser, new XYConstraints(300, 40, 100, -1));
        contentPane.add(jLabel_user, new XYConstraints(10, 210, 70, -1));
        contentPane.add(jLabel_buddy, new XYConstraints(10, 240, 70, -1));
        contentPane.add(jTextField_userInitial,
                        new XYConstraints(100, 210, 50, -1));
        contentPane.add(jTextField_buddyInitial,
                        new XYConstraints(100, 240, 50, -1));
        contentPane.add(jButton_addBuddy, new XYConstraints(200, 210, 100, -1));
        contentPane.add(jButton_getBuddys, new XYConstraints(200, 240, 100, -1));
        contentPane.add(jButton_delUser, new XYConstraints(320, 210, 100, -1));
        contentPane.add(jButton_delBuddy, new XYConstraints(320, 240, 100, -1));
    }

    public static void main(String[] args) {
        new db_test().setVisible(true);
        inter = new db_interface();
        debug = new db_debug();
        debug.setVisible(true);

    }

    public void jButton_addUser_actionPerformed(ActionEvent e) {
        Properties prop = new Properties();
        prop.setProperty(init, jTextField_initial.getText());
        prop.setProperty(mail, jTextField_mail.getText());
        prop.setProperty(matri, jTextField_matrikel.getText());
        prop.setProperty(fname, jTextField_fname.getText());
        prop.setProperty(lname, jTextField_lname.getText());
        prop.setProperty(nick, jTextField_nick.getText());
        inter.addUser(prop);
    }

    public void jButton_getAllUser_actionPerformed(ActionEvent e) {
        java.util.List users = inter.getAllUser();
        for (int i = 0; i < users.size(); i++) {
            User us = (User) users.get(i);
            debug.infMsg(us.getInitial() + " " + us.getMail() + " " +
                         us.getMatrikel() + " " + us.getFname() + " " +
                         us.getLname() + " " + us.getNick());
        }

    }

    public void jButton_addBuddy_actionPerformed(ActionEvent e) {
        inter.addBuddy(jTextField_userInitial.getText(), jTextField_buddyInitial.getText());
    }

    public void jButton_getBuddys_actionPerformed(ActionEvent e) {
        java.util.List users = inter.getBuddys(jTextField_userInitial.getText());
        for (int i = 0; i < users.size(); i++) {
            User us = (User) users.get(i);
            debug.infMsg(us.getInitial() + " " + us.getMail() + " " +
                         us.getMatrikel() + " " + us.getFname() + " " +
                         us.getLname() + " " + us.getNick());
        }

    }

    public void jButton_delBuddy_actionPerformed(ActionEvent e) {
        inter.delBuddy(jTextField_userInitial.getText(), jTextField_buddyInitial.getText());
    }

    public void jButton_delUser_actionPerformed(ActionEvent e) {
        inter.delUser(jTextField_userInitial.getText());
    }
}


class db_test_jButton_delUser_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_delUser_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_delUser_actionPerformed(e);
    }
}


class db_test_jButton_delBuddy_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_delBuddy_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_delBuddy_actionPerformed(e);
    }
}


class db_test_jButton_getBuddys_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_getBuddys_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_getBuddys_actionPerformed(e);
    }
}


class db_test_jButton_addBuddy_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_addBuddy_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_addBuddy_actionPerformed(e);
    }
}


class db_test_jButton_getAllUser_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_getAllUser_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_getAllUser_actionPerformed(e);
    }
}


class db_test_jButton_addUser_actionAdapter implements ActionListener {
    private db_test adaptee;
    db_test_jButton_addUser_actionAdapter(db_test adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_addUser_actionPerformed(e);
    }
}
