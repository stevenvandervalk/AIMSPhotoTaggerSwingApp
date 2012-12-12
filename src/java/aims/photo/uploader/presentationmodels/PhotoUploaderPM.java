package aims.photo.uploader.presentationmodels;

import aims.photo.uploader.client.ImageBrowser;
import aims.photo.uploader.client.Photo;

public class PhotoUploaderPM extends SimplePhotoUploaderPM {
    private PhotoList photoList = new PhotoList();
    private ImageBrowser imageBrowser;

    public boolean isDirty() {
        return dirty;
    }

    private boolean dirty=false;

    public PhotoUploaderPM() {
        super();
    }

    public void setDirty() {
        dirty=true;
    }

    public void setKeywordList(KeywordList keywordList) {
        super.setKeywordList(keywordList);
        keywordList.setListener(this);
    }


    public void setPhotos(Object[] photos) {
        photoList.setPhotos(photos);
        refreshAllFields();
    }


    public void refreshAllFields() {
        setLatitude (photoList.getLatitude());
        setLongitude (photoList.getLongitude());
        setCaption  (photoList.getCaption());
        setPhotographer  (photoList.getPhotographer());
        setLocation  (photoList.getLocation());
        setDepth  (photoList.getDepth());
        setCopyright (photoList.getCopyright());
        getKeywordList().setList(photoList.getKeywords());

        keywordListChanged();
        dirty=false;
    }


    public void update() {
        photoList.setKeywords(getKeywordList().getList());
        photoList.update(this);
        dirty=false;
    }
    public ImageBrowser getImageBrowser() {
        return imageBrowser;
    }

    public void setImageBrowser(ImageBrowser imageBrowser) {
        this.imageBrowser = imageBrowser;
    }


    public void deleteKeyword(String k) {
        photoList.deleteKeyword(k);
        keywordListChanged();
    }


    public PhotoList getPhotoList() {
        return photoList;
    }
}