package de.fh_zwickau.pti.whzintravoip.thin_client;
import de.fh_zwickau.pti.whzintravoip.db_access.*;

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
    private Vector userVector2 = null;
    private ThinClientGUI userGUI = null;
    private JTree jTree = null;
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode child = null;
    private DefaultMutableTreeNode subchild = null;
    private DefaultTreeModel treeModel = null;

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
        for(int i=1; i<=5; ++i){
            String name = "Child - " + i;
            child = new DefaultMutableTreeNode(name);
            root.add(child);
            for(int j=1; j<=3; ++j){
                subchild = new DefaultMutableTreeNode(name + " - " + j);
                child.add(subchild);
            }
        }

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
     * Fügt die im übergebenen Vector aufgelisteten User der JTree hinzu
     *
     * @param uuuuuserVector Vector die Userobjekte, welche momentan online sind
     */
    public void addUserTreeEntries(Vector uuuuuserVector){
        createDummyUsers();
        for(Enumeration el = userVector2.elements(); el.hasMoreElements();){
            User user = (User)el.nextElement();
            String name = user.getFname() + " " + user.getLname();
            child = new DefaultMutableTreeNode(name);
            treeModel.insertNodeInto(child, root, treeModel.getChildCount(root));
        }
    }

    /**
     * legt für Testzwecke einen Dummy-Vector an
     */
    private String getUserInfos(String fullName){
        return "bla";
    }

    /**
     * legt für Testzwecke einen Dummy-Vector an
     * und füllt ihn mit einigen User-Objekten
     */
    private void createDummyUsers(){
        userVector2 = new Vector();
        for (int i=0; i<=5; ++i) {
            User user = new User();
            user.setIdUser(i);
            user.setInitial("initial-" + i);
            user.setMail("mail-" + i);
            user.setMatrikel("matrikel-" + i);
            user.setFname("fname-" + i);
            user.setLname("lname-" + i);
            user.setNick("ni-" + i);
            userVector2.addElement(user);
        }
    }
}
