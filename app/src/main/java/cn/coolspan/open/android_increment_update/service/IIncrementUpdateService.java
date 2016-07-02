package cn.coolspan.open.android_increment_update.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.zip.GZIPInputStream;

import cn.coolspan.open.IncrementUpdateLibs.IncrementUpdateUtil;
import cn.coolspan.open.android_increment_update.util.AppUtil;
import cn.coolspan.open.android_increment_update.bean.BSPatchResponse;
import cn.coolspan.open.android_increment_update.util.FileUtil;
import cn.coolspan.open.android_increment_update.IIncrementUpdateServer;

/**
 * Coolspan on 2016/6/24 16:33
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class IIncrementUpdateService extends Service {

    private PriorityBlockingQueue<Integer> mHotUpdateRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO：初始化启动服务后所需的数据
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IncreamentUpdateImpl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.e("IncreamentUpdateImpl", "onStartCommand:" + intent.getDataString());
        } else {
            //do nothing
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class IncreamentUpdateImpl extends IIncrementUpdateServer.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //do nothing
        }

        @Override
        public void start() throws RemoteException {
            // TODO: 2016/6/24 请求增量更新接口
            Log.e("IncreamentUpdateImpl", "start");

            doRequest();
        }

        @Override
        public void reload() throws RemoteException {
            // TODO: 2016/6/24 合并差异文件
            Log.e("IncreamentUpdateImpl", "startMergePatch");
            if (mHotUpdateRequestQueue != null) {
                mHotUpdateRequestQueue.offer(1);
            } else {
                //do nothing
            }
        }

        @Override
        public void stop() throws RemoteException {
            if (mHotUpdateRequestQueue != null) {
                mHotUpdateRequestQueue.offer(-1);
            } else {
                //do nothing
            }
        }
    }

    private synchronized void doRequest() {

        if (mHotUpdateRequestQueue == null) {
            mHotUpdateRequestQueue = new PriorityBlockingQueue<>();
            mHotUpdateRequestQueue.offer(1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            int entry = mHotUpdateRequestQueue.take();
                            if (entry == 1) {
                                doRequestServer();
                            } else if (entry == -1) {
                                break;
                            } else {
                                //do nothing
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            mHotUpdateRequestQueue.offer(1);
        }
    }

    private void doRequestServer() {
        try {
            URL url = new URL("http://192.168.1.120:8207/v4/bspatch.wn?version=" + AppUtil.getVersionCode(IIncrementUpdateService.this));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setDoInput(true);
            int responseCode = httpURLConnection.getResponseCode();
            log(responseCode + "");
            String data = FileUtil.transformString(httpURLConnection.getInputStream());
            log(data);
            if (responseCode == 200) {
                if (data != null) {
                    BSPatchResponse bsPatchResponse = JSON.parseObject(data, BSPatchResponse.class);
                    if (bsPatchResponse != null) {
                        if (bsPatchResponse.getStatus() == 1) {
                            verifyCurrentApkMD5(bsPatchResponse);
                        } else {
                            // TODO: 2016/7/2 根据不同状态，做相应的操作
                        }
                    } else {
                        //do nothing
                    }
                } else {
                    //do nothing
                }
            } else {
                //do nothing
                log(responseCode + "    ----body：" + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verifyCurrentApkMD5(BSPatchResponse bsPatchResponse) {
        String oldApkMD5 = bsPatchResponse.getOldApkMD5();
        if (oldApkMD5 != null && !"".equals(oldApkMD5)) {
            String oldApkPath = AppUtil.getApkDerectory(this);
            if (oldApkPath != null && !"".equals(oldApkPath)) {
                byte[] data = FileUtil.transformByteArray(oldApkPath);
                String dataMD5 = FileUtil.decrpterByteArrayMD5(data, -1);
                if (dataMD5 != null && dataMD5.equals(oldApkMD5)) {
                    File outFile = new File(Environment.getExternalStorageDirectory(), getLocalPatchFileName(bsPatchResponse.getUrl()));
                    downloadPatchFile(bsPatchResponse, outFile.getAbsolutePath());
                } else {
                    // TODO: 2016/7/2 下载完整安装包
                    log("增量更新。。。下载完整安装包1");
                }
            } else {
                //do nothing
                log("找不到旧安装包目录");
                // TODO: 2016/7/2 下载完整安装包
            }
        } else {
            //do nothing
            log("接口数据未返回旧Apk文件的MD5");
            // TODO: 2016/7/2 下载完整安装包
        }
    }

    private void downloadPatchFile(BSPatchResponse bsPatchResponse, String outFilePath) {
        try {
            String url = bsPatchResponse.getUrl();
            URL uRL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) uRL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip;q=1.0, identity; q=0.5, *;q=0");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setDoInput(true);

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                String encode = httpURLConnection.getContentEncoding();
                if (encode != null && "gzip".equals(encode)) {
                    GZIPInputStream gzipInputStream = new GZIPInputStream(httpURLConnection.getInputStream());
                    FileUtil.transformFile(gzipInputStream, outFilePath);
                    httpURLConnection.getInputStream().close();
                } else {
                    FileUtil.transformFile(httpURLConnection.getInputStream(), outFilePath);
                }
                verifyPatchMD5(outFilePath, bsPatchResponse);
            } else {
//                String data = FileUtil.transformString(httpURLConnection.getInputStream());
                log("downloadPatchFile responseCode:" + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verifyPatchMD5(String outFilePath, BSPatchResponse bsPatchResponse) {
        byte[] data = FileUtil.transformByteArray(outFilePath);
        String dataMD5 = FileUtil.decrpterByteArrayMD5(data, -1);
        String md5 = bsPatchResponse.getMd5();
        if (dataMD5 != null && md5 != null && dataMD5.equals(md5)) {
            log(dataMD5);
            log(md5);
            mergePatchToApk(outFilePath, bsPatchResponse);
        } else {
            FileUtil.deleteFile(outFilePath);
            log("校验文件失败，文件不正确");
            log(dataMD5);
            log(md5);
        }
    }

    private void mergePatchToApk(String outFilePath, BSPatchResponse bsPatchResponse) {
        String oldApkPath = AppUtil.getApkDerectory(this);
        if (oldApkPath != null && !"".equals(oldApkPath) && oldApkPath.endsWith(".apk")) {
            String fileName = "WNyunshang.apk";
            File newApkFile = new File(Environment.getExternalStorageDirectory(), fileName);
            FileUtil.deleteFile(newApkFile);
            String newApkPath = newApkFile.getAbsolutePath();
            int result = IncrementUpdateUtil.mergePatch(oldApkPath, outFilePath, newApkPath);
            log(result + "");
            if (result == 0) {
                byte[] data = FileUtil.transformByteArray(outFilePath);
                String dataMD5 = FileUtil.decrpterByteArrayMD5(data, -1);
                if (dataMD5 != null && bsPatchResponse.getNewApkMD5() != null && !"".equals(dataMD5) && !"".equals(bsPatchResponse.getNewApkMD5())) {
                    log("增量更新。。。成功");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(newApkFile),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // TODO: 2016/7/2 合成的安装包失败，增量更新有问题，不兼容此手机
                    log("增量更新。。。失败1");
                }
            } else {
                // TODO: 2016/7/2 合成新Apk文件失败
                log("增量更新。。。失败2");
            }
        } else {
            // TODO: 2016/7/2 此手机不能使用增量更新
            log("增量更新。。。失败3");
        }
    }

    public synchronized String getLocalPatchFileName(String url) {
        if (url == null) {
            return null;
        } else {
            url = url.substring(url.lastIndexOf("/") + 1);
            return url;
        }
    }

    private void log(String msg) {
        if (msg != null) {
            Log.e("Coolspan Service", msg);
        } else {
            Log.e("Coolspan Service", "NULL");
        }
    }
}
