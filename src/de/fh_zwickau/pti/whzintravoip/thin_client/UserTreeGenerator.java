package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Überschrift: WHZIntraVoIP</p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann yves.schumann@fh-zwickau.de
 * @version 0.0.1
 */

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public class UserTreeGenerator {

    private Vector m_UserVector = null;
    private ThinClientGUI m_UserGUI = null;
    private JTree m_JTree = null;
    private DefaultMutableTreeNode m_Root = null;
    private DefaultMutableTreeNode m_Child = null;
    private DefaultMutableTreeNode m_Subchild = null;
    private DefaultTreeModel m_TreeModel = null;
    private String m_sIPOfChoosenUser = null;

    public UserTreeGenerator() {
    }

    public UserTreeGenerator(Vector userVector, ThinClientGUI userGUI) {
        this.m_UserVector = userVector;
        this.m_UserGUI = userGUI;
    }

    /**
     * Initialisiert den JTree der User, die gerade online sind und bindet
     * ihn in das Hauptfenster ein
     */
    public void initTreeView()
    {
        m_Root = new DefaultMutableTreeNode("Root");
        m_TreeModel = new DefaultTreeModel(m_Root);
        /**
        for(int i=1; i<=5; ++i){
            String name = "Child - " + i;
            child = new DefaultMutableTreeNode(name);
            root.add(child);
            for(int j=1; j<=3; ++j){
                subchild = new DefaultMutableTreeNode(name + " - " + j);
                child.add(subchild);
            }
        }
        */
        addUserTreeEntries(m_UserVector);

        // Tree erzeugen
        m_JTree = new JTree(m_TreeModel);
        m_JTree.setRootVisible(true);

        // Selectionmode festlegen
        DefaultTreeSelectionModel defaultTreeSelectionModel = new DefaultTreeSelectionModel();
        defaultTreeSelectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        m_JTree.setSelectionModel(defaultTreeSelectionModel);

        // Tree einfügen
        m_UserGUI.getTreeViewScrollPane().getViewport().add(new JScrollPane(m_JTree));
        m_UserGUI.getTreeViewScrollPane().setPreferredSize(new Dimension(300, 150));

        // TreeSelectionListener einfügen
        m_JTree.addTreeSelectionListener(
                new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                TreePath tp = event.getNewLeadSelectionPath();
                if (tp != null) {
                    m_UserGUI.showUserInfo(getUserInfos(tp.toString()));
                } else {
                    m_UserGUI.showUserInfo("nix selektiert");
                }
            }
        }
        );
    }

    /**
     * löscht den momentan selektierten Eintrag aus dem JTree
     */
    public void removeUserTreeEntry(){
        m_UserGUI.stdOutput("löschen...");
        TreePath tp = m_JTree.getLeadSelectionPath();
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode)tp.getLastPathComponent();
        if (node != m_Root) {
            TreeNode parent = node.getParent();
            TreeNode[] path = m_TreeModel.getPathToRoot(parent);
            m_TreeModel.removeNodeFromParent(node);
            m_JTree.setSelectionPath(new TreePath(path));
        }
    }

    /**
     * Entfernt alle Einträge aus dem JTree der User
     */
    public void removeAllEntries(){
        int childCount = m_Root.getChildCount();
        for (int i=childCount; i > 0; i--) {
            DefaultMutableTreeNode child = m_Root.getNextNode();
            m_TreeModel.removeNodeFromParent(child);
        }
    }

    /**
     * Fügt die im übergebenen Vector aufgelisteten User dem JTree hinzu
     *
     * @param userVector Vector die Userobjekte, welche momentan online sind
     */
    public void addUserTreeEntries(Vector userVector){
        for(Enumeration el = userVector.elements(); el.hasMoreElements();){
            User user = (User)el.nextElement();
            String name = user.getUserFName() + " " + user.getUserLName() + " (" + user.getUserInitial() + ")";
            m_Child = new DefaultMutableTreeNode(name);
            m_TreeModel.insertNodeInto(m_Child, m_Root, m_TreeModel.getChildCount(m_Root));
        }
    }

    /**
     * Ließt die Userdaten aus dem UserVector für den User aus, welcher im JTree
     * selektiert wurde. Daraus wird das Login-Kürzel extrahiert, dieses im
     * UserVector gesucht und die entsprechenden Daten werden angezeigt.
     * Der übergebene Name setzt sich wie folgt zusammen:
     * "Max Mustermann (MaMu)". Dabei wird der Ausdruck zwischen den Klammern
     * extrahiert und ausgewertet.
     *
     * @param fullName String - der angeklickte Eintrag aus dem JTree
     * @return String - der komplette Info-String
     */
    private String getUserInfos(String fullName){
        User user = null;
        Enumeration el = m_UserVector.elements();
        while(el.hasMoreElements()){
            User dummyUser = (User)el.nextElement();
            StringTokenizer tokenizer = new StringTokenizer(fullName, "()");
            String loginName = null;
            try{
                // erstes Token ist der volle Name
                loginName = tokenizer.nextToken();
                // dieses Token ist das Login-Kürzel
                loginName = tokenizer.nextToken();
            }catch(NoSuchElementException ex){
            }
            if (loginName.equals(dummyUser.getUserInitial())) {
                user = dummyUser;
                break;
            }
        }
        if (user != null) {
            m_sIPOfChoosenUser = "123.123.123.123";
            String choosenUser = "Name:\t" + user.getUserFName()
                                 + "\nVorname:\t" + user.getUserLName()
                                 + "\nEmail:\t" + user.getUserMail()
                                 + "\nMatrikel:\t" + user.getUserCompany();
            return choosenUser;
        } else {
            m_sIPOfChoosenUser = null;
            return "Kein User gefunden";
        }
    }

    public String getIPOfChoosenUser(){
        return m_sIPOfChoosenUser;
    }
}
