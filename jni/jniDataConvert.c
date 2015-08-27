#include "jniDataConvert.h"
#include <stdio.h>
#include <string.h>
#include <android/log.h>


#define  LOG_TAG    "NDK_TEST"
#define  LOGUNK(...)  __android_log_print(ANDROID_LOG_UNKNOWN,LOG_TAG,__VA_ARGS__)
#define  LOGDEF(...)  __android_log_print(ANDROID_LOG_DEFAULT,LOG_TAG,__VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGF(...)  __android_log_print(ANDROID_FATAL_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGS(...)  __android_log_print(ANDROID_SILENT_ERROR,LOG_TAG,__VA_ARGS__)


JNIEXPORT jstring JNICALL Java_samjung_pimonitor_com_jniconvert_jniConvert
  (JNIEnv * env, jobject thiz, jint aa, jint bb, jint cc)
{
		jintArray intJavaArray = (*env)->NewIntArray(env, 3);

		int data = (int)aa;
		int data2= (int)bb;
		int data3= (int)cc;
		int arr[3];
		LOGD("%d %d %d",data,data2,data3);
		char strBuffer[3072] = "";
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
