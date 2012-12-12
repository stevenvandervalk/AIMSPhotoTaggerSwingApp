package aims.photo.uploader.client;

import aims.app.generic.logger.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 19/09/2008
 * Time: 10:36:35
 * To change this template use File | Settings | File Templates.
 */
public class LargePhoto extends JFrame {

    LargePhotoForm form = new  LargePhotoForm();
    JList imageList;

    public LargePhoto(JList imageList) throws HeadlessException {
        super();
        this.imageList = imageList;
        this.setSize(800, 600);
        this.setContentPane(form.getMainPanel());
        initActions();
    }

    private void initActions() {
        form.getNextButton().addActionListener(new NextListener());
        form.getPreviousButton().addActionListener(new PreviousListener());
    }

    public void setPhoto() {
        Photo photo = (Photo) imageList.getSelectedValue();
        if (photo == null) {
            return;
        }
//        if (photo.getFileName().equals(photo.getFileName())) {
//            return;
//        }

        try {
            form.getImagePanel1().setImage(ImageIO.read(photo.getOriginal()));
        } catch (IOException e) {
            LoggerFactory.LogSevereException(e); //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException(e);
        };



        form.getImagePanel1().repaint();



    }


    private class NextListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = imageList.getSelectedIndex();
            if (index >= imageList.getModel().getSize()-1) {
                index = 0;
            } else {
                index ++;
            }

            imageList.setSelectedIndex(index);
            setPhoto();
        }
    }

    private class PreviousListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = imageList.getSelectedIndex();
            if (index <= 0 ) {
                index = imageList.getModel().getSize()-1;
            } else {
                index --;
            }

            imageList.setSelectedIndex(index);
            setPhoto();
        }
    }

}
