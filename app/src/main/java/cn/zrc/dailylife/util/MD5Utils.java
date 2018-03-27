package cn.zrc.dailylife.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yangzhizhong
 */

public class MD5Utils {

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 生成字符串的md5校验值
     *
     * @param s
     * @return
     */
    public static String MD5(String s) {
        return MD5(s.getBytes());
    }

    /**
     * 生成输入流的md5校验值，注意，<b>调用该方法后应该自己关闭输入流</b>
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String MD5(InputStream inputStream) {
        if (inputStream != null) {
            try {
                MessageDigest l_messagedigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int numRead = 0;
                while ((numRead = inputStream.read(buffer)) > 0) {
                    l_messagedigest.update(buffer, 0, numRead);
                }
                return bufferToHex(l_messagedigest.digest());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String MD5(File file) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return MD5(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String MD5(byte[] bytes) {
        try {
            MessageDigest l_messagedigest = MessageDigest.getInstance("MD5");
            l_messagedigest.update(bytes);
            return bufferToHex(l_messagedigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
