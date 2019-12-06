package com.horowitz.commons;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MyImageIO {

  private MyImageIO() {

  }

  public static void writeImageTS(BufferedImage image, String filename) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH-mm-ss-SSS");
    String date = sdf.format(Calendar.getInstance().getTime());

    int ind = filename.indexOf(".");
    String ext = ".jpg";
    if (ind > 0) {
      ext = filename.substring(ind);
      filename = filename.substring(0, ind);
    }
    String filename2 = filename + " " + date + ext;
    writeImage(image, filename2);
  }

  public static void writeImage(final BufferedImage image, final String filename) {
//    new Thread(new Runnable() {
//      public void run() {
        try {
          int ind = filename.lastIndexOf("/");
          if (ind > 0) {
            String path = filename.substring(0, ind);
            File f = new File(path);
            f.mkdirs();
          }

          File file = new File(filename);
          write(image, filename.substring(filename.length() - 3).toUpperCase(), file);
        } catch (IOException e) {
          e.printStackTrace();
        }

//      }
//    }).start();
  }

  public static void writeScreen(String filename) {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    writeArea(new Rectangle(new Point(0, 0), screenSize), filename);
  }

  public static void writeArea(Rectangle rect, String filename) {
    try {
      writeImage(new Robot().createScreenCapture(rect), filename);
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  public static void writeAreaTS(Rectangle rect, String filename) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH-mm-ss-SSS");
    String date = sdf.format(Calendar.getInstance().getTime());
    int ind = filename.indexOf(".");
    String ext = ".jpg";
    if (ind > 0) {
      ext = filename.substring(ind);
      filename = filename.substring(0, ind);
    }
    String filename2 = filename + " " + date + ext;

    writeArea(rect, filename2);
  }

  private static ImageWriter getWriter(RenderedImage im, String formatName) {
    ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(im);
    Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);

    if (iter.hasNext()) {
      return iter.next();
    } else {
      return null;
    }
  }

  public static boolean write(RenderedImage im, String formatName, File output) throws IOException {
    if (output == null) {
      throw new IllegalArgumentException("output == null!");
    }
    ImageOutputStream stream = null;

    ImageWriter writer = getWriter(im, formatName);
    if (writer == null) {
      /*
       * Do not make changes in the file system if we have no appropriate writer.
       */
      return false;
    }

    try {
      // output.delete();
      stream = ImageIO.createImageOutputStream(output);
    } catch (IOException e) {
      throw new IIOException("Can't create output stream!", e);
    }

    try {
      return doWrite(im, writer, stream);
    } finally {
      stream.close();
    }
  }

  private static boolean doWrite(RenderedImage im, ImageWriter writer, ImageOutputStream output) throws IOException {
    if (writer == null) {
      return false;
    }
    writer.setOutput(output);
    try {
      writer.write(im);
    } finally {
      writer.dispose();
      output.flush();
    }
    return true;
  }
}
