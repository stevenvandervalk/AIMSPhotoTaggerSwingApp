package aims.photo.uploader.client;

import aims.app.generic.GlobalParamaters;
import aims.app.generic.db.JPAUtil;
import aims.app.generic.dbgui.DatabaseGuiList;
import aims.photo.uploader.client.treednd.NodeMoveTransferHandler;
import aims.photo.uploader.client.treednd.TreeDropTarget;
import aims.photo.uploader.tablemodels.KeywordTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 27/02/2008
 * Time: 11:02:38
 * To change this template use File | Settings | File Templates.
 */
public class KeywordManagerApp extends JFrame {
    private KeywordManager keywordManager;

    public KeywordManagerApp() throws HeadlessException {

        super("Keyword manager");
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
        }

        keywordManager = new KeywordManager();


        setContentPane(keywordManager.getMainPanel());

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        show();


        bind();
        initActions();

    }

    public void bind() {
        KeywordTree t = new KeywordTree();
        keywordManager.getTree1().setModel(new DefaultTreeModel(t.getRootNode()));
        NodeMoveTransferHandler handler = new NodeMoveTransferHandler();
        keywordManager.getTree1().setTransferHandler(handler);
        keywordManager.getTree1().setDragEnabled(true);
        keywordManager.getTree1().setDropTarget(new TreeDropTarget(handler));


    }
    public void initActions() {
        keywordManager.getBtnAdd().addActionListener(new AddKeywordAction());        


    }

    private static void initDb() {
        JPAUtil.getInstance(GlobalParamaters.getDATABASE_CONNECTION_PARAMS());
        GlobalParamaters.setDATABASE(DatabaseGuiList.DATABASE_TIERED_JPA);


    }

    private void addKeyword() {
        TreePath selectedPath = keywordManager.getTree1().getSelectionPath();
        if (selectedPath != null) {
            Object o = selectedPath.getLastPathComponent();
            if (o != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) o;
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode("new_keyword");
                ((DefaultTreeModel) keywordManager.getTree1().getModel()).insertNodeInto(
                        newChild, selectedNode, selectedNode.getChildCount());
                TreePath newPath = selectedPath.pathByAddingChild(newChild);
                keywordManager.getTree1().setSelectionPath(newPath);
                keywordManager.getTree1().startEditingAtPath(newPath);
            }
        }
    }


    private class AddKeywordAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            addKeyword();
        }
    }


    public static void main(String[] args) {
        initDb();

        KeywordManagerApp gui = new KeywordManagerApp();


    }



}
