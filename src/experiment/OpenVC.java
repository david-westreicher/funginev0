package experiment;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import util.Log;

//import com.googlecode.javacv.FFmpegFrameGrabber;
//import com.googlecode.javacv.FrameGrabber;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OpenVC extends Thread {

	public static String MUTEX = "";
	public static int width;
	public static int height;
	public static Buffer data;
	public static BufferedImage bi;

	public void run() {
//		FrameGrabber grabber = new FFmpegFrameGrabber("C:\\lena.avi");
//		try {
//			grabber.start();
//			IplImage grabbedImage = grabber.grab();
//			while ((grabbedImage = grabber.grab()) != null) {
//				width = grabbedImage.width();
//				height = grabbedImage.height();
//				synchronized (MUTEX) {
//					data = grabbedImage.getIntBuffer();
//					bi = grabbedImage.getBufferedImage();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
