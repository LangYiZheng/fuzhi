//
//
//

#include <stdint.h>
#include <string>
#include "EncodeUtils.h"
#include "md5.h"
#include "Utils.h"
#include "JavaClassesDef.h"
#ifdef __cplusplus
extern "C" {
#endif
#include "aes.h"
void AES_CBC_encrypt_buffer(uint8_t* output, uint8_t* input, uint32_t length, const uint8_t* key, const uint8_t* iv);

#ifdef __cplusplus
}
#endif

using namespace std;

static void initKey(uint8_t* pKey,JNIEnv *env) {
    string oriKey = appEnv.deviceID;
    jmethodID methodGetDigest = env->GetStaticMethodID(baseClasses.jniUtilClass, SecureUtil_getRandomAESKey_Method, SecureUtil_getRandomAESKey_Param);
    jbyteArray jb = (jbyteArray)env->CallStaticObjectMethod(baseClasses.jniUtilClass, methodGetDigest);
    jbyte* jbj = env->GetByteArrayElements(jb, 0);
    jsize  size = env->GetArrayLength(jb);

//
//    jsize  oldsize = env->GetArrayLength(bb);
//    const byte* bytearr = (byte*)jbj;
//    int len = (int)oldsize;


//    const byte* md5 = MD5(oriKey).getDigest();
    memcpy(pKey, jbj, KEY_LEN);
}

static void initServerKey(uint8_t* pKey,JNIEnv *env) {
    string oriKey = appEnv.deviceID;
    jmethodID methodGetDigest = env->GetStaticMethodID(baseClasses.jniUtilClass, SecureUtil_getServerKey_Method, SecureUtil_getServerKey_Param);
    jbyteArray jb = (jbyteArray)env->CallStaticObjectMethod(baseClasses.jniUtilClass, methodGetDigest);
    jbyte* jbj = env->GetByteArrayElements(jb, 0);
    jsize  size = env->GetArrayLength(jb);


    memcpy(pKey, jbj, KEY_LEN);
}

static string getSalt() {
    string salt = appEnv.deviceID + "appKey" + appEnv.deviceID;
    return salt;
}

static void initIv(uint8_t* pIv) {
    uint8_t iv[]  = {97,21,66,17,113,47,106,116,43,25,87,97,60,13,99,74} ; // { 74,-106,-14,-127,26,-73,-70,-122,55,40,-54,-123,-2,-79,-93,-120,}
    for (int i = 0; i < KEY_LEN; i++) {
        pIv[i] = iv[i];
    }
}

jbyteArray EncodeUtils::encryptData(JNIEnv *env, jbyteArray& data, int type) {

    uint8_t iv[KEY_LEN];
    initIv(iv);

    uint8_t key[KEY_LEN];
    initKey(key,env);

    int lenOri = env->GetArrayLength(data); //源数据长度
    jbyte* jData = env->GetByteArrayElements(data, 0);

    uint8_t padding = KEY_LEN - lenOri % KEY_LEN;
    int lenOffset = lenOri + padding;  //用来加密的数据长度必须是16的倍数,不够时补齐

    uint8_t srcData[lenOffset]; //补齐后的源数据
    memcpy(srcData, jData, lenOri);

    for (int i = 0; i < padding; i++) { // PKCS5Padding/PKCS7Padding 填充
        srcData[lenOri + i] = padding;
    }

    uint8_t result[lenOffset];
    result[lenOffset];

    AES_CBC_encrypt_buffer(result, srcData, lenOffset, key, iv);

    jbyte *by = (jbyte*)result;
    jbyteArray jarray = env->NewByteArray(lenOffset);
    env->SetByteArrayRegion(jarray, 0, lenOffset, by);

    return jarray;

}

jbyteArray EncodeUtils::decryptData(JNIEnv *env, jbyteArray &data, int type) {

    uint8_t iv[KEY_LEN];
    initIv(iv);

    uint8_t key[KEY_LEN];
    initServerKey(key,env);

    int lenOri = env->GetArrayLength(data); //源数据长度
    jbyte* jData = env->GetByteArrayElements(data, 0);

    uint8_t srcData[lenOri];
    memcpy(srcData, jData, lenOri);

    uint8_t result[lenOri];
    AES_CBC_decrypt_buffer(result, srcData, lenOri, key, iv);

    int padding = result[lenOri-1];
    int resultSize = lenOri - padding;

    jbyte *by = (jbyte*)result;
    jbyteArray jarray = env->NewByteArray(resultSize);
    env->SetByteArrayRegion(jarray, 0, resultSize, by);

    return jarray;
}

jstring EncodeUtils::geneSign(JNIEnv *env, jstring &data) {
    string strData = jstring2String(env, data);
    string salt = getSalt();
    string oriSign = salt + strData + salt;
    string strSign = MD5(oriSign).toStr();

    logV(("originData: " + strData).c_str());
    logV(("salt: " + salt).c_str());
    logV(("oriSign: " + oriSign).c_str());

    return env->NewStringUTF(strSign.c_str());
}

