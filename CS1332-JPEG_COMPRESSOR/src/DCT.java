public class DCT {
	// Default JPEG Quality
	private int quality = 50;
	// Quantization Matrix.
	private int[] quantizaionMatrix = new int[]{
		  16, 11, 10, 16,  24,  40,  51,  61
		, 12, 12, 14, 19,  26,  58,  60,  55
		, 14, 13, 16, 24,  40,  57,  69,  56
		, 14, 17, 22, 29,  51,  87,  80,  62
		, 18, 22, 37, 56,  68, 109, 103,  77
		, 24, 35, 55, 64,  81, 104, 113,  92
		, 49, 64, 78, 87, 103, 121, 120, 101
		, 72, 92, 95, 98, 112, 100, 103,  99
	};
	
	/**
	 * Constructor for DCT class. Get a value from parameter and change the Quantization Matrix based on the quality.
	 * @param _quality	JPEG quality
	 */
	public DCT(int _quality) {
		quality = _quality;
		// Calibrating the quality value.
		if (quality <= 0)
			quality = 1;
		if (quality > 100)
			quality = 100;
		if (quality < 50)
			quality = 5000 / quality;
		else
			quality = 200 - quality * 2;
		
		// Calibrating the Quantization Matrix based on quality value
		for (int i = 0; i < 64; i++) {
			quantizaionMatrix[i] = (quantizaionMatrix[i] * quality + 50) / 100;
			if (quantizaionMatrix[i] <= 0)
				quantizaionMatrix[i] = 1;
			if (quantizaionMatrix[i] > 255)
				quantizaionMatrix[i] = 255;
		}
	}
	
	/**
	 * Quantization process
	 * @param inputData Discrete Cosine Transform 8x8 Matrix
	 * @return Quantized 8x8 matrix
	 */
	public int[] quantize(float[][] inputData) {
		int outputData[] = new int[Compressor.N * Compressor.N];
		int index = 0;
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				outputData[index] = (int) Math.round(inputData[h][w] / quantizaionMatrix[index]);
				index++;
			}
		}
		return outputData;
	}
	
	/**
	 * get quantizaionMatrix
	 * @return quantizaionMatrix
	 */
	public int[] getQuantum(){
		return quantizaionMatrix;
	}
	
	/**
	 * Discrete Cosine Transform process
	 * @param input 8x8 original channel matrix
	 * @return DCT processed 8x8 matrix
	 */
	public float[][] getDCT(float input[][]) {
		float[][] M = getM(input);		// get level off matrix M
		float[][] T = getT();			// get T matrix
		float[][] TTrans = getTrans(T);	// get T transpose matrix
		float[][] TM = getTM(T, M);		// matrix multiplication with T and M
		float[][] D = getD(TM, TTrans);	// matrix multiplication with T and M and T transpose
		return D;
	}
	
	/**
	 * get level off matrix M (subtract 128 from original values)
	 * @param input original matrix
	 * @return M matrix which is leveled off from original matrix
	 */
	public float[][] getM(float input[][]){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				output[h][w] = input[h][w] - 128f;
			}
		}
		return output;
	}
	
	/**
	 * get T matrix
	 * @return T matrix
	 */
	public float[][] getT(){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				if(h==0){
					output[h][w] = (float) (1 / Math.sqrt((float) Compressor.N));
				}else{
					output[h][w] = (float) (  Math.sqrt( 2 / (float) Compressor.N )
							* (float) Math.cos( (2*w+1)*h*Math.PI/(2*Compressor.N) ) );
				}
			}
		}
		return output;
	}
	
	/**
	 * 
	 * @param get T transpose matrix
	 * @return	T transpose matrix
	 */
	public float[][] getTrans(float[][] T){
		float [][] result = new float[Compressor.N][Compressor.N];
		for(int i =0;i<Compressor.N;i++){
			for(int j =0;j<Compressor.N;j++){
				result[j][i] = T[i][j];
			}
		}
		return result;
	}
	
	/**
	 * get matrix multiplication with T and M
	 * @param T T matrix
	 * @param M M matrix
	 * @return TxM matrix
	 */
	public float[][] getTM(float[][] T, float[][] M){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				for(int x=0; x<Compressor.N; x++){
					output[h][w] += T[h][x] * M[x][w];
				}
			}
		}
		return output;
	}
	
	/**
	 * get matrix multiplication TxMxTtranspose
	 * @param TM TxM matrix
	 * @param T TxMxTtranspose
	 * @return get matrix multiplication TxMxTtranspose
	 */
	public float[][] getD(float[][] TM, float[][] T){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				for(int x=0; x<Compressor.N; x++){
					output[h][w] += TM[h][x] * T[x][w];
				}
			}
		}
		return output;
	}
}