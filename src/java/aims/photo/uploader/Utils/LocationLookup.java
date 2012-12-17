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
 * Last Updated: 14/12/2012
 * By: Timothy Hart
 * <p/>
 * <code>LocationLookup</code> is used to acquire data about <code>RmSectorEntity</code> objects located in the specified bin file.
 * Once the data is retrieved a list of enitities is built while ensuring that all relevant data about an entity is maintained from file reading.
 * <p/>
 * <code>LocationLookup</code> includes functionality to ensure that each entities correct location in a list is present and fully expressed.
 * i.e. any related sectors and any and all <code>ReefGeolocale</code> reefs.
 * <p/>
 * <code>LocationLookup</code> uses class loading methods to obtain the file.
 *
 * @author Timothy Hart
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
            throw new RuntimeException("ERROR!!! There was a problem loading files. Locations may not be present in the keyword list.");
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

    private boolean readFromXML() {
        try {
            ObjectInputStream d = null;
            d = new ObjectInputStream(
                    new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("lookup_bins/location.bin")));
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
