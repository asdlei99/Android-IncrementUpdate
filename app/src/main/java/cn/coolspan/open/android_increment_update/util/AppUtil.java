package cn.coolspan.open.android_increment_update.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Coolspan on 2016/7/2 11:44
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class AppUtil {

    /**
     * 获取安装包目录
     *
     * @param context
     * @return
     */
    public static String getApkDerectory(Context context) {
        return context.getApplicationInfo().sourceDir;
    }

    /**
     * 获取应用的版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
