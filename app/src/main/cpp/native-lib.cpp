//
//
//

#include <jni.h>
#include <string>
#include "Utils.h"
#include "EnvChecker.h"
#include "JavaClassesDef.h"
#include "EncodeUtils.h"
#include <sys/ptrace.h>


static jbyteArray encrypt(JNIEnv *env, jclass instance, jobject contextObject, jbyteArray data) {

    if (EnvChecker::isValid(env, contextObject)) {
        return EncodeUtils::encryptData(env, data, 1);
    } else {
        showToast(env, env->NewStringUTF("encryptData非法调用"));
        return data;
    }
}

static jbyteArray decrypt(JNIEnv *env, jclass instance, jobject contextObject, jbyteArray data) {

    if (EnvChecker::isValid(env, contextObject)) {
        return EncodeUtils::decryptData(env, data, 1);
    } else {
        showToast(env, env->NewStringUTF("decryptData非法调用"));
        return data;
    }
}

static jstring geneSign(JNIEnv *env, jclass instance, jobject contextObject, jstring str) {

    if (EnvChecker::isValid(env, contextObject)) {
        const char *data = env->GetStringUTFChars(str, 0);
        jstring temp = EncodeUtils::geneSign(env, str);
        env->ReleaseStringUTFChars(str, data);
        return temp;
    } else {
        showToast(env, env->NewStringUTF("getSign非法调用"));
        return str;
    }
}


static jstring getRSAPublicKEY(JNIEnv *env, jclass instance, jobject contextObject) {

    if (EnvChecker::isValid(env, contextObject)) {
        // 服务器端加密公钥
        string rsaPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzf2LDcdoylEkq28VBJjfIetTkNVcLYNe\n"
                "yfYpaMqAjiiUUqOKk9OxewA/5i8E9cbAZw4GMD/osLCIbuXCldnZggq03uDFWZBL7B2pKiJPYvI5\n"
                "8xCr0fbWheIz8YBEz9h1Zn9RRUL66VKnUiX/KYLSDjrXNDZwSS8pXJUZ2+rwehNKkBE8nUP7vni3\n"
                "fzkLDGIZgE2cfgw1KDK394EhlN0mGVhYg+sMTihIRiCe4FUCKvkBR/YAaWkSMaPEPAUeachyHHvY\n"
                "qDtvhWJzFDhlwhx6yTusc0iLIb7vHX1YNwqBMoFUN64EIVEqkr+0aIhxHp1SlYiDYErJfCVz+X+w\n"
                "DIiO9wIDAQAB";
//        logV(("rsaPublicKey: " + rsaPublicKey).c_str());

        return env->NewStringUTF(rsaPublicKey.c_str());
    } else {
        showToast(env, env->NewStringUTF("getRSAPublicKEY非法调用"));
        return env->NewStringUTF("");
    }
}

static jstring getRSAPrivateKEY(JNIEnv *env, jclass instance, jobject contextObject) {

    if (EnvChecker::isValid(env, contextObject)) {
        // 客户端解密私钥
        string rsaPrivateKey ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDBX9o+jhYIAo74QK7t275eC5e8\n"
                "2LBGhTxz5Peu6KrYKtwmAth/Ky3aDUIb4zRKmcw5yJPzBD9O4BsFmeVS9rab16SV+6vsWV/2zYqz\n"
                "Zh1rLp1rSEAoAc6maGCvH7kGEQYt1VsZ9pWLG61sfh9/p8NUgmbwbRVdh28sAVVns4xgpu1EgohM\n"
                "K9gJfE+iOKJuUD244kJRn+ZGi9ljGYdMSlyPQco1tB3kdqR2EtkeszYERE+YE7NUVOUE8yihExVM\n"
                "qhOy9tOBFyUK1Zh+/GN75+je/jVx91bAx03rzw/Yl6MMarSqrXcUDGUgRvnPDjoj5WeznJrlkdt+\n"
                "Or/RdcSWqhNHAgMBAAECggEAW9Q6/c3XpIIihr2g1I+vvVVf/2wfeb230YmAHW2cA/+Gf/6F9Q36\n"
                "PjxhkoC4ylxy4VXg/Fyd+8Ts0CIfBuDmIueq3xaetCRVMAy3uy3Y9pcyZknvPZBuJBQZJdC4uqrG\n"
                "9mkFVDUHGwJPvt+98Y8ncTBj1fTXWo0APDN97TsUhKitS24ITSs0R2emcgk8mEaXx/BchCulPjdG\n"
                "OQKTES86ZbOFF4aIk6p3DvUHImuHDJs9TwWPisa04IY/BGIMhpqKffit/3f+juU+1qqv7QpAF6QP\n"
                "LGyM7JzAZ3BqpkuvCbEFLRSc6kVnxLOEEYIdeQ/JlObtUO7J74LA6QOXc9KcEQKBgQDkfPEwgYwz\n"
                "LTtieiDgHON67xpro8LspX3gLa+P9daZEMI4rg/QdW0el8qR4q9Pa3HBhyxRBLKZEUDSeqqqa0JD\n"
                "T16JYbZMqfjpmyK43Uba/ShkSBCgLay++aDKHa1nRk08pn2y5jtweH0vFPdL//6mfo1pBgnZrXpo\n"
                "Cq7awhbdKwKBgQDYqIvEo/NR/V7vWOPmK23HyPksLDEpY30/PWcaXVv+rbbnh3KNejL5deGSToBL\n"
                "R0EPQbEGJcvKOP9AIgXmLaHpSs84kVZMtxoQwpKwubCwr5ufLi5Ma7SRYRbYSieJbffWXT1a7qPp\n"
                "VgOpWuZ9QfKcYiX+oNWNQUG2jgg0gg3sVQKBgQDUjiK1tB5f0XlIIEMLGfTI/Tv/3KVODpot/XIw\n"
                "I0vSsxNrNQoDK+oqJp1M36uk31hA5+XjLNKUsMczxueTiBppkaFpflvfr9OeRjKj9T2gJRf5Sk0l\n"
                "JbhpJMJepAMXawAZJu0SiznKZFxYe1GfAvO7oUkd5X8uYELaHO4TFbG9ZwKBgB0hrIloMhNYKies\n"
                "oBgWxtASKc7SslmKUxDpxK861l/MI8fF4pU7VRdNzmLJw15b2lee0ZquAlTSjR+mc3ybriWcNXi5\n"
                "sgzmoc6XGvpAPY3ETvx8TSVhZgoWL+gtMFu+OZDNQK61X7zEIAHpgwxcja6RU+KE5bH0kE1nk5rb\n"
                "0dtxAoGAHZ4g6+ILr7OiQs0dWP4GmtfokE78vcfLkkNC+Rc1o3XsYYMGf6vH5j13IaEbCyZuBcip\n"
                "DtDAmT6S9fpXgyzT6zimxjsQieFKkn5bQNashZaUJeehkhr4n0A8KYIFBgdZcHWLLVLAOotJM4c0\n"
                "n89zv7EkOm9nzd/ZbDcvNU7qlIY=";

//        logV(("rsaPrivateKey: " + rsaPrivateKey).c_str());

        return env->NewStringUTF(rsaPrivateKey.c_str());
    } else {
        showToast(env, env->NewStringUTF("getRSAPrivateKEY非法调用"));
        return env->NewStringUTF("");
    }
}

static JNINativeMethod nMethods[] = {
        {SecureUtil_encryptData_Method, SecureUtil_encryptData_Param, (void *) encrypt},
        {SecureUtil_decryptData_Method, SecureUtil_decryptData_Param, (void *) decrypt},
        {SecureUtil_getSign_Method, SecureUtil_getSign_Param, (void *) geneSign},
        {SecureUtil_getRSAPublicKEY_Method, SecureUtil_getRSAPublicKEY_Param,
         (void *) getRSAPublicKEY},
        {SecureUtil_getRSAPrivateKEY_Method, SecureUtil_getRSAPrivateKEY_Param,
         (void *) getRSAPrivateKEY}
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    //防止动态调试
    ptrace(PTRACE_TRACEME, 0, 0, 0);

    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
        return result;

    baseClasses.contextClass = (jclass) env->NewGlobalRef(
            env->FindClass("android/content/Context"));
    baseClasses.signatureClass = (jclass) env->NewGlobalRef(
            env->FindClass("android/content/pm/Signature"));
    baseClasses.packageManagerClass = (jclass) env->NewGlobalRef(
            env->FindClass("android/content/pm/PackageManager"));
    baseClasses.packageInfoClass = (jclass) env->NewGlobalRef(
            env->FindClass("android/content/pm/PackageInfo"));
    baseClasses.jniUtilClass = (jclass) env->NewGlobalRef(env->FindClass(SecureUtil_Clz));

    initAppEnv(env);

    env->RegisterNatives(baseClasses.jniUtilClass, nMethods,
                         sizeof(nMethods) / sizeof(nMethods[0]));

    gIsValid = false;

    logV("JNI OnLoad");

    return JNI_VERSION_1_4;
}