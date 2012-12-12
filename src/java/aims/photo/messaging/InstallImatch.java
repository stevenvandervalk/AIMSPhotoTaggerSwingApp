package aims.photo.messaging;

import aims.app.generic.logger.LoggerFactory;
import com.roxes.win32.LnkFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 12/08/2008
 * Time: 12:10:51
 * To change this template use File | Settings | File Templates.
 */
public class InstallImatch {
    public static final String ALL_USERS_DIR = "C:\\Documents and Settings\\All Users";
    public static final String USER_TOOLS = "\\Application Data\\photools.com\\IMatch\\UserTools";

    public static File allUsersUserTools() {
        return new File(ALL_USERS_DIR + USER_TOOLS);
    }

    public static File thisUserUserTools() {
        return new File(System.getProperty("user.home") + USER_TOOLS);
    }

    public static File userToolsDir() {
        File out = allUsersUserTools();
        if (out.exists()) {
            return out;
        }
        out = thisUserUserTools();
        if (out.exists()) {
            return out;
        }

        return null;

    }


    public static void installShortcut () {

        File batchFile = new File("c:/aims/phototagger.bat");
        if (!batchFile.exists()) {
            new File("c:/aims").mkdirs();
            FileWriter out=null;
            try {
                out = new  FileWriter(batchFile);
                out.write("javaws -offline -XnoSplash -open %1 http://hydatina.aims.gov.au:8080/phototagger/phototagger.jnlp");
            } catch (IOException e) {
                LoggerFactory.LogSevereException(e); //To change body of catch statement use File | Settings | File Templates.
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    LoggerFactory.LogSevereException(e); //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

        File dir = userToolsDir();
        if (dir != null) {
            LnkFile shortcut = new LnkFile(dir.getAbsolutePath(), "phototagger.lnk");
            shortcut.setWorkingDirectory("c:/aims");
            shortcut.setPath("c:/aims/phototagger.bat");
            shortcut.save();
        }
    }

    public static void main(String[] args) {
        InstallImatch.installShortcut();

    }

}
