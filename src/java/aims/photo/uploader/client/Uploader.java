package aims.photo.uploader.client;

import aims.app.generic.GlobalParamaters;
import aims.app.generic.dbgui.AimsCustomBinders;
import aims.app.generic.dbgui.AimsJTable;
import aims.app.generic.dbgui.DatabaseGuiListNavigatorListener;
import aims.app.generic.gui.AimsSwingErrorHandler;
import aims.app.generic.gui.SwingList;
import aims.app.generic.gui.SwingPleaseWait;
import aims.app.generic.logger.LoggerFactory;
import aims.photo.messaging.GotoDirectory;
import aims.photo.messaging.GotoDirectoryListener;
import aims.photo.uploader.presentationmodels.*;
import aims.photo.uploader.tablemodels.KeywordTableModel;
import aims.photo.uploader.tablemodels.KeywordTree;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 31/01/2007
 * Time: 15:21:49
 * To change this template use File | Settings | File Templates.
 */

public class Uploader implements GotoDirectoryListener {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton applyButton;
    private JTable imageMeta;
    private JButton selectNoneButton;
    private JTabbedPane tabbedPane1;
    private JTextField edPhotographer;
    private JTextField edLocation;
    private JTextField edLatitude;
    private JTextField edLongitude;
    private JTextArea taCaption;
    private JTree trKeywords;
    private JButton selectAllButton;
    private ImageBrowser imageBrowser;
    private JButton setDefaultsAltDButton;
    private JTextField edDefPhotographer;
    private JTextField edDefLocation;
    private JTextField edDefLatitude;
    private JTextField edDefLongitude;
    private JTextArea taDefCaption;
    private AimsJTable tblKeywords;
    private JTextField edDepth;
    private JTextField edDefDepth;
    private AimsJTable tblDefKeywords;
    private JTextArea taKeywordString;
    private JPanel tpKeywords;
    private JTextArea taDefKeywordString;
    private JTextField edCopyright;
    private JTextField edDefCopyright;
    private JTextField edAddKeyword;
    private JButton addKeyWordButton;
    private JTree trDefKeywords;
    private JButton copyMetadataButton;
    private JButton pasteMetadataButton;
    private JButton btnDelete;
    private JButton btnDeleteDef;
    private JCheckBox showLongNamesCheckBox;
    private JCheckBox defShowLongNamesCheckBox;
    private JButton findKeywordsButton;
    private JTextField tfSearch;
    private JButton btnSearch;
    private KeywordTree keywordTree;
    private DefaultTreeModel treeModel;


    private PhotoUploaderPM photoUploaderPM;
    private DefaultPhotoUploaderPM defaultPhotoUploaderPM;
    private SimplePhotoUploaderPM clipboard;
    private PresentationModel model;
    private PresentationModel defaultModel;
    private static final String VERSION = "0.3.0";

    private KeywordParserPhotoUploader keywordParser;
    private int listIndex = 0;
    private String lastSearch = null;


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Uploader() {


        LoggerFactory.getInstance().getLogger().info("Photo tagger Version: " + VERSION);
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
        }



        $$$setupUI$$$();


        frame = new JFrame("Data Centre Image Tagger " + VERSION);

        frame.setContentPane(getMainPanel());

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new ThisWindowAdapter());


        bind();
        GlobalParamaters.getPleaseWait().hide();

        frame.show();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imageBrowser.getImageList().addListSelectionListener(new PhotoSelectionListener(imageBrowser.getImageList(), photoUploaderPM));

        initActions();

        GotoDirectory gotoDirectory = new GotoDirectory(this);
        gotoDirectory.startPolling();


    }


    public void initActions() {
        applyButton.addActionListener(new ApplyAction());
        this.setDefaultsAltDButton.addActionListener(new DefaultsAction());
        selectAllButton.addActionListener(new SelectAllAction());
        findKeywordsButton.addActionListener(new FindKeywordsAction());
        selectNoneButton.addActionListener(new SelectNoneAction());
        trKeywords.addMouseListener(new TreeMouseListener());
        trDefKeywords.addMouseListener(new DefTreeMouseListener());
        trDefKeywords.addTreeExpansionListener(new ThisTreeExpansionListener());
        addKeyWordButton.addActionListener(new AddKeywordAction());
        copyMetadataButton.addActionListener(new CopyMetadataAction());
        pasteMetadataButton.addActionListener(new PasteMetadataAction());
        btnDelete.addActionListener(new DeleteKeywordAction());
        btnDeleteDef.addActionListener(new DeleteDefaultKeywordAction());
        tfSearch.getDocument().addDocumentListener(new ActiveSearchTreeAction());
        btnSearch.addActionListener(new SearchTreeAction());
        this.showLongNamesCheckBox.addChangeListener(new changeLongNames());
        this.defShowLongNamesCheckBox.addChangeListener(new changeDefLongNames());
    }


    public void bind() {

        photoUploaderPM = new PhotoUploaderPM();
        clipboard = new SimplePhotoUploaderPM();
        model = new PresentationModel(photoUploaderPM);
        Bindings.bind(edLatitude, model.getModel(PhotoUploaderPM.PROPNAME_LATITUDE));
        Bindings.bind(edLongitude, model.getModel(PhotoUploaderPM.PROPNAME_LONGITUDE));
        Bindings.bind(taCaption, model.getModel(PhotoUploaderPM.PROPNAME_CAPTION));
        Bindings.bind(edPhotographer, model.getModel(PhotoUploaderPM.PROPNAME_PHOTOGRAPHER));
        Bindings.bind(edDepth, model.getModel(PhotoUploaderPM.PROPNAME_DEPTH));
        Bindings.bind(edLocation, model.getModel(PhotoUploaderPM.PROPNAME_LOCATION));
        Bindings.bind(taKeywordString, model.getModel(PhotoUploaderPM.PROPNAME_KEYWORD_STRING));
        Bindings.bind(edCopyright, model.getModel(PhotoUploaderPM.PROPNAME_COPYRIGHT));
        Bindings.bind(this.showLongNamesCheckBox, model.getModel(PhotoUploaderPM.PROPNAME_SHOW_LONGNAMES));


        defaultPhotoUploaderPM = DefaultPhotoUploaderPM.read();
        try {
            imageBrowser.setDirectory(defaultPhotoUploaderPM.getDirectory());
        } catch (Exception e) {
        }
        defaultModel = new PresentationModel(defaultPhotoUploaderPM);
        Bindings.bind(edDefLatitude, defaultModel.getModel(PhotoUploaderPM.PROPNAME_LATITUDE));
        Bindings.bind(edDefLongitude, defaultModel.getModel(PhotoUploaderPM.PROPNAME_LONGITUDE));
        Bindings.bind(taDefCaption, defaultModel.getModel(PhotoUploaderPM.PROPNAME_CAPTION));
        Bindings.bind(edDefPhotographer, defaultModel.getModel(PhotoUploaderPM.PROPNAME_PHOTOGRAPHER));
        Bindings.bind(edDefDepth, defaultModel.getModel(PhotoUploaderPM.PROPNAME_DEPTH));
        Bindings.bind(edDefLocation, defaultModel.getModel(PhotoUploaderPM.PROPNAME_LOCATION));
        Bindings.bind(taDefKeywordString, defaultModel.getModel(PhotoUploaderPM.PROPNAME_KEYWORD_STRING));
        Bindings.bind(edDefCopyright, defaultModel.getModel(PhotoUploaderPM.PROPNAME_COPYRIGHT));
        Bindings.bind(this.defShowLongNamesCheckBox, defaultModel.getModel(PhotoUploaderPM.PROPNAME_SHOW_LONGNAMES));


        DatabaseGuiListNavigatorListener nv = new DatabaseGuiListNavigatorListener();

        SwingList swingList = (SwingList) photoUploaderPM.getKeywordList().getGuiList();
        tblKeywords.setModel(new KeywordTableModel(swingList.getSelectionInList(), photoUploaderPM));
        AimsCustomBinders.bind(tblKeywords, photoUploaderPM.getKeywordList(), nv);
        tblKeywords.getColumnModel().getColumn(0).setCellRenderer(new LeftDotRenderer());


        DatabaseGuiListNavigatorListener nvDef = new DatabaseGuiListNavigatorListener();

        SwingList swingListDef = (SwingList) defaultPhotoUploaderPM.getKeywordList().getGuiList();
        tblDefKeywords.setModel(new KeywordTableModel(swingListDef.getSelectionInList(), defaultPhotoUploaderPM));
        AimsCustomBinders.bind(tblDefKeywords, defaultPhotoUploaderPM.getKeywordList(), nvDef);
        tblDefKeywords.getColumnModel().getColumn(0).setCellRenderer(new LeftDotRenderer());


        keywordTree = new KeywordTree();
        treeModel = new DefaultTreeModel(keywordTree.getRootNode());
        trKeywords.setModel(treeModel);
        trDefKeywords.setModel(treeModel);


        try {
            GlobalParamaters.getPleaseWait().setMessage("Getting Reef List From the Server.", "Usually this is quite quick(<10 seconds).\n On very rare occasions you may have to wait up to five minutes.");
            keywordTree.addLocation();
            GlobalParamaters.getPleaseWait().setMessage("Initialising Keywords.", "Usually this is quite quick(<10 seconds).\n On very rare occasions you may have to wait up to five minutes.");
            buildKeywords();
            GlobalParamaters.getPleaseWait().setMessage("Initialising Taxonomy.", "Usually this is quite quick(<10 seconds).\n On very rare occasions you may have to wait up to five minutes.");
            buildTaxa();
        } catch (Exception e) {
            LoggerFactory.LogSevereException(e); //To change body of catch statement use File | Settings | File Templates.
            AimsSwingErrorHandler.ErrorMessage(e, this.getMainPanel());
        }

        keywordParser = new KeywordParserPhotoUploader(photoUploaderPM);
    }

    private void getKeywordFromTree(boolean def) {
        DefaultMutableTreeNode node;
        if (def) {
            node = (DefaultMutableTreeNode)
                    trDefKeywords.getLastSelectedPathComponent();

        } else {
            node = (DefaultMutableTreeNode)
                    trKeywords.getLastSelectedPathComponent();

        }

        if (node == null) return;

        if (node.getLevel() > 1) {
            String longName = KeywordTree.getStringPath(node);
            String shortName = (String) node.getUserObject();
            if (def) {
                defaultPhotoUploaderPM.AddKeyword(new KeywordPM(shortName, longName));
                this.tblDefKeywords.repaint();

            } else {
                photoUploaderPM.AddKeyword(new KeywordPM(shortName, longName));
                this.tblKeywords.repaint();
            }

        }

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void messageReceived(String directory) {
        LoggerFactory.LogInfo(directory);
//        imageBrowser.setNewDirectoryText(directory.replaceAll("[\u0000-\u0020]", ""));
        imageBrowser.setNewDirectoryText(directory);
    }

    private void findKeywords() {
        if (photoUploaderPM.getPhotoList().getLength() == 1) {
            keywordParser.parse();
        } else {
            if (JOptionPane.showConfirmDialog(this.getMainPanel(), "You have more than one photo selected. If you continue I will find the keywords for each selected photo and automatically save these updates.\n Do you want to continue?", "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                FindKeywordWorker.findKeywords(photoUploaderPM, keywordParser);
            }
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(508);
        splitPane1.setOneTouchExpandable(true);
        splitPane1.setResizeWeight(1.0);
        mainPanel.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(800, 600), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setText("Select All (Alt-A)");
        panel2.add(selectAllButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(113, 30), null, 0, false));
        selectNoneButton = new JButton();
        selectNoneButton.setText("Select None (Alt-N)");
        panel2.add(selectNoneButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(129, 30), null, 0, false));
        imageBrowser = new ImageBrowser();
        panel1.add(imageBrowser.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setEnabled(true);
        panel4.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tpKeywords = new JPanel();
        tpKeywords.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("General", tpKeywords);
        final JLabel label1 = new JLabel();
        label1.setText("Photographer");
        tpKeywords.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edPhotographer = new JTextField();
        tpKeywords.add(edPhotographer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Location");
        tpKeywords.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edLocation = new JTextField();
        tpKeywords.add(edLocation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Latitude");
        tpKeywords.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edLatitude = new JTextField();
        tpKeywords.add(edLatitude, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Longitude");
        tpKeywords.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edLongitude = new JTextField();
        tpKeywords.add(edLongitude, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Caption");
        tpKeywords.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Depth");
        tpKeywords.add(label6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDepth = new JTextField();
        edDepth.setText("");
        tpKeywords.add(edDepth, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Keywords");
        tpKeywords.add(label7, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edCopyright = new JTextField();
        tpKeywords.add(edCopyright, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Copyright");
        tpKeywords.add(label8, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        tpKeywords.add(scrollPane1, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        taKeywordString = new JTextArea();
        taKeywordString.setEditable(false);
        taKeywordString.setEnabled(false);
        taKeywordString.setLineWrap(true);
        scrollPane1.setViewportView(taKeywordString);
        final JScrollPane scrollPane2 = new JScrollPane();
        tpKeywords.add(scrollPane2, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        taCaption = new JTextArea();
        taCaption.setLineWrap(true);
        scrollPane2.setViewportView(taCaption);
        findKeywordsButton = new JButton();
        findKeywordsButton.setText("Find Keywords");
        tpKeywords.add(findKeywordsButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Keywords", panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setOrientation(0);
        panel7.add(splitPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setLeftComponent(panel8);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel8.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 80), null, 0, false));
        tblKeywords = new AimsJTable();
        tblKeywords.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        scrollPane3.setViewportView(tblKeywords);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        edAddKeyword = new JTextField();
        panel9.add(edAddKeyword, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addKeyWordButton = new JButton();
        addKeyWordButton.setText("Add Key Word");
        panel9.add(addKeyWordButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnDelete = new JButton();
        btnDelete.setFont(new Font(btnDelete.getFont().getName(), Font.BOLD, 12));
        btnDelete.setForeground(new Color(-65485));
        btnDelete.setText("Delete");
        panel10.add(btnDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showLongNamesCheckBox = new JCheckBox();
        showLongNamesCheckBox.setText("Show Long Names");
        panel10.add(showLongNamesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setRightComponent(panel11);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel11.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        trKeywords = new JTree();
        scrollPane4.setViewportView(trKeywords);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Defaults", panel12);
        edDefPhotographer = new JTextField();
        panel12.add(edDefPhotographer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDefLocation = new JTextField();
        panel12.add(edDefLocation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDefLatitude = new JTextField();
        panel12.add(edDefLatitude, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDefLongitude = new JTextField();
        panel12.add(edDefLongitude, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDefDepth = new JTextField();
        panel12.add(edDefDepth, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edDefCopyright = new JTextField();
        panel12.add(edDefCopyright, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Photographer");
        panel12.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Location");
        panel12.add(label10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Latitude");
        panel12.add(label11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Longitude");
        panel12.add(label12, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Depth");
        panel12.add(label13, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Copyright");
        panel12.add(label14, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Caption");
        panel12.add(label15, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Keywords");
        panel12.add(label16, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane5 = new JScrollPane();
        panel12.add(scrollPane5, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        taDefCaption = new JTextArea();
        taDefCaption.setLineWrap(true);
        taDefCaption.setText("");
        scrollPane5.setViewportView(taDefCaption);
        final JScrollPane scrollPane6 = new JScrollPane();
        panel12.add(scrollPane6, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        taDefKeywordString = new JTextArea();
        taDefKeywordString.setEditable(false);
        taDefKeywordString.setEnabled(false);
        taDefKeywordString.setLineWrap(true);
        scrollPane6.setViewportView(taDefKeywordString);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Default Keywords", panel13);
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setOrientation(0);
        panel13.add(splitPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setLeftComponent(panel14);
        final JScrollPane scrollPane7 = new JScrollPane();
        panel14.add(scrollPane7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 80), null, 0, false));
        tblDefKeywords = new AimsJTable();
        tblDefKeywords.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        scrollPane7.setViewportView(tblDefKeywords);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel15, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnDeleteDef = new JButton();
        btnDeleteDef.setFont(new Font(btnDeleteDef.getFont().getName(), Font.BOLD, 12));
        btnDeleteDef.setForeground(new Color(-65485));
        btnDeleteDef.setText("Delete");
        panel15.add(btnDeleteDef, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        defShowLongNamesCheckBox = new JCheckBox();
        defShowLongNamesCheckBox.setText("Show Long Names");
        panel15.add(defShowLongNamesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setRightComponent(panel16);
        final JScrollPane scrollPane8 = new JScrollPane();
        panel16.add(scrollPane8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        trDefKeywords = new JTree();
        scrollPane8.setViewportView(trDefKeywords);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Camera", panel17);
        tabbedPane1.setEnabledAt(4, false);
        final JScrollPane scrollPane9 = new JScrollPane();
        panel17.add(scrollPane9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane9.setBorder(BorderFactory.createTitledBorder(""));
        imageMeta = new JTable();
        scrollPane9.setViewportView(imageMeta);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Update (Alt-U)");
        panel18.add(applyButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 30), null, 0, false));
        setDefaultsAltDButton = new JButton();
        setDefaultsAltDButton.setText("Set Defaults (Alt-D)");
        panel18.add(setDefaultsAltDButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel18.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        copyMetadataButton = new JButton();
        copyMetadataButton.setText("Copy Metadata");
        panel18.add(copyMetadataButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pasteMetadataButton = new JButton();
        pasteMetadataButton.setText("Paste Metadata");
        panel18.add(pasteMetadataButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


    private class DefaultsAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            photoUploaderPM.copyProperties(defaultPhotoUploaderPM);
        }
    }

    private class SelectAllAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int end = imageBrowser.getImageList().getModel().getSize() - 1;
            if (end > 0) {
                imageBrowser.getImageList().setSelectionInterval(0, end);
            }
        }
    }

    private class SelectNoneAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            imageBrowser.getImageList().clearSelection();
        }
    }

    private class ApplyAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
//            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//            getMainPanel().setCursor(hourglassCursor);


            PhotoUpdateWorker.update(photoUploaderPM, PhotoUpdateWorker.AFTER_NOTHING, null);

//            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
//            getMainPanel().setCursor(normalCursor);


        }
    }

    private class BuildKeywordAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            (new KeywordBuilder()).execute();
        }
    }

    private class BuildTaxaAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            (new TaxaBuilder()).execute();
        }
    }

//    public void setData(PhotoUploaderPM data) {
//        edPhotographer.setText(data.getPhotographer());
//        edLocation.setText(data.getLocation());
//        edLatitude.setText(data.getLatitude());
//        edLongitude.setText(data.getLongitude());
//        taCaption.setText(data.getCaption());
//    }
//
//    public void getData(PhotoUploaderPM data) {
//        data.setPhotographer(edPhotographer.getText());
//        data.setLocation(edLocation.getText());
//        data.setLatitude(edLatitude.getText());
//        data.setLongitude(edLongitude.getText());
//        data.setCaption(taCaption.getText());
//    }

//    public boolean isModified(PhotoUploaderPM data) {
//        if (edPhotographer.getText() != null ? !edPhotographer.getText().equals(data.getPhotographer()) : data.getPhotographer() != null)
//            return true;
//        if (edLocation.getText() != null ? !edLocation.getText().equals(data.getLocation()) : data.getLocation() != null)
//            return true;
//        if (edLatitude.getText() != null ? !edLatitude.getText().equals(data.getLatitude()) : data.getLatitude() != null)
//            return true;
//        if (edLongitude.getText() != null ? !edLongitude.getText().equals(data.getLongitude()) : data.getLongitude() != null)
//            return true;
//        if (taCaption.getText() != null ? !taCaption.getText().equals(data.getCaption()) : data.getCaption() != null)
//            return true;
//        return false;
//    }


    private class ThisWindowAdapter extends WindowAdapter {
        public void windowClosed(WindowEvent e) {
//            imageBrowser.killProcessing();


            super.windowClosed(e);
            defaultPhotoUploaderPM.setDirectory(imageBrowser.getDirectory());
            defaultPhotoUploaderPM.write();
            if (photoUploaderPM.isDirty()) {
                if (JOptionPane.showConfirmDialog(mainPanel, "Do you want to save your changes", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    PhotoUpdateWorker.update(photoUploaderPM, PhotoUpdateWorker.AFTER_EXIT, null);
                } else {
                    System.exit(0);

                }

            } else {
                System.exit(0);

            }

        }
    }

    private class TreeMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
//            LoggerFactory.LogInfo("mouse");
            if (e.getClickCount() == 2) {
//                LoggerFactory.LogInfo("double");
                getKeywordFromTree(false);
            }
        }


    }

    private class DefTreeMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 2) {
                System.out.println("tree");
                getKeywordFromTree(true);
            }
        }


        public void mouseReleased(MouseEvent e) {
        }


    }


    private class CopyMetadataAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
//            clipboard.clearProperties();
            clipboard.copyProperties(photoUploaderPM);
        }
    }

    private class PasteMetadataAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            photoUploaderPM.copyProperties(clipboard);
        }
    }

    private class DeleteKeywordAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            photoUploaderPM.deleteCurrentKeyword();
        }
    }

    private class DeleteDefaultKeywordAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultPhotoUploaderPM.deleteCurrentKeyword();
        }
    }

    private class FindKeywordsAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            findKeywords();
        }

    }

    private class changeLongNames implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            tblKeywords.repaint();
        }
    }

    private class changeDefLongNames implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            tblDefKeywords.repaint();
        }
    }

    private class AddKeywordAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String keywordString = edAddKeyword.getText();
            String longName = keywordTree.addKeyword(keywordString);
            treeModel.reload();
            photoUploaderPM.AddKeyword(new KeywordPM(keywordString, longName));

        }
    }
    private class ActiveSearchTreeAction implements DocumentListener{
    // DocumentListener methods
    public void insertUpdate(DocumentEvent ev) {
        collapseNode();
        String search = tfSearch.getText();
        expandNode(search);
        resetListIndex();
    }

    public void removeUpdate(DocumentEvent ev) {
        collapseNode();
        String search = tfSearch.getText();
        expandNode(search);
        resetListIndex();
           }

    public void changedUpdate(DocumentEvent ev) {
        collapseNode();
        String search = tfSearch.getText();
        expandNode(search);
        resetListIndex();

    }
    }


    public class SearchTreeAction implements ActionListener{

        //@Override
        public void actionPerformed(ActionEvent e) {
            String search = tfSearch.getText();

            expandNextNode(search, listIndex);
            listIndex++;

        }


    }

    public void resetListIndex(){
        listIndex = 0;
    }

    private void buildKeywords() {
        keywordTree.addKeywords();
        treeModel.reload();
    }

    private class KeywordBuilder extends SwingWorker {


        protected Object doInBackground() throws Exception {
            buildKeywords();
            return null;
        }


        protected void done() {

        }


    }

    private void buildTaxa() {
        keywordTree.addTaxa();
        treeModel.reload();
    }

    private class TaxaBuilder extends SwingWorker {


        protected Object doInBackground() throws Exception {
            buildTaxa();
            return null;
        }


        protected void done() {

        }

    }

    private class ThisTreeExpansionListener implements TreeExpansionListener {

        public void treeExpanded(TreeExpansionEvent event) {
            mainPanel.revalidate();

        }

        public void treeCollapsed(TreeExpansionEvent event) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }



    public void expandNode(String s){
        DefaultMutableTreeNode root = keywordTree.getRootNode();
        TreePath path = find(root, s);
        trKeywords.setSelectionPath(path);
        trKeywords.scrollPathToVisible(path);
        trKeywords.expandPath(path);
    }

    public void expandNextNode(String s, int i){
        DefaultMutableTreeNode root = keywordTree.getRootNode();
        List<TreePath> pathsList = findListOfTreePaths(root, s);
        if (listIndex < pathsList.size()-1){
        TreePath path = pathsList.get(i);
        trKeywords.setSelectionPath(path);
        trKeywords.scrollPathToVisible(path);
        trKeywords.expandPath(path);
        } else {
            resetListIndex();
            expandNextNode(s, i);
        }
    }

    public void collapseNode(){
              for(int i = trKeywords.getRowCount()-1; i>0; i--){
                  trKeywords.collapseRow(i);
              }
        }


    private TreePath find(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().toLowerCase().startsWith(s.toLowerCase())) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private List<TreePath> findListOfTreePaths(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
       List<TreePath> paths = new ArrayList<TreePath>();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().toLowerCase().startsWith(s.toLowerCase())) {
                    paths.add(new TreePath(node.getPath()));
            }
        }
        if (paths.size() > 0) {
            return paths;
        }  else
        return null;
    }


    public static void main(String[] args) {
        System.setProperty
                ("sun.awt.exception.handler",
                        "aims.app.generic.gui.AimsSwingExceptionHandler");

        GlobalParamaters.setPleaseWait(new SwingPleaseWait());
        GlobalParamaters.getPleaseWait().setMessage("Initializing");
        GlobalParamaters.getPleaseWait().show();


        Uploader gui = new Uploader();


    }


}
