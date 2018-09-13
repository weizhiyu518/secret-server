package com.secret.uitil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class AESUtil {

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding"; //"算法/模式/补码方式"
    private static final String ENCODING = "utf-8";

    public static String generateAESKey() throws UnsupportedEncodingException {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        byte[] keyExternal = key.getEncoded();
        return Base64Utils.encodeToString(keyExternal);
    }

    // 加密
    public static String encrypt(String src, String key, String iv) {
        if (StringUtils.isBlank(key)) {
            log.error("key is null");
            return null;
        }
        try {
            byte[] raw = new BASE64Decoder().decodeBuffer(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            if (StringUtils.isNotBlank(iv)) {
                IvParameterSpec ivObj = new IvParameterSpec(iv.getBytes(ENCODING));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivObj);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            }
            byte[] encrypted = cipher.doFinal(src.getBytes(ENCODING));
            return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            log.error("encrypt err, src={} key={} iv={}", src, key, iv, e);
            return null;
        }

    }

    // 解密
    public static String decrypt(String src, String key, String iv) {
        try {
            // 判断Key是否正确
            if (StringUtils.isBlank(key)) {
                log.error("key is null");
                return null;
            }
            byte[] raw = Base64Utils.decodeFromString(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            if (StringUtils.isNotBlank(iv)) {
                IvParameterSpec ivObj = new IvParameterSpec(iv.getBytes(ENCODING));
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivObj);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            }
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(src);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, ENCODING);
            return originalString;
        } catch (Exception ex) {
            log.error("decrypt err, src={} key={} iv={}", src, key, iv, ex);
            return null;
        }
    }

    // 加密
    public static String Encrypt(String src, String key, String iv) {
        if (key == null) {
            log.error("key is null");
            return null;
        }
//        // 判断Key是否为16位
//        if (key.length() != 16) {
//            log.error("len of key is not 16, {}", key);
//            return null;
//        }

        try {
            byte[] raw = Base64Utils.decodeFromString(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
            if (StringUtils.isNotBlank(iv)) {
                IvParameterSpec ivObj = new IvParameterSpec(iv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivObj);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            }
            byte[] encrypted = cipher.doFinal(src.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            log.error("encrypt err, src={} key={} iv={}", src, key, iv, e);
            return null;
        }

    }

    // 解密
    public static String Decrypt(String src, String key, String iv) {
        try {
            // 判断Key是否正确
            if (key == null) {
                log.error("key is null");
                return null;
            }
            // 判断Key是否为16位
//            if (key.length() != 16) {
//                log.error("len of key is not 16, {}", key);
//                return null;
//            }
            byte[] raw = Base64Utils.decodeFromString(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (StringUtils.isNotBlank(iv)) {
                IvParameterSpec ivObj = new IvParameterSpec(iv.getBytes());
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivObj);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            }
            byte[] encrypted1 = Base64.decodeBase64(src);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                log.error("decrypt err, src={} key={} iv={}", src, key, iv, e);
                return null;
            }
        } catch (Exception ex) {
            log.error("decrypt err, src={} key={} iv={}", src, key, iv, ex);
            return null;
        }
    }

    public static void testNewAes() throws UnsupportedEncodingException {
        String cKey = generateAESKey();
        System.out.println(cKey);
        // 需要加密的字串
        String cSrc = "Email : arix04@xxx.com";
        System.out.println(cSrc);
        // 加密
        long lStart = System.currentTimeMillis();
        String enString = encrypt(cSrc, cKey, "1234567890123456");
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
        String DeString = decrypt(enString, cKey, "1234567890123456");
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

    public static void main(String[] args) throws Exception {
        testNewAes();
        /*
         * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
         * 此处使用AES-128-CBC加密模式，key需要为16位。
         */
        String cKey = "1234567890123456";
        System.out.println(cKey);
        // 需要加密的字串
        String cSrc = "Email : arix04@xxx.com";
        System.out.println(cSrc);
        // 加密
        long lStart = System.currentTimeMillis();
        String enString = Encrypt(cSrc, cKey, "1234567890123456");
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
        String DeString = Decrypt(enString, cKey, "1234567890123456");
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

}
