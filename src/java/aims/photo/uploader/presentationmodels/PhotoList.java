package aims.photo.uploader.presentationmodels;

import aims.app.generic.GlobalParamaters;
import aims.app.generic.logger.LoggerFactory;
import aims.photo.uploader.client.Photo;

import java.io.File;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 13/12/2007
 * Time: 11:49:52
 * To change this template use File | Settings | File Templates.
 */
public class PhotoList {

    Object []photos;

    Set<KeywordPM> keywords;


    private String latitude;
    private String longitude;
    private String caption;
    private String depth;
    private String photographer;
    private String location;
    private String copyright;

    public static final String NOT_SET = "";
    public static final String VARIED = "###_VARIED_###";


    public void setPhotos(Object[] p) {
        photos = p;

        latitude = NOT_SET;
        longitude = NOT_SET;
        caption = NOT_SET;
        depth = NOT_SET;
        photographer = NOT_SET;
        location = NOT_SET;
        copyright = NOT_SET;

        keywords=null;

        for (Object o: photos) {
            Photo photo = (Photo) o;
            latitude = common(latitude, photo.getLatitude());
            longitude = common(longitude, photo.getLongitude());
            caption= common(caption, photo.getCaption());
            depth = common(depth , photo.getDepth ());
            photographer= common(photographer, photo.getPhotographer());
            location = common(location, photo.getILocation());
            copyright = common(copyright, photo.getCopyright());

            Set<KeywordPM> photoKeywords = photo.getKeywordSet();
            if (keywords == null) {
                keywords = new HashSet<KeywordPM>(photoKeywords);
            } else {
                keywords.retainAll(photoKeywords);

            }
        }
    }



    public void update(PhotoUploaderPM p) {
        List<File> fileList = new ArrayList<File>(photos.length);
//        Map<String, String> metadata = new HashMap<String, String>(10);
//        if (!p.getLatitude().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_LATITUDE, p.getLatitude());
//        }
//        if (!p.getLongitude().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_LONGITUDE, p.getLongitude());
//        }
//        if (!p.getCaption().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_CAPTION, p.getCaption());
//        }
//        if (!p.getDepth().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_DEPTH, p.getDepth());
//        }
//        if (!p.getPhotographer().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_PHOTOGRAPHER, p.getPhotographer());
//        }
//        if (!p.getLocation().equals(VARIED)) {
//             metadata.put(ExifUtils.TAG_LOCATION, p.getLocation());
//        }
//
//        if (!p.getReef().equals(VARIED) && !p.getReef().equals(".")) {
//             metadata.put(ExifUtils.TAG_REEF, p.getReef());
//        }


        boolean singlePhoto = (photos.length == 1);
        for (Object o: photos) {
            Photo photo = (Photo) o;
            GlobalParamaters.getPleaseWait().setMessage("Updating photo meta-data", "for file " + photo.getOriginal().getName());
            LoggerFactory.LogInfo("Start update " + photo.getOriginal().getName());
            if (!p.getLatitude().equals(VARIED)) {
                 photo.setLatitude(p.getLatitude());
            }
            if (!p.getLongitude().equals(VARIED)) {
                 photo.setLongitude(p.getLongitude());
            }
            if (!p.getCaption().equals(VARIED)) {
                 photo.setCaption(p.getCaption());
            }
            if (!p.getDepth().equals(VARIED)) {
                 photo.setDepth(p.getDepth());
            }
            if (!p.getPhotographer().equals(VARIED)) {
                 photo.setPhotographer(p.getPhotographer());
            }
            if (!p.getLocation().equals(VARIED)) {
                 photo.setILocation(p.getLocation());
            }

            if (!p.getCopyright().equals(VARIED) && !p.getCopyright().equals(".")) {
                 photo.setReef(p.getCopyright());
            }

            if (singlePhoto) {
                photo.getKeywordSet().clear();
            }
            photo.getKeywordSet().addAll(keywords);


            fileList.add(photo.getOriginal());

            photo.updateMetadata();
            LoggerFactory.LogInfo("Finish update " + photo.getOriginal().getName());
        }


//        ExifUtils.getInstance().setTagsToFiles(fileList, metadata);


    }

    public void deleteKeyword(String k) {
        for (Object o: photos) {
            Photo photo = (Photo) o;
            photo.getKeywordSet().remove(k);        
        }
    }

    public String getLatitude() {

        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCaption() {
        return caption;
    }

    public String getDepth() {
        return depth;
    }

    public String getPhotographer() {
        return photographer;
    }

    public String getLocation() {
        return location;
    }

    public String getCopyright() {
        return copyright;
    }

    private String common(String sofar, String thisString) {
        if (sofar.equals(NOT_SET)) {
            return nullAsEmpty(thisString);
        }

        if (sofar.equals(thisString)) {
            return sofar;
        }

        return VARIED;
    }

    private String nullAsEmpty(String s) {
        if (s == null) {
            return NOT_SET;
        } else {
            return s;
        }
    }

    public Set<KeywordPM> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<KeywordPM> keywords) {
        this.keywords = keywords;
    }

    public int getLength() {
        return photos.length;
    }

    public List<Object> getCopyPhotoList() {
        return Arrays.asList(photos);

    }
}
