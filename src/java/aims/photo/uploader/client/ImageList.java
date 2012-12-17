package aims.photo.uploader.client;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 12/12/2007
 * Time: 14:16:15
 * To change this template use File | Settings | File Templates.
 */
public class ImageList extends JList implements PropertyChangeListener {


    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Action");
    }
}
