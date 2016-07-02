package cn.coolspan.open.android_increment_update.bean;

import java.io.Serializable;

/**
 * Coolspan on 2016/7/2 11:04
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class BSPatchResponse implements Serializable {

    /**
     * 状态
     */
    public int status;

    /**
     * 版本号
     */
    public int versionCode;

    /**
     * 当前差异文件的md5
     */
    public String md5;

    /**
     * 差异文件的下载链接
     */
    public String url;

    /**
     * 合成新的Apk文件的MD5
     */
    public String newApkMD5;

    /**
     * 旧Apk文件的MD5
     */
    public String oldApkMD5;

    public String getNewApkMD5() {
        return newApkMD5;
    }

    public void setNewApkMD5(String newApkMD5) {
        this.newApkMD5 = newApkMD5;
    }

    public String getOldApkMD5() {
        return oldApkMD5;
    }

    public void setOldApkMD5(String oldApkMD5) {
        this.oldApkMD5 = oldApkMD5;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
