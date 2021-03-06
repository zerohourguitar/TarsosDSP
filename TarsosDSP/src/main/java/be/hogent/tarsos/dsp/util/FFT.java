/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -----------------------------------------------------------
*
*  TarsosDSP is developed by Joren Six at 
*  The School of Arts,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
* -----------------------------------------------------------
*
*  Info: http://tarsos.0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://tarsos.0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/

package be.hogent.tarsos.dsp.util;


/**
 * Wrapper for calling a hopefully Fast Fourier transform. Makes it easy to
 * switch FFT algorithm with minimal overhead.
 * Support for window functions is also present.
 * 
 * @author Joren Six
 */
public final class FFT {

	/**
	 * Forward FFT.
	 */
	private final FloatFFT fft;
	private final WindowFunction windowFunction;
	private final int fftSize;

	public FFT(final int size) {
		this(size,null);
	}
	
	/**
	 * Create a new fft of the specified size. Apply the specified window on the samples before a forward transform. 
	 * arning: the window is not applied in reverse when a backwards transform is requested.
	 * @param size The size of the fft.
	 * @param windowFunction Apply the specified window on the samples before a forward transform. 
	 * arning: the window is not applied in reverse when a backwards transform is requested.
	 */
	public FFT(final int size, final WindowFunction windowFunction){
		fft = new FloatFFT(size);
		fftSize = size;
		this.windowFunction = windowFunction;
	}

	/**
	 * Computes forward DFT.
	 * 
	 * @param data
	 *            data to transform.
	 */
	public void forwardTransform(final float[] data) {
		if(windowFunction!=null){
			windowFunction.apply(data);
		}
		fft.realForward(data);
	}

	/**
	 * Computes inverse DFT.
	 * Warning, does not reverse the window function.
	 * @param data
	 *            data to transform
	 */
	public void backwardsTransform(final float[] data) {
		fft.realInverse(data, true);
	}

	public double binToHz(final int binIndex, final float sampleRate) {
		return binIndex * sampleRate / (double) fftSize;
	}

	/**
	 * Returns the modulus of the element at index bufferCount. The modulus,
	 * magnitude or absolute value is (a²+b²) ^ 0.5 with a being the real part
	 * and b the imaginary part of a complex number.
	 * 
	 * @param data
	 *            The FFT transformed data.
	 * @param index
	 *            The index of the element.
	 * @return The modulus, magnitude or absolute value of the element at index
	 *         bufferCount
	 */
	public float modulus(final float[] data, final int index) {
		final int realIndex = 2*index;
		final int imgIndex = 2 * index + 1;
		final float modulus = data[realIndex] * data[realIndex] + data[imgIndex] * data[imgIndex];
		return (float) Math.pow(modulus, 0.5);
	}

	/**
	 * Calculates the the modulus for each element in data and stores the result
	 * in amplitudes.
	 * 
	 * @param data
	 *            The input data.
	 * @param amplitudes
	 *            The output modulus info or amplitude.
	 */
	public void modulus(final float[] data, final float[] amplitudes) {
		assert data.length / 2 == amplitudes.length;
		for (int i = 0; i < amplitudes.length; i++) {
			amplitudes[i] = modulus(data, i);
		}
	}
}
