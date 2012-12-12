package aims.photo.uploader.client;

import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 3/02/2007
 * Time: 12:47:54
 * To change this template use File | Settings | File Templates.
 */
public class MetadataTableModel extends AbstractTableModel {

    private List tags;

    public MetadataTableModel(List tags) {
        this.tags=tags;
    }


    public int getColumnCount() {
            return 2;
        }
        public int getRowCount() {
            return tags.size();
        }
        public Object getValueAt(int row, int col) {
            Tag tag = (Tag)tags.get(row);

            if (col == 0)
                return tag.getTagName();
            else {
                try  {
                    return tag.getDescription();
                }
                catch (MetadataException e) {
                    return "<none>";
                }
            }
        }

        public void changeTagValues(java.util.List tagValues) {
            this.tags = tagValues;
            fireTableDataChanged();
        }
}
