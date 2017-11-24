package me.lv.app;

import me.lv.secret.SecretUtils;

/**
 * Created by lzw on 2017/11/19.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(SecretUtils.getMD5("123456"));

        String key = "6E3753494864585030657A4F74453346";
        String iv = "3472626E4A575635534F307A58645052";

        String aesStr = SecretUtils.getAESEncrypt("1234567890", iv, key);
        System.out.println(aesStr);
        System.out.println(SecretUtils.getAESDecrypt(aesStr, iv, key));

        String desIv = "12345678";
        String desStr = SecretUtils.getDES3encode("123456", desIv, key);
        System.out.println(desStr);
        System.out.println(SecretUtils.getDES3decode(desStr, desIv, key));
    }
}
