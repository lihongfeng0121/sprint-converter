package com.sprint.common.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
public class Miscs {

    /**
     * 获取数据元素
     *
     * @param ts           数组
     * @param i            位置
     * @param defaultValue 默认值
     * @param <T>          数组元素类型
     * @return 数组元素
     */
    public static <T> T at(T[] ts, int i, T defaultValue) {
        if (ts == null) {
            return defaultValue;
        }
        if (i < 0) {
            i += ts.length;
        }
        return ts.length > i ? ts[i] : defaultValue;
    }


    /**
     * 获取数据元素
     *
     * @param ts  数组
     * @param i   位置
     * @param <T> 数组元素类型
     * @return 数组元素
     */
    public static <T> T at(T[] ts, int i) {
        return at(ts, i, null);
    }

    /**
     * 是否包含
     *
     * @param ts 数组
     * @param t  元素
     * @return 是否包含
     */
    public static boolean contained(Object[] ts, String t) {
        if (ts != null && t != null) {
            for (Object obj : ts) {
                if (obj != null && obj.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 集合大小
     *
     * @param collection 数组
     * @return 数组元素
     */
    public static int size(Collection<?> collection) {
        if (collection == null) {
            return 0;
        }
        return collection.size();
    }

    public static final int BUFFER_SIZE = 4096;

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
}
