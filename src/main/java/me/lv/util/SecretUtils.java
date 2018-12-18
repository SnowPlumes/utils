package me.lv.util;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;

/**
 *
 * @author lzw
 * @date 2017/11/19
 */
public class SecretUtils {

    private SecretUtils() {
    }

    /**
     * MD5加密
     *
     * @param string 需要加密的字符串
     * @return 加密后字符串
     */
    public static String getMD5(String string) {
        //用于加密的字符
        char[] md5String = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = string.getBytes();

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest()执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = md5String[byte0 >>> 4 & 0xf];
                str[k++] = md5String[byte0 & 0xf];
            }
            //返回经过加密后的字符串
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES加密
     *
     * @param text 需要加密字符串
     * @param iv   向量
     * @param key  秘钥
     * @return 加密后字符串
     */
    public static String getAESEncrypt(String text, String iv, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keySpec = new SecretKeySpec(HexUtils.parseHexStr2Byte(key), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(HexUtils.parseHexStr2Byte(iv));

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
            return HexUtils.parseByte2HexStr(results);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param text 需要解密字符串
     * @param key  秘钥
     * @param iv   向量
     * @return 解密后字符串
     */
    public static String getAESDecrypt(String text, String iv, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keySpec = new SecretKeySpec(HexUtils.parseHexStr2Byte(key), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(HexUtils.parseHexStr2Byte(iv));

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal(HexUtils.parseHexStr2Byte(text));
            return new String(results, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES3加密
     *
     * @param plainText 待加密字符串
     * @param iv        向量
     * @param secretKey 秘钥
     * @return
     * @throws Exception
     */
    public static String getDES3encode(String plainText, String iv, String secretKey) {
        try {
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes("utf-8"));
            return Base64.encode(encryptData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES3解密
     *
     * @param encryptText 待解密字符串
     * @param iv          向量
     * @param secretKey   秘钥
     * @return
     * @throws Exception
     */
    public static String getDES3decode(String encryptText, String iv, String secretKey) {
        try {
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

            byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

            return new String(decryptData, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
