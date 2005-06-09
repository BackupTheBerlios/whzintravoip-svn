package de.fh_zwickau.pti.whzintravoip.thin_client;

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

    public UserTreeGenerator() {
    }

    public UserTreeGenerator(Vector userVector, ThinClientGUI userGUI) {
        this.userVector = userVector;
        this.userGUI = userGUI;
    }

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
                    userGUI.showUserInfo("Selektiert: " + tp.toString());
                } else {
                    userGUI.showUserInfo("nix selektiert");
                }
            }
        }
        );
    }

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

    public void removeAllEntries(){
        int childCount = root.getChildCount();
        userGUI.stdOutput("" + childCount);
        for (int i=childCount; i > 0; i--) {
            DefaultMutableTreeNode child = root.getNextNode();
            treeModel.removeNodeFromParent(child);
        }
    }

    public void addUserTreeEntry(Vector uuuuuserVector){
        createDummyVector();
        for(Enumeration el = userVector.elements(); el.hasMoreElements();){
            String name = "Child - " + el.nextElement().toString();
            child = new DefaultMutableTreeNode(name);
            treeModel.insertNodeInto(child, root, treeModel.getChildCount(root));
        }
    }

    private void createDummyVector(){
        userVector = new Vector();
        userVector.addElement("a");
        userVector.addElement("b");
        userVector.addElement("c");
        userVector.addElement("d");
        userVector.addElement("e");
    }
}
