package aims.photo.uploader.client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;



/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 3/11/2008
 * Time: 16:25:52
 * To change this template use File | Settings | File Templates.
 */
public class LeftDotRenderer extends DefaultTableCellRenderer
{
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        super.getTableCellRendererComponent(table, value,
            isSelected, hasFocus, row, column);

        int availableWidth = table.getColumnModel().getColumn(column).getWidth();
        availableWidth -= table.getIntercellSpacing().getWidth();
        Insets borderInsets = getBorder().getBorderInsets((Component)this);
        availableWidth -= (borderInsets.left + borderInsets.right);
        String cellText = getText();
        FontMetrics fm = getFontMetrics( getFont() );

        if (fm.stringWidth(cellText) > availableWidth)
        {
            String dots = "...";
            int textWidth = fm.stringWidth( dots );
            int nChars = cellText.length() - 1;
            for (; nChars > 0; nChars--)
            {
                textWidth += fm.charWidth(cellText.charAt(nChars));

                if (textWidth > availableWidth)
                {
                    break;
                }
            }

            setText( dots + cellText.substring(nChars + 1) );
        }

        return this;
    }
}
