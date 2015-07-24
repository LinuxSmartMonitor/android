
package samjung.pimonitor.com;

public class jniconvert {
	public native byte[] jniConvert(int aa, int bb, int cc);
	 static
		{
			System.loadLibrary("jniDataConvert");
		}
}
