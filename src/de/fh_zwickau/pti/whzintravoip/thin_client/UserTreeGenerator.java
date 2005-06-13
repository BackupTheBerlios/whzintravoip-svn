package de.fh_zwickau.pti.whzintravoip.thin_client;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

import java.util.*;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import java.awt.Dimension;
import javax.swing.event.TreeSelectionListener;

/**
 * <p>Überschrift: </p>
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
public class UserTreeGenerator {

    private Vector userVector = null;
    private ThinClientGUI userGUI = null;
    private JTree jTree = null;
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode child = null;
    private DefaultMutableTreeNode subchild = null;
    private DefaultTreeModel treeModel = null;
    private String ipOfChoosenUser = null;

    public UserTreeGenerator() {
    }

    public UserTreeGenerator(Vector userVector, ThinClientGUI userGUI) {
        this.userVector = userVector;
        this.userGUI = userGUI;
    }

    /**
     * Initialisiert den JTree der User, die gerade online sind und bindet
     * ihn in das Hauptfenster ein
     */
    public void initTreeView()
    {
        root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
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
        addUserTreeEntries(userVector);

        // Tree erzeugen
        jTree = new JTree(treeModel);
        jTree.setRootVisible(true);

        // Selectionmode festlegen
        DefaultTreeSelectionModel defaultTreeSelectionModel = new DefaultTreeSelectionModel();
        defaultTreeSelectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setSelectionModel(defaultTreeSelectionModel);

        // Tree einfügen
        userGUI.getTreeViewScrollPane().getViewport().add(new JScrollPane(jTree));
        userGUI.getTreeViewScrollPane().setPreferredSize(new Dimension(300, 150));

        // TreeSelectionListener einfügen
        jTree.addTreeSelectionListener(
                new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                TreePath tp = event.getNewLeadSelectionPath();
                if (tp != null) {
                    userGUI.showUserInfo(getUserInfos(tp.toString()));
                } else {
                    userGUI.showUserInfo("nix selektiert");
                }
            }
        }
        );
    }

    /**
     * löscht den momentan selektierten Eintrag aus dem JTree
     */
    public void removeUserTreeEntry(){
        userGUI.stdOutput("löschen...");
        TreePath tp = jTree.getLeadSelectionPath();
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode)tp.getLastPathComponent();
        if (node != root) {
            TreeNode parent = node.getParent();
            TreeNode[] path = treeModel.getPathToRoot(parent);
            treeModel.removeNodeFromParent(node);
            jTree.setSelectionPath(new TreePath(path));
        }
    }

    /**
     * Entfernt alle Einträge aus dem JTree der User
     */
    public void removeAllEntries(){
        int childCount = root.getChildCount();
        for (int i=childCount; i > 0; i--) {
            DefaultMutableTreeNode child = root.getNextNode();
            treeModel.removeNodeFromParent(child);
        }
    }

    /**
     * Fügt die im übergebenen Vector aufgelisteten User dem JTree hinzu
     *
     * @param uuuuuserVector Vector die Userobjekte, welche momentan online sind
     */
    public void addUserTreeEntries(Vector userVector){
        for(Enumeration el = userVector.elements(); el.hasMoreElements();){
            User user = (User)el.nextElement();
            String name = user.getUserFName() + " " + user.getUserLName() + " (" + user.getUserInitial() + ")";
            child = new DefaultMutableTreeNode(name);
            treeModel.insertNodeInto(child, root, treeModel.getChildCount(root));
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
        Enumeration el = userVector.elements();
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
            ipOfChoosenUser = "123.123.123.123";
            String choosenUser = "Name:\t" + user.getUserFName()
                                 + "\nVorname:\t" + user.getUserLName()
                                 + "\nEmail:\t" + user.getUserMail()
                                 + "\nMatrikel:\t" + user.getUserCompany();
            return choosenUser;
        } else {
            ipOfChoosenUser = null;
            return "Kein User gefunden";
        }
    }

    public String getIPOfChoosenUser(){
        return ipOfChoosenUser;
    }
}
