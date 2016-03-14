import java.awt.AWTException;
import java.awt.Image;
import java.awt.image.PixelGrabber;

public class ImageInfo {
	// number of channel to encode JPEG. If its' value is 3, it encodes with Y, Cr, Cb channel.
	// If the number of channel is 1, it means that it encodes only using Y channel, which makes gray scale image.
	public static final int numOfChannel = 3;
	
	public Image image;
	public float[][][] rgbComp;
	public float[][][] yccComp;
	public int width, height;
	public int compWidth, compHeight;
	public int blockWidth, blockHeight;
	
	/**
	 * Analyze image data such as width and height, and convert RGB data into YCrCb data.
	 * @param _image
	 */
	public ImageInfo(Image _image){
		image = _image;
		width = image.getWidth(null);
		height = image.getHeight(null);
		// convert RGB data into YCrCb.
		getYCC();
	}
	
	/**
	 * Convert RGB data into YCrCb.
	 */
	private void getYCC(){
		yccComp = new float[numOfChannel][][];
		int values[] = new int[width * height];
		PixelGrabber grabber = new PixelGrabber(image.getSource(), 0, 0, width, height, values, 0, width);
		
		compWidth = ((width % 8 != 0) ? ((int) Math.ceil(width / 8.0)) * 8 : width);
		blockWidth = (int) Math.ceil(compWidth / 8.0);
		compHeight = ((height % 8 != 0) ? ((int) Math.ceil(height / 8.0)) * 8 : height);
		blockHeight = (int) Math.ceil(compHeight / 8.0);
		
		try {
			if(grabber.grabPixels() != true){
				try{
					throw new AWTException("Grabber returned false: " + grabber.status());
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
		// initiate RGB 8x8 data array
		float R[][] = new float[compHeight][compWidth];
		float G[][] = new float[compHeight][compWidth];
		float B[][] = new float[compHeight][compWidth];
		// initiate YCrCb 8x8 data array
		float Y[][] = new float[compHeight][compWidth];
		float Cb[][] = new float[compHeight][compWidth];
		float Cr[][] = new float[compHeight][compWidth];
		
		int index = 0;
		for (int h=0; h<height; h++){
			for (int w=0; w<width; w++){
				// get RGB value of image
				R[h][w] = ((values[index] >> 16) & 0xff);
				G[h][w] = ((values[index] >> 8) & 0xff);
				B[h][w] = (values[index] & 0xff);
				// convert into YCrCb data
				Y[h][w] = (float) ((0.299 * R[h][w] + 0.587 * G[h][w] + 0.114 * B[h][w]));
				Cb[h][w] = 128 + (float) ((-0.16874 * R[h][w] - 0.33126 * G[h][w] + 0.5 * B[h][w]));
				Cr[h][w] = 128 + (float) ((0.5 * R[h][w] - 0.41869 * G[h][w] - 0.08131 * B[h][w]));
				
				index++;
			}
		}
		yccComp[0] = Y;
		yccComp[1] = Cb;
		yccComp[2] = Cr;
	}
	
	/**
	 * get image width
	 * @return
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * get image height
	 * @return
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * get image block width which is multiple of 8
	 * @return
	 */
	public int getBlockWidth(){
		return blockWidth;
	}
	
	/**
	 * get image block height which is multiple of 8
	 * @return
	 */
	public int getBlockHeight(){
		return blockHeight;
	}
	
	/**
	 * get YCrCb image data
	 * @return
	 */
	public float[][][] getComp(){
		return yccComp;
	}
}
