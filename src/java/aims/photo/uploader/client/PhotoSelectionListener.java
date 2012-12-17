package aims.photo.uploader.client;

import aims.photo.uploader.presentationmodels.PhotoUploaderPM;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 3/02/2007
 * Time: 12:09:07
 * To change this template use File | Settings | File Templates.
 */
public class PhotoSelectionListener implements ListSelectionListener {

    private JList imageList;
    private PhotoUploaderPM photoUploaderPM;


    public PhotoSelectionListener(JList imageList, PhotoUploaderPM photoUploaderPM) {
        this.imageList = imageList;
        this.photoUploaderPM = photoUploaderPM;
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {

        Object[] objects = imageList.getSelectedValues();
        if (photoUploaderPM.isDirty()) {
            if (JOptionPane.showConfirmDialog(imageList, "Do you want to save your changes", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                PhotoUpdateWorker.update(photoUploaderPM, PhotoUpdateWorker.AFTER_CHANGE_SELECTION, objects);
            } else {
                photoUploaderPM.setPhotos(objects);

            }
        } else {

            photoUploaderPM.setPhotos(objects);
        }

    }
    
}
