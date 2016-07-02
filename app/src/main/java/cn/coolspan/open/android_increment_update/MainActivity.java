package cn.coolspan.open.android_increment_update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.coolspan.open.IncrementUpdateLibs.IncrementUpdateUtil;


/**
 * 当前考虑使用跨进程实现Apk的增量更新
 * <p/>
 * Coolspan on 2016/3/26 11:57
 *
 * @author 乔晓松 coolspan@sina.cn
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private Handler handler = new Handler();

    private Button button;
    private TextView textView;

    IIncrementUpdateServer mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.e("ServiceConnection", "disconnect service");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.e("ServiceConnection", "connect service");
            mService = IIncrementUpdateServer.Stub.asInterface(service);
            try {
                if (mService != null)
                    mService.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.button = (Button) this.findViewById(R.id.button);
        this.button.setOnClickListener(this);

        this.textView = (TextView) this.findViewById(R.id.textView);

        try {
            this.textView.setText("猜猜我是谁，我的版本是" + (this.getPackageManager().getPackageInfo("cn.coolspan.open.android_increment_update", 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        Toast.makeText(this, "新的安装包", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        Intent intent = new Intent("cn.coolspan.IncrementUpdateService");
        //测试出现在高版本的系统会出现异常：Service Intent must be explicit
        //解决方式：就是指定Intent package
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            intent.setPackage("cn.coolspan.open.android_increment_update");
        }
        intent.putExtras(args);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File rootFile = Environment.getExternalStorageDirectory();
                        File patchFile = new File(rootFile, "patch.patch");
                        if (patchFile.exists()) {
                            File newApkFile = new File(rootFile, "newApkFile.apk");
                            int state = IncrementUpdateUtil.mergePatch(IncrementUpdateUtil.getApkDerectory(MainActivity.this),
                                    patchFile.getAbsolutePath(), newApkFile.getAbsolutePath());
                            Log.e("bspatch", "" + state);

                            //删除差异文件
                            patchFile.delete();

                            //安装新的apk文件
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(newApkFile),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                        } else {//无差异文件
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "无补丁文件", Toast.LENGTH_SHORT).show();
                                }
                            });
//                            Intent intent = new Intent(MainActivity.this, IIncrementUpdateService.class);
//                            intent.putExtra("ckey", "cvalue");
//                            startService(intent);
                            mService.start();
//                            mService.startMergePatch("----------------------url");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
