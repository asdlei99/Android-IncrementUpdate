package cn.coolspan.open.android_increment_update.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.coolspan.open.android_increment_update.IIncrementUpdateServer;

/**
 * Coolspan on 2016/6/24 16:33
 *
 * @author 乔晓松 coolspan@sina.cn
 */
public class IIncrementUpdateService extends Service {

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
            Log.e("IncreamentUpdateImpl", "onStartCommand:"+intent.getDataString());
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
        }

        @Override
        public void startMergePatch(String patchFilePath) throws RemoteException {
            // TODO: 2016/6/24 合并差异文件
            Log.e("IncreamentUpdateImpl", "startMergePatch");
        }
    }
}
