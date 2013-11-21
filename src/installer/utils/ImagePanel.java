package installer.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

/**
 * Create Image Panel
 */
@SuppressWarnings("unused")
public class ImagePanel extends JPanel {
    // Image
    private BufferedImage image;

    public ImagePanel(final String imagePath) {
        System.out.println("Image : " + imagePath);
        // Use SwingWorker when loading images
        new SwingWorker<BufferedImage, Void>(){
            @Override
            protected BufferedImage doInBackground() throws Exception
            {
                return ImageIO.read(installer.Installer.class.getResource(imagePath));
            }

            @Override
            protected void done()
            {
                try{ image = get(); }
                catch(InterruptedException e){ e.printStackTrace(); }
                catch(ExecutionException e){ e.printStackTrace(); }
            }
        }.execute();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
