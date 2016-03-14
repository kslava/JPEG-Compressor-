import java.util.ArrayList;

public class Huffman {
	public int[][] DC_matrix;
	public int[][] AC_matrix;
	// Huffman Prefix Table
	public Integer[] bitsDCluminance = { 0x00, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
	public Integer[] valDCluminance = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
	public Integer[] bitsACluminance = { 0x10, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d };
	public Integer[] valACluminance = {
		0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41,
		0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08, 0x23, 0x42,
		0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17,
		0x18, 0x19, 0x1a, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
		0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
		0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77,
		0x78, 0x79, 0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95,
		0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2,
		0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8,
		0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2, 0xe3, 0xe4,
		0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9,
		0xfa
	};
	public static int[] jpegNaturalOrder = { 0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5,
		12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36,
		29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61, 54, 47, 55, 62,
		63
	};
	
	public ArrayList<Integer[]> bits;
	public ArrayList<Integer[]> val;
	
	private ImageWriter writer;
	
	public Huffman() {
		bits = new ArrayList<Integer[]>();
		bits.add(bitsDCluminance);
		bits.add(bitsACluminance);
		val = new ArrayList<Integer[]>();
		val.add(valDCluminance);
		val.add(valACluminance);
		
		DC_matrix = new int[12][2];
		AC_matrix = new int[255][2];
		int[] huffsize = new int[257];
		int[] huffcode = new int[257];
		
		// Init of the DC values for the luminance
		int p = 0;
		for (int l = 1; l <= 16; l++) {
			for (int i = 1; i <= bitsDCluminance[l]; i++) {
				huffsize[p++] = l;
			}
		}
		huffsize[p] = 0;
		int lastp = p;
		
		int code = 0;
		int si = huffsize[0];
		p = 0;
		while (huffsize[p] != 0) {
			while (huffsize[p] == si) {
				huffcode[p++] = code;
				code++;
			}
			code <<= 1;
			si++;
		}
		
		for (p = 0; p < lastp; p++) {
			DC_matrix[valDCluminance[p]][0] = huffcode[p];
			DC_matrix[valDCluminance[p]][1] = huffsize[p];
		}

		// Init of the AC hufmann code for luminance matrix
		p = 0;
		for (int l = 1; l <= 16; l++) {
			for (int i = 1; i <= bitsACluminance[l]; i++) {
				huffsize[p++] = l;
			}
		}
		huffsize[p] = 0;
		lastp = p;
		
		code = 0;
		si = huffsize[0];
		p = 0;
		while (huffsize[p] != 0) {
			while (huffsize[p] == si) {
				huffcode[p++] = code;
				code++;
			}
			code <<= 1;
			si++;
		}
		for (int q = 0; q < lastp; q++) {
			AC_matrix[valACluminance[q]][0] = huffcode[q];
			AC_matrix[valACluminance[q]][1] = huffsize[q];
		}
	}
	
	public void encode(int zigzag[], int prec) {		
		// The DC portion
		int temp, temp2;
		temp = temp2 = zigzag[0] - prec;
		if (temp < 0) {
			temp = -temp;
			temp2--;
		}
		int nbits = 0;
		while (temp != 0) {
			nbits++;
			temp >>= 1;
		}
		
		writer.bufferIt(DC_matrix[nbits][0], DC_matrix[nbits][1]);
		// The arguments in bufferIt are code and size.
		if (nbits != 0) {
			writer.bufferIt(temp2, nbits);
		}

		// The AC portion
		int r = 0;
		for (int k = 1; k < 64; k++) {
			if ((temp = zigzag[jpegNaturalOrder[k]]) == 0) {
				r++;
			} else {
				while (r > 15) {
					writer.bufferIt(AC_matrix[0xF0][0], AC_matrix[0xF0][1]);
					r -= 16;
				}
				temp2 = temp;
				if (temp < 0) {
					temp = -temp;
					temp2--;
				}
				nbits = 1;
				while ((temp >>= 1) != 0) {
					nbits++;
				}
				int i = (r << 4) + nbits;
				writer.bufferIt(AC_matrix[i][0], AC_matrix[i][1]);
				writer.bufferIt(temp2, nbits);
				r = 0;
			}
		}
		// End of block (0, 0) pair
		if (r > 0) {
			writer.bufferIt(AC_matrix[0][0], AC_matrix[0][1]);
		}
	}
	
	public void setWriter(ImageWriter _writer) {
		writer = _writer;
	}
}
