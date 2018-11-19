package com.guyu.android.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.gis.app.GisQueryApplication;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 *
 */

public class SecureUtil {
    private static String deviceId;
    private static byte[] key;
    private static byte[] serverKey;
    private static RSAPublicKey publicKey = null;
    private static RSAPrivateKey privateKey = null;

    private static Size DEFAULT = Size.S256;

    private enum Size {

        S224(224), S256(256), S384(384), S512(512);

        int bits = 0;

        Size(int bits) {
            this.bits = bits;
        }

        public int getValue() {
            return this.bits;
        }
    }

    private static RSAPublicKey getPublicKey() throws Exception {
        if (publicKey == null) {
            publicKey = loadPublicKeyByStr(
                    getRSAPublicKEY());
        }
        return publicKey;
    }

    private static RSAPrivateKey getPrivateKey() throws Exception {
        if (privateKey == null) {
            privateKey = loadPrivateKeyByStr(
                    getRSAPrivateKEY());
        }
        return privateKey;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    private static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    private static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer =decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    // 公钥加密
    public static byte[] encryptOfRSA(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");// java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        return cipher.doFinal(content);
    }

    // 私钥解密
    public static byte[] decryptOfRSA(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        return cipher.doFinal(content);
    }

    /**
     * 加密字符串
     *
     * @param str
     *            需要加密的内容
     *
     * @return 加密后的字符串
     */
    public static String encodeStringOfAES(String str) {
        try {
            String base64 = encodeBase64(encryptData(str.getBytes()));
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * 解密字符串
     *
     * @param str
     *            需要解密的字符串
     * @param RSAkeyForAES
     *            客户端发过来的使用Rsa加密的aesKey
     * @return 解密后的字符串
     */
    public static String decodeStringOfAES(String str, String RSAkeyForAES) {
        try {
            byte[] deData = decodeBase64(str);
            serverKey =decryptOfRSA(decodeBase64(RSAkeyForAES));
            return new String(decryptData(deData));
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    private static void mkdirs(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

    }

    /**
     * aes加密文件
     *
     * @param localFile
     *            需要加密的内容
     * @param secretFilePath
     *            加密后的文件位置
     *
     * @return 加密后的文件
     * @throws IOException
     */
    public static File encodeFile(File localFile, String secretFilePath) throws IOException {

        File secretFile = new File(secretFilePath);
        mkdirs(localFile);
        mkdirs(secretFile);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(localFile);
            fos = new FileOutputStream(secretFile);

            byte[] b = new byte[2048];
            while (fis.read(b) != -1) {
                fos.write(encryptData(b));
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return secretFile;
    }

    /**
     * 解密文件
     *
     * @param secretFile
     *            加密文件
     * @param localFilePath
     *            存放解密的文件位置
     * @param RSAkeyForAES
     *            客户端发过来的使用Rsa加密的aesKey
     * @return 解密后的文件
     * @throws Exception
     */
    public static File decodeFile(File secretFile, String localFilePath, String RSAkeyForAES) throws Exception {
        serverKey = decryptOfRSA(decodeBase64(RSAkeyForAES));

        File localFile = new File(localFilePath);
        mkdirs(localFile);
        mkdirs(secretFile);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(secretFile);
            fos = new FileOutputStream(localFile);

            byte[] b = new byte[2064];
            while (fis.read(b) != -1) {
                fos.write(decryptData(b));
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return secretFile;
    }

    public static String encryptString(String str) {
        return encodeBase64(encryptData(str.getBytes()));
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("toURLEncoded error:",paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("toURLEncoded error:"+paramString, localException.getMessage());
        }

        return "";
    }

    public static String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("toURLDecoded error:",paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("toURLDecoded error:"+paramString, localException.getMessage());
        }

        return "";
    }



    public static String encodeBase64(byte[] bytes) {
        return Base64.encodeToString(bytes,Base64.DEFAULT);
//        return org.bouncycastle.util.encoders.Base64.toBase64String(bytes);
    }

    public static byte[] decodeBase64(String str) throws IOException {
		return Base64.decode(str,Base64.DEFAULT);
//        return org.bouncycastle.util.encoders.Base64.decode(str);
    }


    public static byte[] getServerKey(){
        return serverKey;
    }


    /**
     *
     * @return AES密码 发给服务端请加密 encryptRSA()
     * @throws Exception
     */
    public static byte[] getRandomAESKey() {
        if(key ==null){
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[256];
            sr.nextBytes(salt);
            key  =  getDigest(salt, DEFAULT);
        }
        return key;
    }
    public static byte[] getDigest(byte[] b) {
        return getDigest(b, DEFAULT);
    }

    public static byte[] getDigest(byte[] b, Size s) {
        Size size = s == null ? DEFAULT : s;

        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(size.getValue());

        md.update(b);
        byte[] digest = md.digest();

        byte[] digest2 = new byte[digest.length / 2];
        for (int i = 0; i < digest2.length; i++) {
            digest2[i] = digest[i];
        }
        return digest2;
    }

    static {
        System.loadLibrary("native-lib");
    }

    public static byte[] encryptData(byte[] data) {
        return encryptData(GisQueryApplication.getApp(), data);
    }

    public static byte[] decryptData(byte[] data) {
        return decryptData(GisQueryApplication.getApp(), data);
    }

    public static String getSign(String data) {
        return getSign(GisQueryApplication.getApp(), data);
    }

    public static String getRSAPublicKEY() {
        return getRSAPublicKEY(GisQueryApplication.getApp());
    }

    public static String getRSAPrivateKEY() {
        return getRSAPrivateKEY(GisQueryApplication.getApp());
    }


    public static String getDeviceId() {
        if (deviceId == null) {
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[256];
            sr.nextBytes(salt);
            deviceId = Base64.encodeToString(
                    salt, 0);
        }
        return deviceId;
    }




    public static String getAppVersion() {
        return "1.0";
    }

    public static String getChannel() {
        return "huawei";
    }


    native private static byte[] encryptData(Context context, byte[] data);

    native private static byte[] decryptData(Context context, byte[] data);

    native private static String getSign(Context context, String data);

    native private static String getRSAPublicKEY(Context context);

    native private static String getRSAPrivateKEY(Context context);
}
