package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: This class generates the tree with the online users and
 * provides methods to modify this tree.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann <ys@fh-zwickau.de>
 * @version 0.1.0
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public class UserTreeGenerator {

    private Vector m_UserVector = null;
    private ThinClient m_ThinClient = null;
    private ThinClientGUI m_UserGUI = null;
    private JTree m_JTree = null;
    private DefaultMutableTreeNode m_Root = null;
    private DefaultMutableTreeNode m_Child = null;
    private DefaultMutableTreeNode m_Subchild = null;
    private DefaultTreeModel m_TreeModel = null;
    private String m_sIPOfChoosenUser = null;

    public UserTreeGenerator(Vector userVector, ThinClient client,
                             ThinClientGUI userGUI) {
        this.m_UserVector = userVector;
        this.m_ThinClient = client;
        this.m_UserGUI = userGUI;
    }

    /**
     * Initializes the JTree of the users which are online at the moment and
     * connects the tree with the main window.
     */
    public void initTreeView() {
        m_Root = new DefaultMutableTreeNode("erreichbare User:");
        m_TreeModel = new DefaultTreeModel(m_Root);

        addUserTreeEntries(m_UserVector);

        // create tree
        m_JTree = new JTree(m_TreeModel);
        m_JTree.setRootVisible(true);

        // define selection mode
        DefaultTreeSelectionModel defaultTreeSelectionModel = new
                DefaultTreeSelectionModel();
        defaultTreeSelectionModel.setSelectionMode(DefaultTreeSelectionModel.
                SINGLE_TREE_SELECTION);
        m_JTree.setSelectionModel(defaultTreeSelectionModel);

        // insert tree
        JScrollPane jScrollPane = new JScrollPane(m_JTree);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.
                                                 HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.
                                               VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setWheelScrollingEnabled(true);

        m_UserGUI.getGUIContentPane().add(jScrollPane,
                                          new GridBagConstraints(0, 1, 2, 1,
                0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 2, 2));

        // insert TreeSelectionListener
        m_JTree.addTreeSelectionListener(
                new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                TreePath tp = event.getNewLeadSelectionPath();
                if (tp != null) {
                    m_ThinClient.showUserInfo(getUserInfos(tp.toString()));
                } else {
                    m_ThinClient.showUserInfo("nix selektiert");
                }
            }
        }
        );
    }

    /**
     * Remove the actually selected user in the tree. This method was only for
     * testing the JTree, actually it is not used.
     */
    public void removeUserTreeEntry() {
        m_ThinClient.stdOutput("Löschen des angeklickten Users aus dem Tree");
        TreePath tp = m_JTree.getLeadSelectionPath();
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode) tp.getLastPathComponent();
        if (node != m_Root) {
            TreeNode parent = node.getParent();
            TreeNode[] path = m_TreeModel.getPathToRoot(parent);
            m_TreeModel.removeNodeFromParent(node);
            m_JTree.setSelectionPath(new TreePath(path));
        }
    }

    /**
     * Remove all entries on the JTree which is the whole user list.
     */
    public void removeAllEntries() {
        int childCount = m_Root.getChildCount();
        for (int i = childCount; i > 0; i--) {
            DefaultMutableTreeNode child = m_Root.getNextNode();
            m_TreeModel.removeNodeFromParent(child);
        }
    }

    /**
     * Insert the users from the given user vector into the JTree. This is not
     * correct at all, so only some data of this user objects where put into the
     * tree like the name and the IP. The user objects are still on the vector.
     *
     * @param userVector Vector - The user objects to insert into the tree.
     */
    public void addUserTreeEntries(Vector userVector) {
        for (Enumeration el = userVector.elements(); el.hasMoreElements(); ) {
            User user = (User) el.nextElement();
            // filter out myself (only insert the others into the tree, not me)
            if (!user.getUserIP().equals(m_ThinClient.getStoredIP())) {
                String name = user.getUserFName()
                              + " "
                              + user.getUserLName()
                              + " ("
                              + user.getUserInitial()
                              + ")";
                m_Child = new DefaultMutableTreeNode(name);
                m_TreeModel.insertNodeInto(m_Child, m_Root,
                                           m_TreeModel.getChildCount(m_Root));
            }
        }
    }

    /**
     * Expands the JTree if it is collapsed.
     */
    private void expandJTree() {
        TreePath tp = m_JTree.getPathForLocation(0, 0);
        m_JTree.expandPath(tp);
    }

    /**
     * Set a complete new user list (JTree). At first the old users where
     * removed from the list, then the new users from the given vector are
     * added to the tree. After that the tree will be expanded so the new list
     * is visible at the main window.
     *
     * @param userVector Vector - The vector with the users.
     */
    public void setNewUserList(Vector userVector) {
        removeAllEntries();
        this.m_UserVector = userVector;
        addUserTreeEntries(userVector);
        expandJTree();
    }

    /**
     * Reads the user data for the user, which is selected on the JTree. Then
     * this user will be searched on the user vector and the necessary data are
     * displayed in the TextArea on the right of the main window. <br>
     * The name is given in the following form: "Max Mustermann (MaMu)". The
     * method extracts the string between the brackets and search for this
     * shortcut on the user vector.
     *
     * @param fullName String - the activated entry on the JTree
     * @return String - the complete info string
     */
    private String getUserInfos(String fullName) {
        User user = null;
        Enumeration el = m_UserVector.elements();
        while (el.hasMoreElements()) {
            User dummyUser = (User) el.nextElement();
            StringTokenizer tokenizer = new StringTokenizer(fullName, "()");
            String loginName = null;
            try {
                // erstes Token ist der volle Name
                loginName = tokenizer.nextToken();
                // dieses Token ist das Login-Kürzel
                loginName = tokenizer.nextToken();
            } catch (NoSuchElementException ex) {
            }
            if (loginName.equals(dummyUser.getUserInitial())) {
                user = dummyUser;
                break;
            }
        }
        if (user != null) {
            m_sIPOfChoosenUser = user.getUserIP();
            String choosenUser = "Name:\t" + user.getUserFName()
                                 + "\nVorname:\t" + user.getUserLName()
                                 + "\nEmail:\t" + user.getUserMail()
                                 + "\nMatrikel:\t" + user.getUserCompany()
                                 + "\naktuelle IP:\t" + user.getUserIP();
            return choosenUser;
        } else {
            m_sIPOfChoosenUser = null;
            return "Kein User gefunden";
        }
    }

    /**
     * Extracts out of the user vector the name of a user by the given IP
     *
     * @param userIP String - the IP to search for on the user vector
     * @return String - the found name
     */
    public String getUserName(String userIP) {
        User user = null;
        Enumeration el = m_UserVector.elements();
        while (el.hasMoreElements()) {
            User dummyUser = (User) el.nextElement();
            if (userIP.equals(dummyUser.getUserIP())) {
                user = dummyUser;
                break;
            }
        }
        if (user != null) {
            String name = user.getUserFName() + " " + user.getUserLName();
            return name;
        } else {
            return "Unbekannter User";
        }
    }

    /**
     * Returns the IP of the activated user in the JTree
     *
     * @return String - IP
     */
    public String getIPOfChoosenUser() {
        return m_sIPOfChoosenUser;
    }
}
