package aims.photo.uploader.Utils;

import aims.app.generic.logger.LoggerFactory;
import aims.app.generic.utilities.CaseInsensitiveString;
import aims.app.reefmonitoring.ejb3.Keywords;

import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 2/07/2008
 * Time: 14:13:26
 * To change this template use File | Settings | File Templates.
 */
public class KeywordLookup {
    private static KeywordLookup ourInstance = new KeywordLookup();

    public static KeywordLookup getInstance() {
        return ourInstance;
    }

    private KeywordLookup() {
    }

    private ArrayList<Keywords> list;
    private List<CaseInsensitiveString> caseInsensitiveList;

    public void populate() {

            if (readFromXML()) {
                LoggerFactory.LogInfo("Keyword List obtained from the local file.");
            } else {
                throw new RuntimeException("ERROR!!! You are not connected to the AIMS server. Also it appears you have not run this program before. The first time you run this program, you should be connected to the AIMS server.");
            }


        caseInsensitiveList = new ArrayList<CaseInsensitiveString>(list.size());
        for (Keywords k : list) {
            caseInsensitiveList.add(new CaseInsensitiveString(k.getKeyword()));
        }

    }

    public List<Keywords> getList() {
        return list;
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
                            new FileOutputStream("c:/aims/keywords.bin")));
            e.writeObject(list);
            e.close();

        } catch (FileNotFoundException e1) {
            LoggerFactory.LogSevereException(e1);
        }
    }

    private boolean readFromXML() {
        URL c = getClass().getClassLoader().getResource("lookup_bins/keywords.bin");
        try {
            ObjectInputStream d = null;
            d = new ObjectInputStream(
                    new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("lookup_bins/keywords.bin")));
            //new FileInputStream(new File(c.toURI()))));
            list = (ArrayList) d.readObject();
            d.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        KeywordLookup.getInstance().populate();
        LoggerFactory.LogInfo(KeywordLookup.getInstance().getList().toString());
        System.exit(0);

    }

    public String isKeyword(String s) {
        try {
            int i = caseInsensitiveList.indexOf(new CaseInsensitiveString(s));
            if (i == -1) {
                return null;
            } else {
                return caseInsensitiveList.get(i).getString();
            }

        } catch (Exception e) {
            LoggerFactory.LogSevereException(e);
            return null;
        }
    }


}
