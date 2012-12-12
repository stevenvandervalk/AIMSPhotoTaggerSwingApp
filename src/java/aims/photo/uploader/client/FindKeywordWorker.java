package aims.photo.uploader.client;

import aims.app.generic.GlobalParamaters;
import aims.app.reefmonitoring.model.KeywordParserAbstract;
import aims.photo.uploader.presentationmodels.PhotoUploaderPM;
import org.jdesktop.swingx.util.SwingWorker;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 15/01/2009
 * Time: 11:55:01
 * To change this template use File | Settings | File Templates.
 */
public class FindKeywordWorker extends SwingWorker {
    private PhotoUploaderPM photoUploaderPM;
    private KeywordParserAbstract keywordParser;
    public PhotoUploaderPM getPhotoUploaderPM() {
        return photoUploaderPM;
    }


    public void setPhotoUploaderPM(PhotoUploaderPM photoUploaderPM) {
        this.photoUploaderPM = photoUploaderPM;
    }

    public KeywordParserAbstract getKeywordParser() {
        return keywordParser;
    }

    public void setKeywordParser(KeywordParserAbstract keywordParser) {
        this.keywordParser = keywordParser;
    }

    public static void findKeywords(PhotoUploaderPM photoUploaderPM, KeywordParserAbstract keywordParser) {
        FindKeywordWorker worker = new FindKeywordWorker ();
        worker.setKeywordParser(keywordParser);
        worker.setPhotoUploaderPM(photoUploaderPM);

        GlobalParamaters.getPleaseWait().show();
        GlobalParamaters.getPleaseWait().setMessage("Finding keywords and updating");
        worker.execute();

    }

    protected Object doInBackground() throws Exception {

        java.util.List photos = photoUploaderPM.getPhotoList().getCopyPhotoList();
         for (Object photo : photos) {
             Object[] p = {photo};
             photoUploaderPM.setPhotos(p);
             keywordParser.parse();
             photoUploaderPM.update();

         }
         photoUploaderPM.setPhotos(photos.toArray());

        GlobalParamaters.getPleaseWait().hide();

        return null;
    }


}
