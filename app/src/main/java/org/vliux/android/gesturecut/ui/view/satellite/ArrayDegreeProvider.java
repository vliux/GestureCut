package org.vliux.android.gesturecut.ui.view.satellite;

/**
 * Provide the degree between each satellite as an array of degrees. Can be provided to 
 * {@link SatelliteMenu} as a parameter.  
 *  
 * @author Siyamed SINIR
 *
 */
public class ArrayDegreeProvider implements IDegreeProvider {
	private float[] degrees;
	
	public ArrayDegreeProvider(float[] degrees) {
		this.degrees = degrees;
	}
	
	public float[] getDegrees(int count, float totalDegrees){
		if(degrees == null || degrees.length != count){
            throw new IllegalArgumentException("Provided delta degrees and the action count are not the same.");
        }
		return degrees; 
	}
}
