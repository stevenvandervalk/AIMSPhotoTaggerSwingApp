package aims.photo.uploader.client;

import aims.app.generic.logger.LoggerFactory;
import aims.app.generic.utilities.ExifUtilsETool;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.ImageFilter;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 12/12/2007
 * Time: 13:32:23
 * To change this template use File | Settings | File Templates.
 */
public class ImageBrowser extends JPanel implements PropertyChangeListener {
    private JList imageList;
    private JFileChooser fileChooser;
    private JPanel mainPanel;
    private JButton changeToDirectoryButton;
    private JTextField edNewDirectory;
    private JTabbedPane tpFileChooser;
    private JList textFileList;
    private JButton pasteFromClipboardButton;
    public static final int THUMB_WIDTH = 210;
    public static final int THUMB_HEIGHT = 210;
    private DefaultListModel listModel;
    private ImageFilter filter;
    private List<File> photoList;
    private LargePhoto largePhoto;

    private String[] files;

    public JTextField getEdNewDirectory() {
        return edNewDirectory;
    }

    public void setEdNewDirectory(JTextField edNewDirectory) {
        this.edNewDirectory = edNewDirectory;
    }

    private JFrame getLargePhoto() {
        if (largePhoto == null) {
            largePhoto = new LargePhoto(imageList);
        }
        return largePhoto;
    }

    public ImageBrowser()

    {
        super();
        $$$setupUI$$$();
        this.add(mainPanel);


        imageList.setCellRenderer(new MyListCellRenderer());
        imageList.setFixedCellWidth(THUMB_WIDTH);
        imageList.setFixedCellHeight(THUMB_HEIGHT);
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(3);

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addPropertyChangeListener(this);
        filter = new ImageFilter(false);
        fileChooser.addChoosableFileFilter(new ImageFilter(true));
//        new ThisWorker().execute();
        this.changeToDirectoryButton.addActionListener(new GotoNewDirectory());
        this.pasteFromClipboardButton.addActionListener(new PasteAction());
        imageList.addMouseListener(new OpenPhotoListener());
        tpFileChooser.addChangeListener(new TabFileChooserChanged());


    }

    public void setDirectory(String directoryS) {
        LoggerFactory.LogInfo(directoryS);
        File dir = new File(directoryS);
        if (dir == null) {
            LoggerFactory.LogWarning("dir is null");
            return;
        }
        if (!dir.exists()) {
            LoggerFactory.LogWarning("dir does not exist");
            return;
        }

        if (!dir.isDirectory()) {
            dir = dir.getParentFile();
        }

        LoggerFactory.LogInfo("dir does exist");

        fileChooser.setCurrentDirectory(dir);
//        fileChooser.setSelectedFile(dir);
//        loadFilesInBackground();
    }

    public String getDirectory() {
        return fileChooser.getCurrentDirectory().getPath();
    }

    public JList getImageList() {
        return imageList;
    }

    public Photo[] getSelectedPhotos() {
        return (Photo[]) imageList.getSelectedValues();
    }

    private void loadFilesInBackground() {
//        System.out.println("loading files in background");
//        killProcessing();

//        loadFilesInBackgroundWorker = new LoadFilesInBackgroundWorker();
//
//        loadFilesInBackgroundWorker.execute();

        loadFilesFromFileBrowser();

    }

//    public void killProcessing() {
//        LoggerFactory.LogInfo("check for killed ");
//        if (!loadFilesInBackgroundWorker.isDone()) {
//            LoggerFactory.LogInfo("killing ");
//            loadFilesInBackgroundWorker.cancel(true);
//            LoggerFactory.LogInfo("killed!! ");
//        }
//
//    }

    private void loadFilesFromFileBrowser() {

        File dir = fileChooser.getCurrentDirectory();
        LoggerFactory.LogInfo("ImageBrowser.loadfiles for dir " + dir.getAbsolutePath());

        if (dir != null) {
            LoggerFactory.LogInfo("ImageBrowser.loadfiles for dir one");
            LoggerFactory.LogInfo("ImageBrowser.loadfiles for dir two");

            File[] files = dir.listFiles(filter);

            LoggerFactory.LogInfo("Number of files " + files.length);


            loadFiles(files);


        }

    }

    private void loadFilesFromTextList() {
        File[] realFiles = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            realFiles[i] = new File(files[i]);
        }
        loadFiles(realFiles);

    }

    private void loadFiles(File[] files) {
        listModel.removeAllElements();
        int index = 0;
        if (files.length > 0) {
            photoList = Arrays.asList(files);
            if (photoList.size() > 150) {
                photoList = photoList.subList(0, 149);
                JOptionPane.showMessageDialog(this, "Too many photos in this directory. Only showing the first 150.");
            }
            Set<String> tagNames = ExifUtilsETool.getInstance().getTagNames();
            Map<File, Map<String, String>> metadata = ExifUtilsETool.getInstance().getTagsFromFiles(photoList, tagNames);

            for (File file : photoList) {


                System.out.println(file.getName());
//                System.out.println("do stuff with file");

                Photo p = new Photo(file, metadata.get(file));
                listModel.add(index++, p);

                System.out.println("added photo " + index);


            }


        }
    }

    private void changeLargePhoto() {
        LoggerFactory.LogInfo("changeLargePhoto");
        if (largePhoto == null) {
            return;
        }

        largePhoto.setPhoto();

    }

    //    private class LoadFilesInBackgroundWorker extends SwingWorker {
//
//        protected Object doInBackground() throws Exception {
//            System.out.println("do in background");
//            loadFilesFromFileBrowser();
//            return null;
//        }
//
//
//    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
            System.out.println("this " + evt.getPropertyName());

            loadFilesInBackground();

        }
    }


    private void createUIComponents() {
        mainPanel = new JPanel();
        listModel = new DefaultListModel();
        imageList = new JList(listModel);
        // TODO: place custom component creation code here
    }


    public void setNewDirectoryText(String newDirectory) {
        getEdNewDirectory().setText(newDirectory);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(12, 12), null, 0, false));
        changeToDirectoryButton = new JButton();
        changeToDirectoryButton.setText("Change to Directory");
        panel1.add(changeToDirectoryButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edNewDirectory = new JTextField();
        panel1.add(edNewDirectory, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(0);
        mainPanel.add(splitPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setMaximumSize(new Dimension(2147483647, 359));
        splitPane1.setLeftComponent(panel2);
        tpFileChooser = new JTabbedPane();
        panel2.add(tpFileChooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tpFileChooser.addTab("File Browser", panel3);
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.setMultiSelectionEnabled(false);
        panel3.add(fileChooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tpFileChooser.addTab("File List (text)", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        pasteFromClipboardButton = new JButton();
        pasteFromClipboardButton.setText("Paste from clipboard");
        panel5.add(pasteFromClipboardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel5.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel6.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textFileList = new JList();
        scrollPane1.setViewportView(textFileList);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel7);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel7.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        imageList.setMinimumSize(new Dimension(-1, -1));
        scrollPane2.setViewportView(imageList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private class GotoNewDirectory implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            setDirectory(edNewDirectory.getText());
        }
    }

    private class PasteAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String clipText = aims.app.generic.utilities.Utils.getClipboardContents();
            files = clipText.split("\n");
            textFileList.setListData(files);
            loadFilesFromTextList();

        }
    }


    private class OpenPhotoListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                LoggerFactory.LogInfo("Open Photo " + ((Photo) imageList.getSelectedValue()).getFileName());

                getLargePhoto().show();
                changeLargePhoto();
            }
        }

    }

    private class TabFileChooserChanged implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (tpFileChooser.getSelectedIndex() == 0) {
                loadFilesFromFileBrowser();
            } else {
                loadFilesFromTextList();
            }
        }
    }

}
