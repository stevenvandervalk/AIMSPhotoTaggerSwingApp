package aims.photo.uploader.tablemodels;

import aims.photo.uploader.presentationmodels.KeywordPM;
import aims.photo.uploader.presentationmodels.SimplePhotoUploaderPM;
import com.jgoodies.binding.adapter.AbstractTableAdapter;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 10/12/2007
 * Time: 11:28:49
 * To change this template use File | Settings | File Templates.
 */
public class KeywordTableModel extends AbstractTableAdapter {

    private static final String[] COLUMNS = {"Keyword"};

    private SimplePhotoUploaderPM pm;

    public boolean isShowLongnames() {
        return pm.isShowLongnames();
    }

    public KeywordTableModel  (ListModel listModel, SimplePhotoUploaderPM pm) {
        super(listModel, COLUMNS);
        this.pm=pm;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
            KeywordPM k = (KeywordPM) getRow(rowIndex);
            switch (columnIndex) {
                case 0 : if (isShowLongnames()) {
                    return k.getKeyword();

                } else {
                    return k.getShortName();

                }



                default :
                    throw new IllegalStateException("Unknown column");
            }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            KeywordPM k = (KeywordPM) getRow(rowIndex);
            switch (columnIndex) {
                case 0 : k.setKeyword((String) aValue);break;


                default :
                    throw new IllegalStateException("Unknown column");
            }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
    }


}
