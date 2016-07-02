package cn.coolspan.open.android_increment_update.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Coolspan on 2016/7/2 10:31
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class FileUtil {

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        } else {
            //do nothing
        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            deleteFile(file);
        } else {
            //do nothing
        }
    }

    /**
     * 计算data的md5值
     *
     * @param data
     * @param length
     * @return
     */
    public static String decrpterByteArrayMD5(byte[] data, int length) {
        if (data == null) {
            return null;
        } else {
            if (length == -1) {
                length = data.length;
            } else {
                //do nothing
            }
            try {
                MessageDigest digests = MessageDigest.getInstance("MD5");
                digests.update(data, 0, length);
                BigInteger bi = new BigInteger(1, digests.digest());
                String result = bi.toString(16);
                int resultLength = result.length();
                if (resultLength < 32) {//补位运算
                    int diff = 32 - resultLength;
                    for (int i = 0; i < diff; i++) {
                        result = "0" + result;
                    }
                }
                return result;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 把文件转换为byte数组
     *
     * @param filePath
     * @return
     */
    public static byte[] transformByteArray(String filePath) {
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    return transformByteArray(fileInputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                return null;
            }
        }
    }

    /**
     * 把输入流转换为byte数组
     *
     * @param inputStream
     * @return
     */
    public static byte[] transformByteArray(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        } else {
            //do next something
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * 把输入流转换为字符串
     *
     * @param inputStream
     * @return
     */
    public static String transformString(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        } else {
            byte[] data = transformByteArray(inputStream);
            if (data == null) {
                return null;
            } else {
                return new String(data);
            }
        }
    }

    /**
     * 把输入流转换为文件
     *
     * @param inputStream
     * @param outFilePath
     * @throws IOException
     */
    public static void transformFile(InputStream inputStream, String outFilePath) throws IOException {
        if (inputStream == null || outFilePath == null) {
            throw new IOException("输入流和输入路径不能为空");
        } else {
            int length;
            byte[] buffer = new byte[1024];
            File file = new File(outFilePath);
            deleteFile(file);
            FileOutputStream fileOutputStream = new FileOutputStream(outFilePath);
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        }
    }
}
