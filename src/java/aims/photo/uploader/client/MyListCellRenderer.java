package aims.photo.uploader.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 2/02/2007
 * Time: 14:51:56
 * To change this template use File | Settings | File Templates.
 */
public class MyListCellRenderer implements ListCellRenderer {


    public Component getListCellRendererComponent(JList jList, Object object, int i, boolean isSelected, boolean cellHasFocus) {


        if (object instanceof Photo) {
            Photo cell = (Photo) object;

            cell.setSelected(isSelected);
            cell.setHasFocus(cellHasFocus);

            return cell;
        }


        return null;

    }
}
