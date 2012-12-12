package aims.photo.uploader.presentationmodels;

import aims.app.generic.logger.LoggerFactory;
import aims.app.generic.utilities.Utils;
import com.jgoodies.binding.beans.Model;

import javax.swing.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 13/12/2007
 * Time: 14:29:32
 * To change this template use File | Settings | File Templates.
 */
public class SimplePhotoUploaderPM extends Model implements KeywordListListener{
    private String photographer;
    private String location;
    private String latitude;
    private String longitude;
    private String caption;
    private String depth;
    private String copyright;
//    private List<String> keywords;


    public static final String PROPNAME_SHOW_LONGNAMES = "showLongnames";
    public static final String PROPNAME_LONGITUDE = "longitude";
    public static final String PROPNAME_LATITUDE = "latitude";
    public  static final String PROPNAME_PHOTOGRAPHER = "photographer";
    public  static final String PROPNAME_LOCATION = "location";
    public  static final String PROPNAME_DEPTH = "depth";
    public  static final String PROPNAME_CAPTION = "caption";
    public static final String PROPNAME_REEF = "reef";
    public static final String PROPNAME_KEYWORD_STRING = "keywordString";
    public  static final String PROPNAME_COPYRIGHT = "copyright";

    private KeywordList keywordList;

    public SimplePhotoUploaderPM() {
        keywordList = new KeywordList();
        keywordList.init(new ArrayList<KeywordPM>());
        getKeywordList().setListener(this);

    }

//    public List<String> getKeywords() {
//        return keywords;
//    }
//
//    public void setKeywords(List<String> keywords) {
//        this.keywords = keywords;
//    }



    private boolean showLongnames= true;

    public boolean isShowLongnames() {
        return showLongnames;
    }

    public void setShowLongnames(boolean showLongnames) {
        boolean oldVal = this.showLongnames;
        this.showLongnames = showLongnames;
        firePropertyChangeGreg(PROPNAME_SHOW_LONGNAMES, oldVal, showLongnames);

    }

    public void firePropertyChangeGreg(String fld, Object oldVal, Object newVal) {
        super.firePropertyChange(fld, oldVal, newVal);
        setDirty();
    }

    public void fireMultiplePropertiesChangedGreg () {
        super.fireMultiplePropertiesChanged();
        setDirty();

    }

    public void setDirty() {

    }

    public KeywordList getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(KeywordList keywordList) {
        this.keywordList = keywordList;
    }


    public String getKeywordString () {
        return keywordList.getDelimitedString();
    }

    public void setKeywordString (String s) {

    }


    public void AddKeyword(KeywordPM k) {

        getKeywordList().add(k);
        keywordListChanged();        
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        String oldVal = this.photographer;
        this.photographer = photographer;
        firePropertyChangeGreg(PROPNAME_PHOTOGRAPHER, oldVal, photographer);

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        String oldVal = this.location;
        this.location = location;
        firePropertyChangeGreg(PROPNAME_LOCATION, oldVal, location);

    }


    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        String oldVal = this.depth;
        this.depth = depth;
        firePropertyChangeGreg(PROPNAME_DEPTH, oldVal, depth);

    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        String oldVal = this.latitude;
        this.latitude = latitude;
        firePropertyChangeGreg(PROPNAME_LATITUDE, oldVal, latitude);

    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        String oldVal = this.longitude;
        this.longitude = longitude;
        firePropertyChangeGreg(PROPNAME_LONGITUDE, oldVal, longitude);

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        String oldVal = this.caption;
        this.caption = caption;
        firePropertyChangeGreg(PROPNAME_CAPTION, oldVal, caption);

    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        String oldVal = this.copyright;
        this.copyright = copyright;
        firePropertyChangeGreg(PROPNAME_COPYRIGHT, oldVal, copyright);

    }

    public void keywordListChanged() {
        this.fireMultiplePropertiesChangedGreg();
    }

    public void clearProperties () {
            this.setLatitude(PhotoList.NOT_SET);
            this.setLongitude(PhotoList.NOT_SET);
            this.setDepth(PhotoList.NOT_SET);
            this.setLocation(PhotoList.NOT_SET);
            this.setCaption(PhotoList.NOT_SET);
            this.setPhotographer(PhotoList.NOT_SET);
            this.setCopyright(PhotoList.NOT_SET);

            getKeywordList().clear();

    }

    public void copyProperties (SimplePhotoUploaderPM def) {
        if (!isNullOrEmptyOrVaried(def.getLatitude())) {
            this.setLatitude(def.getLatitude());
        }

        if (!isNullOrEmptyOrVaried(def.getLongitude())) {
            this.setLongitude(def.getLongitude());
        }

        if (!isNullOrEmptyOrVaried(def.getDepth())) {
            this.setDepth(def.getDepth());
        }

        if (!isNullOrEmptyOrVaried(def.getLocation())) {
            this.setLocation(def.getLocation());
        }
        if (!isNullOrEmptyOrVaried(def.getCaption())) {
            this.setCaption(def.getCaption());
        }
        if (!isNullOrEmptyOrVaried(def.getPhotographer())) {
            this.setPhotographer(def.getPhotographer());
        }

        if (!isNullOrEmptyOrVaried(def.getCopyright())) {
            this.setCopyright(def.getCopyright());
        }

        KeywordList k = def.getKeywordList();
        boolean ok = k.first();
        while (ok) {
            AddKeyword(k.getCurrent());
            ok = k.next();            
        }

    }

    private static boolean isNullOrEmptyOrVaried(String s) {
        if (s == null) {
            return true;
        }
        if (s.equals("")) {
            return true;
        }
        if (s.equals(PhotoList.NOT_SET)) {
            return true;
        }
        if (s.equals(PhotoList.VARIED)) {
            return true;
        }

        return false;
    }


    public void deleteKeyword(String k) {
        keywordListChanged();
    }

    public void deleteCurrentKeyword () {
        this.getKeywordList().delete();
    }


}
