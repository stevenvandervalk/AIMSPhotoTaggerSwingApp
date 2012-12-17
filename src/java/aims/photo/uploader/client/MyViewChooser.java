package aims.photo.uploader.client;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 12/12/2007
 * Time: 12:02:14
 * To change this template use File | Settings | File Templates.
 */

/*
Java Swing, 2nd Edition
By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
ISBN: 0-596-00408-7
Publisher: O'Reilly
*/
// MyViewChooser.java
//An example that uses custom file views to show thumbnails of graphic files
//rather than the regular file icon. (see ThumbnailFileView.java)
//

import javax.swing.*;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.io.File;

//ThumbNailFileView.java
//A simple implementation of the FileView class that provides a 16x16 image of
//each GIF or JPG file for its icon. This could be SLOW for large images, as we
//simply load the real image and then scale it.
//

    class ThumbNailFileView extends FileView {

      private Icon fileIcon = MetalIconFactory.getTreeLeafIcon();

      private Icon folderIcon = MetalIconFactory.getTreeFolderIcon();

      private Component observer;

      public ThumbNailFileView(Component c) {
        // We need a component around to create our icon's image
        observer = c;
      }

      public String getDescription(File f) {
        // We won't store individual descriptions, so just return the
        // type description.
        return getTypeDescription(f);
      }

      public Icon getIcon(File f) {
        // Is it a folder?
        if (f.isDirectory()) {
          return folderIcon;
        }

        // Ok, it's a file, so return a custom icon if it's an image file
        String name = f.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".gif")) {
          return new Icon16(f.getAbsolutePath());
        }

        // Return the generic file icon if it's not
        return fileIcon;
      }

      public String getName(File f) {
        String name = f.getName();
        return name.equals("") ? f.getPath() : name;
      }

      public String getTypeDescription(File f) {
        String name = f.getName().toLowerCase();
        if (f.isDirectory()) {
          return "Folder";
        }
        if (name.endsWith(".jpg")) {
          return "JPEG Image";
        }
        if (name.endsWith(".gif")) {
          return "GIF Image";
        }
        return "Generic File";
      }

      public Boolean isTraversable(File f) {
        // We'll mark all directories as traversable
        return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
      }

      public class Icon16 extends ImageIcon {
        public Icon16(String f) {
          super(f);
          Image i = observer.createImage(100, 100);
          i.getGraphics().drawImage(getImage(), 0, 0, 100, 100, observer);
          setImage(i);
        }

        public int getIconHeight() {
          return 100;
        }

        public int getIconWidth() {
          return 100;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
          g.drawImage(getImage(), x, y, c);
        }
      }
    }

