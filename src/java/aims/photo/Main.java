package java.aims.photo;

import aims.app.generic.GlobalParamaters;
import aims.app.generic.gui.SwingPleaseWait;
import aims.app.generic.logger.LoggerFactory;
import aims.photo.uploader.client.Uploader;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 7/08/2008
 * Time: 14:57:15
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) {
        LoggerFactory.LogInfo("Args = " + +args.length + args);

        if (args.length == 0) {
            InstallImatch.installShortcut();
            GlobalParamaters.setPleaseWait(new SwingPleaseWait());
            GlobalParamaters.getPleaseWait().setMessage("Initializing");
            GlobalParamaters.getPleaseWait().show();

            Uploader gui = new Uploader();

        } else {
            if (args.length == 1) {
                System.out.println("Goto Dir " + args[0]);


                GotoDirectory gotoDirectory = new GotoDirectory();
                gotoDirectory.getConnection();
                gotoDirectory.sendMessage(args[0]);


            }

            if (args.length == 2) {
                System.out.println("Goto Dir " + args[1]);


                GotoDirectory gotoDirectory = new GotoDirectory();
                gotoDirectory.getConnection();
                gotoDirectory.sendMessage(args[1]);


            }

            System.exit(1);

        }


    }


}
