package cn.coolspan.open.IncrementUpdateLibs;

import android.content.Context;

/**
 * Coolspan on 2016/3/26 11:57
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class IncrementUpdateUtil {

    static {
        //静态初始化块加载so文件
        System.loadLibrary("bspatch");
    }

    /**
     * 获取安装包的目录位置
     *
     * @param context 上下文对象
     * @return
     */
    public static String getApkDerectory(Context context) {
        return context.getApplicationInfo().sourceDir;
    }

    /**
     * 合并产生安装包文件
     *
     * @param oldApkPath
     * @param patchPath
     * @param newApkPath
     * @return
     */
    public static int mergePatch(String oldApkPath, String patchPath, String newApkPath) {
        return bspatch(oldApkPath, newApkPath, patchPath);
    }

    public native static int bspatch(String oldApkPath,
                                     String newApkPath, String patchPath);

}
