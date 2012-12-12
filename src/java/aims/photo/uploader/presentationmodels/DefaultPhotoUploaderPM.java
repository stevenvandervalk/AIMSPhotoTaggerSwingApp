package aims.photo.uploader.presentationmodels;

import aims.app.generic.logger.LoggerFactory;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 14/01/2008
 * Time: 11:07:39
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPhotoUploaderPM extends SimplePhotoUploaderPM{

    private String directory;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void write () {
        XMLEncoder e = null;
        try {
            e = new XMLEncoder(
                               new BufferedOutputStream(
                                   new FileOutputStream("c:\\PhotoDefaults.xml")));


           e.writeObject(this);
           e.close();

        } catch (FileNotFoundException e1) {
            LoggerFactory.LogSevereException(e1);
        }
    }

    public static DefaultPhotoUploaderPM read() {

        DefaultPhotoUploaderPM result;
        try {
            XMLDecoder d = null;
            d = new XMLDecoder(
                               new BufferedInputStream(
                                   new FileInputStream("c:\\PhotoDefaults.xml")));
            result = (DefaultPhotoUploaderPM) d.readObject();
            d.close();
        } catch (FileNotFoundException e) {
            result = new DefaultPhotoUploaderPM();
        }
        return result;
    }


}
