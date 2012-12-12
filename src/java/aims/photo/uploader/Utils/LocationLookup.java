package aims.photo.uploader.Utils;

import aims.app.generic.logger.LoggerFactory;
import aims.app.reefmonitoring.ejb3.RmSectorEntity;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 11/08/2008
 * Time: 14:47:06
 * To change this template use File | Settings | File Templates.
 */
public class LocationLookup {
    private static LocationLookup ourInstance = new LocationLookup();

    public static LocationLookup getInstance() {
        return ourInstance;
    }

    private LocationLookup() {
    }

    private List<RmSectorEntity> list;

    public void populate() {

            if (readFromXML()) {
                LoggerFactory.LogInfo("Sector List obtained from the local file.");
            } else {
                throw new RuntimeException("ERROR!!! Failed to load keyword list. Your keyword tree will be empty");
            }
    }

    public List<RmSectorEntity> getList() {
        return list;
    }

    public void writeToXML() {
        ObjectOutputStream e = null;

        File dir = new File("c:/aims");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            e = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream("c:/aims/location.bin")));
            e.writeObject(list);
            e.close();

        } catch (Exception e2) {
            LoggerFactory.LogSevereException(e2);
        }
    }


    //InputStream in = loader.getResourceAsStream("/location.bin");

    private boolean readFromXML() {
        URL c = getClass().getClassLoader().getResource("lookup_bins/location.bin");
        try {
            ObjectInputStream d = null;
            d = new ObjectInputStream(
                    new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("lookup_bins/location.bin")));
                            //new FileInputStream(new File(c.toURI()))));
            list = (List) d.readObject();
            Collections.sort(list);

            d.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        LocationLookup.getInstance().populate();
        LoggerFactory.LogInfo(LocationLookup.getInstance().getList().toString());
        System.exit(0);

    }


}
