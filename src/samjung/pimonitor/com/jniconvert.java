package samjung.pimonitor.com;

public class jniconvert {
	public native String jniConvert(int aa, int bb, int cc);
	 static
		{
			System.loadLibrary("jniDataConvert");
		}
}
