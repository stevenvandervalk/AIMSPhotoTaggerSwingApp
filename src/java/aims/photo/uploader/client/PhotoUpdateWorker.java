package aims.photo.uploader.client;

import aims.app.generic.GlobalParamaters;
import aims.app.generic.logger.LoggerFactory;
import aims.photo.uploader.presentationmodels.PhotoUploaderPM;
import org.jdesktop.swingx.util.SwingWorker;


/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 9/12/2008
 * Time: 15:41:28
 * To change this template use File | Settings | File Templates.
 */
public class PhotoUpdateWorker extends SwingWorker {



    private PhotoUploaderPM photoUploaderPM;
    private int after=AFTER_NOTHING ;

    public static final int AFTER_NOTHING = 0;
    public static final int AFTER_EXIT = 1;
    public  static final int AFTER_CHANGE_SELECTION = 2;

    private Object[] newSelection;

    public PhotoUploaderPM getPhotoUploaderPM() {
        return photoUploaderPM;
    }

    public void setPhotoUploaderPM(PhotoUploaderPM photoUploaderPM) {
        this.photoUploaderPM = photoUploaderPM;
    }

    public static void update(PhotoUploaderPM photoUploaderPM, int after, Object[] newSelection) {
        PhotoUpdateWorker worker = new PhotoUpdateWorker();

        worker.setPhotoUploaderPM(photoUploaderPM);
        worker.after=after;
        worker.newSelection=newSelection;
        GlobalParamaters.getPleaseWait().show();
        GlobalParamaters.getPleaseWait().setMessage("Updating photo meta-data");
        worker.execute();

    }



    protected Object doInBackground() throws Exception {
        photoUploaderPM.update();
        GlobalParamaters.getPleaseWait().hide();

        return null;
    }

    protected void done() {
        if (after == AFTER_EXIT) {
            System.exit(0);
        }
        if (after == AFTER_CHANGE_SELECTION) {
            photoUploaderPM.setPhotos(newSelection);

        }

    }
}
