package aims.photo.uploader.client;


import aims.app.generic.utilities.ExifUtilsETool;
import aims.app.generic.utilities.ExifUtilsI;
import aims.app.generic.utilities.LatitudeCoordinate;
import aims.app.generic.utilities.LongitudeCoordinate;
import aims.photo.uploader.presentationmodels.KeywordPM;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 2/02/2007
 * Time: 08:45:21
 * To change this template use File | Settings | File Templates.
 */
public class Photo extends JPanel {
    private JTextField title;
    private JPanel imagePanel;
    private JPanel innerImagePanel;
    private JPanel mainPanel;

    private int index = 0;

    private boolean hasFocus;
    private boolean selected;

    protected File original;

    // Not serialized
    transient protected ExifDirectory directory = null;
    //transient protected SoftReference thumbImage = null;
    transient protected IptcDirectory iDirectory = null;

    private Set<KeywordPM> keywordSet;
    // Not serialized
    //    transient protected GpsDirectory gpsDirectory = null;
    //    transient protected ExifDirectory exifDirectory = null;
    //    transient protected Metadata drewMetadata;
    //    transient protected IFDEntry[][] ifdentry;
    //    transient protected String xmp;
    //    transient protected IPTCEntryCollection i;
    //transient protected SoftReference thumbImage = null;
    transient protected Map<String, String> metadata;


    public Photo() {

        this.setSize(ImageBrowser.THUMB_WIDTH, ImageBrowser.THUMB_HEIGHT);

        this.add(mainPanel, BorderLayout.CENTER);


    }


    public Photo(File photoFile, Map<String, String> metadata) {
        this();
        this.original = photoFile;
//        getThumbImage();

        BufferedImage image = getThumbImage();

        innerImagePanel = new ImagePanel(image);

        this.imagePanel.add(innerImagePanel);
        this.metadata = metadata;
        keywordSet = makeSet(getKeywords());

        title.setText(photoFile.getName());
    }

//    public Photo(Photo photo) {
//        this(photo.getOriginal(), null);
//
//    }

    public String getFileName() {
        return original.getName();
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (this.selected) {
            this.setBackground(Color.DARK_GRAY);
        } else {
            this.setBackground(Color.WHITE);
        }
    }

    public int getIndex() {
        return index;
    }

    public JPanel getInnerImagePanel() {
        return innerImagePanel;
    }

    public JPanel getImagePanel() {
        return imagePanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private File getOriginalFile() {

        return this.original;
    }

    public static void main(String[] args) {

        Photo gui = new Photo();

        final JFrame frame = new JFrame("A Photo Panel");

        frame.setSize(ImageBrowser.THUMB_WIDTH, ImageBrowser.THUMB_HEIGHT);

        frame.setContentPane(gui.getMainPanel());

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.show();


    }


    private BufferedImage getThumbImage() {
        BufferedImage thumb = null;
        try {


            getExifDirectory();
            if (directory != null) {
                byte thumbData[] = directory.getThumbnailData();
                if (thumbData != null) {
                    Iterator readers = ImageIO.getImageReadersBySuffix("jpg");
                    ImageReader jpgReader = (ImageReader) readers.next();
                    jpgReader.setInput(new MemoryCacheImageInputStream
                            (new ByteArrayInputStream(thumbData)));
                    thumb = jpgReader.read(0);
                    jpgReader.dispose();
                    System.out.println("found thumb of size " + thumb.getHeight() + " " + thumb.getWidth());
                }


            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (thumb == null) {
            try {
                thumb = ImageIO.read(original);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return thumb;
    }

    private Set<String> makeSet(String k) {
        if (k == null) {
            return new HashSet<String>();
        } else {
            try {
                List<String> stringList = Arrays.asList(k.split(", "));
                return new HashSet<String>(stringList);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return new HashSet<String>();

            }

        }
    }

    private Set<KeywordPM> makeSet(String[] kWords) {
        if (kWords == null) {
            return new HashSet<KeywordPM>();
        } else {
            HashSet<KeywordPM> set = new HashSet<KeywordPM>(kWords.length);
            for (String kWord : kWords) {
                set.add(new KeywordPM(kWord));
            }
            return set;
        }
    }


    private String makeString(Set<KeywordPM> set) {
        String r = new String();
        String space = "";
        for (KeywordPM kWord : set) {
            r = r + space + kWord.getKeyword();
            space = "::";
        }
        return r;
    }

    public Map<String, String> getMetaData() {

        if (metadata == null && getOriginalFile() != null) {
            try {
// TODO get hmeta data using Drew
//                metadata = ExifUtils.getInstance().getTagsFromFile(getOriginalFile(), ExifUtils.getInstance().getTagNames());
//                keywordSet = makeSet(getKeywords());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return metadata;
    }

    public void updateMetadata() {
        setKeywords(makeString(keywordSet));
        ExifUtilsETool.getInstance().setTagsToFile(getOriginalFile(), metadata);
    }

    public String getLatitude() {
        try {
            String val = getTag(ExifUtilsI.TAG_LATITUDE);
            Double valD = Double.parseDouble(val);
            Double absValD = Math.abs(valD);

            String dir = getTag(ExifUtilsI.TAG_LAT_DIR);
            Double dirD;
            if (dir.startsWith("S")) {
                dirD = -1.0;
            } else {
                dirD = 1.0;
            }

            Double valDir = absValD * dirD;
            return valDir.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getLongitude() {
        try {
            String val = getTag(ExifUtilsI.TAG_LONGITUDE);
            Double valD = Double.parseDouble(val);
            Double absValD = Math.abs(valD);

            String dir = getTag(ExifUtilsI.TAG_LONG_DIR);
            Double dirD;
            if (dir.startsWith("W")) {
                dirD = -1.0;
            } else {
                dirD = 1.0;
            }

            Double valDir = absValD * dirD;
            return valDir.toString();
        } catch (Exception e) {
            return "";
        }
    }


    public void setLatitude(String s) {
        try {

            LatitudeCoordinate coord = new LatitudeCoordinate(s);

            getMetaData().put(ExifUtilsI.TAG_LATITUDE, coord.getValueString());
            getMetaData().put(ExifUtilsI.TAG_LAT_DIR, coord.getDirectionString());
        } catch (NumberFormatException e) {
        }
    }

    public void setLongitude(String s) {
        try {
            LongitudeCoordinate coord = new LongitudeCoordinate(s);

            getMetaData().put(ExifUtilsI.TAG_LONGITUDE, coord.getValueString());
            getMetaData().put(ExifUtilsI.TAG_LONG_DIR, coord.getDirectionString());
        } catch (NumberFormatException e) {
        }
    }

    private void setKeywords(String s) {
        getMetaData().put(ExifUtilsI.TAG_KEYWORDS, s);
    }

    private String[] getKeywords() {
//        return getTag(ExifUtils.TAG_KEYWORDS);
        getExifDirectory();
        if (directory != null) {
            try {
                return iDirectory.getStringArray(IptcDirectory.TAG_KEYWORDS);
            } catch (MetadataException e) {
            }

        }
        return new String[0];
    }

    public Set<KeywordPM> getKeywordSet() {
        return keywordSet;
    }

    public String getDepth() {
        return getTag(ExifUtilsI.TAG_DEPTH);
    }

    public void setDepth(String s) {
        getMetaData().put(ExifUtilsI.TAG_DEPTH, s);
    }

    public String getILocation() {
        return getTag(ExifUtilsI.TAG_LOCATION);
    }

    public void setILocation(String s) {
        getMetaData().put(ExifUtilsI.TAG_LOCATION, s);
    }

    public String getPhotographer() {
        return getTag(ExifUtilsI.TAG_PHOTOGRAPHER);
    }

    public void setPhotographer(String s) {
        getMetaData().put(ExifUtilsI.TAG_PHOTOGRAPHER, s);
    }

    public String getCaption() {
        return getTag(ExifUtilsI.TAG_CAPTION);
    }

    public void setCaption(String s) {
        getMetaData().put(ExifUtilsI.TAG_CAPTION, s);
    }

    public String getCopyright() {
        return getTag(ExifUtilsI.TAG_COPYRIGHT);
    }

    public void setReef(String s) {
        getMetaData().put(ExifUtilsI.TAG_COPYRIGHT, s);
    }

    public void setKeywordSet(Set<KeywordPM> keywordSet) {
        this.keywordSet = keywordSet;
    }


    private String getTag(String tagname) {
        try {
            return getMetaData().get(tagname);
        } catch (Exception e) {
            return "";
        }
    }

    public ExifDirectory getExifDirectory() {
        if (directory == null && getOriginalFile() != null) {
            try {
                Metadata metadata = JpegMetadataReader.readMetadata(getOriginalFile());
                directory = (ExifDirectory) metadata.getDirectory(ExifDirectory.class);
                iDirectory = (IptcDirectory) metadata.getDirectory(IptcDirectory.class);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return directory;
    }

    public File getOriginal() {
        return original;
    }

    //    private Metadata getMetaData() {
//        if (metadata == null && getOriginalFile() != null) {
//            try {
//                metadata = JpegMetadataReader.readMetadata(getOriginalFile());
//            } catch (JpegProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        return metadata;
//    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBackground(new Color(-1));
        mainPanel.setPreferredSize(new Dimension(190, 190));
        title = new JTextField();
        title.setBackground(new Color(-1));
        title.setEditable(false);
        mainPanel.add(title, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(imagePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(190, 190), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
