package aims.photo.uploader.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 3/02/2007
 * Time: 14:20:22
 * To change this template use File | Settings | File Templates.
 */
public class ToolbarButton extends JButton {
  private static final Insets margins =
    new Insets(0, 0, 0, 0);

  public ToolbarButton(Icon icon) {
    super(icon);
    setMargin(margins);
    setVerticalTextPosition(BOTTOM);
    setHorizontalTextPosition(CENTER);
  }

  public ToolbarButton(String imageFile) {
    this(new ImageIcon(imageFile));
  }

  public ToolbarButton(String imageFile, String text) {
    this(new ImageIcon(imageFile));
    setText(text);
  }
}
