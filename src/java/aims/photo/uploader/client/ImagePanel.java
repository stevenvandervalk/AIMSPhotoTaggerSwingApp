package aims.photo.uploader.client;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: mrehbein
 * Date: 2/02/2007
 * Time: 13:11:01
 * To change this template use File | Settings | File Templates.
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel() {
        super();
    }

    public ImagePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
}

    public ImagePanel(LayoutManager layout) {
        super(layout);
    }

    public ImagePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public ImagePanel(BufferedImage image) {
        if (image == null) {
            System.out.println("image is null");

        } else {
            System.out.println("image not null");
        }
        this.image = image;


    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void paintComponent(Graphics g) {

        System.out.println("painting image panel");

//        int pw=this.getParent().getWidth();
//        int ph=this.getParent().getHeight();
        int pw=this.getWidth();
        int ph=this.getHeight();

        setSize(pw, ph);

        System.out.println("parent size " + pw + " " + ph);

        System.out.println("image panel size " + this.getWidth() + " " + this.getHeight());

        Graphics2D g2 = (Graphics2D) g;

        BufferedImage thumbImage = this.image;

        int cellWidth = pw;
        int cellHeight = ph;

        Dimension limits = new Dimension(cellWidth, cellHeight);

        System.out.println("about to paint thumb " + limits);


        if (thumbImage != null) {


            int w = thumbImage.getWidth();
            int h = thumbImage.getHeight();
            System.out.println("thumb size " + w + " " + h);

            Dimension dim = Utils.getScaledSize(limits, w, h);
            int x = (cellWidth - dim.width) / 2;
            int y = (cellHeight - dim.height) / 2;

            System.out.println("drawing image " + x + " " + y + " " + dim.width + " " + dim.height);

            g2.clearRect(0, 0, cellWidth, cellHeight);

            g2.drawImage(thumbImage, x, y, dim.width, dim.height, this);

        }

    }

}
