import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Compressor{
	// size of matrix base: 8
	public static final int N = 8;
	// number of channel to encode JPEG. If its' value is 3, it encodes with Y, Cr, Cb channel.
	// If the number of channel is 1, it means that it encodes only using Y channel, which makes gray scale image.
	public int numOfChannel = 1;
	// file input and output directory string
	private String fileDir, outFileDir;
	// quality of JPEG image
	private int quality;
	// image buffer
	private BufferedImage image = null;
	// classes for image compressing
	private ImageWriter writer;
	private ImageInfo imgInfo;
	private DCT dct;
	private Huffman hufman;
	
	/**
	 * set input directory
	 * @param _fileDir input file full directory
	 */
	public void setFileDir(String _fileDir) {
		fileDir = _fileDir;
	}
	
	/**
	 * set output directory
	 * @param _outFileDir output file directory
	 */
	public void setOutFileDir(String _outFileDir) {
		outFileDir = _outFileDir;
	}
	
	/**
	 * set quality of JPEG image
	 * @param _quality JPEG image
	 */
	public void setQuality(int _quality) {
		quality = _quality;
	}
	
	/**
	 * set Color mode or gray mode. If mode is true, it means it will encode image using 3 color channel.
	 * If mode is false, it means that encodes gray scale image using one channel.
	 * @param _mode
	 */
	public void setMode(boolean _mode) {
		if(_mode)	numOfChannel = 3;
		else	numOfChannel = 1;
	}
	
	/**
	 * Execute image compressing process.
	 */
	public void execute() {
		try {
			image = ImageIO.read(new File(fileDir));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// import image information such as width, height and pixel data.
		imgInfo = new ImageInfo(image);
		// initiate DCT quantization matrix with passing quality value.
		dct = new DCT(quality);
		// initiate Huffman table.
		hufman = new Huffman();
		// initiate BufferedOutputStream with file directory and other essential information about image.
		writer = new ImageWriter(outFileDir, quality, dct, numOfChannel, imgInfo, hufman);
		// pass the ImageWriter class so that we can write buffer inside right after converting matrix.
		hufman.setWriter(writer);
		// write JPEG image header.
		writer.writeHeader();
		// write JPEG body after compressing.
		compress();
		// write JPEG end.
		writer.writeEnd();
		// flush stream buffer after finishing writing JPEG image.
		try{
			writer.outStream.flush();
		}catch (IOException e){
			System.out.println("IO Error: " + e.getMessage());
		}
		// release binding fileStream so we can access image.
		try{
			writer.fileStream.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * image compressing process. Starting from the upper left of the image, it compresses 8x8 block of image.
	 */
	private void compress(){
		float inputArray[][];
		float dctArray1[][] = new float[N][N];
		float dctArray2[][] = new float[N][N];
		int dctArray3[] = new int[N * N];
		
	    int lastDCvalue[] = new int[numOfChannel];
	    int MinBlockWidth, MinBlockHeight;
	    
	    // set the block width and height just in case the size of image is not multiple of 8.
		MinBlockWidth = ((imgInfo.getWidth() % 8 != 0) ? (int) (Math.floor(imgInfo.getWidth() / 8.0) + 1) * 8 : imgInfo.getWidth());
		MinBlockHeight = ((imgInfo.getHeight() % 8 != 0) ? (int) (Math.floor(imgInfo.getHeight() / 8.0) + 1) * 8 : imgInfo.getHeight());
		for (int comp = 0; comp < numOfChannel; comp++) {
			MinBlockWidth = Math.min(MinBlockWidth, imgInfo.getBlockWidth());
			MinBlockHeight = Math.min(MinBlockHeight, imgInfo.getBlockHeight());
		}
		for (int r = 0; r < MinBlockHeight; r++) {
			for (int c = 0; c < MinBlockWidth; c++) {
				int xpos = c * 8;
				int ypos = r * 8;
				for (int comp = 0; comp < numOfChannel; comp++) {
					inputArray = imgInfo.getComp()[comp];
					for (int i = 0; i < ImageWriter.numOfSample; i++) {
						for (int j = 0; j < ImageWriter.numOfSample; j++) {
							int xblockoffset = j * 8;
							int yblockoffset = i * 8;
							for (int a = 0; a < 8; a++) {
								for (int b = 0; b < 8; b++) {
									dctArray1[a][b] = inputArray[ypos + yblockoffset + a][xpos + xblockoffset + b];
								}
							}
							// DCT process
							dctArray2 = dct.getDCT(dctArray1);
							// Quantization process
							dctArray3 = dct.quantize(dctArray2);
							// Huffman encoding
							hufman.encode(dctArray3, lastDCvalue[comp]);
							lastDCvalue[comp] = dctArray3[0];
						}
					}
				}
			}
		}
		writer.flushBuffer();
	}
}
