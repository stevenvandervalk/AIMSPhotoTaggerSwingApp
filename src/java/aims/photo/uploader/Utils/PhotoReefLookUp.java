/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 27/06/2008
 * Time: 14:14:28
 * To change this template use File | Settings | File Templates.
 */
package aims.photo.uploader.Utils;

import aims.app.generic.logger.LoggerFactory;
import aims.app.reefmonitoring.lookups.ReefLookUp;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.List;

public class PhotoReefLookUp {
    private static PhotoReefLookUp ourInstance = new PhotoReefLookUp();

    public static PhotoReefLookUp getInstance() {
        return ourInstance;
    }

    private PhotoReefLookUp() {
    }

    private List reefList;

    public void populate() {
        if (!readFromXML()) {
//            Db.initDb();
            ReefLookUp.populate();
            reefList = ReefLookUp.getReefList();
            writeToXML();
        }

    }

    public List getReefList() {
        return reefList;
    }

    private void writeToXML() {
        XMLEncoder e = null;

        File dir = new File("c:/aims");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("c:/aims/reefs.xml")));
            e.writeObject(reefList);
            e.close();

        } catch (FileNotFoundException e1) {
            LoggerFactory.LogSevereException(e1);
        }
    }

    private boolean readFromXML() {
        try {
            XMLDecoder d = null;
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream("c:/aims/reefs.xml")));
            reefList = (List) d.readObject();
            d.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
