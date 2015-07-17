#include "jniDataConvert.h"
#include <stdio.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_samjung_pimonitor_com_jniconvert_jniConvert
  (JNIEnv * env, jobject thiz, jint aa, jint bb, jint cc)
{

		jintArray intJavaArray = (*env)->NewIntArray(env, 3);
		int data = aa;
		int data2=bb;
		int data3=cc;
		int arr[3];

		char strBuffer[3072];
		memcpy(strBuffer, (char*)&data, sizeof(int));
		memcpy(strBuffer+1024, (char*)&data2, sizeof(int));
		memcpy(strBuffer+2048, (char*)&data3, sizeof(int));

/*String to int
		arr[0] = *(int*)(strBuffer);
		arr[1] = *(int*)(strBuffer+1024);
		arr[2] = *(int*)(strBuffer+2048);
*/

		//(*env)->SetIntArrayRegion(env, intJavaArray, 0, 3, arr);

		//return intJavaArray;
		return (*env)->NewStringUTF(env, strBuffer);
}
