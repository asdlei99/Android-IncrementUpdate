[Android增量更新](http://blog.csdn.net/qxs965266509/article/details/50987403)
===
![](https://github.com/coolspan/Android-Increment-Update/blob/master/images/icon.jpg)

简述
==

增量更新，根据字面理解，就是下载增加的那部分来达到更新的目的，实际就是这个意思。

原理
==

用一个旧的Apk安装与一个新的Apk安装包使用 bsdiff工具 ，执行命令生成一个差异文件，此差异文件就是我们修改需要更新下载的那部分。

引入代码及so文件
==

首先，根据你的[系统的架构选择不同的so文件](https://github.com/coolspan/Android-Increment-Update/tree/master/so)放到你的工程中

接着，[需要把加载so文件的Java类引入到你的工程中](https://github.com/coolspan/Android-Increment-Update/tree/master/app/src/main/java/cn/coolspan/open/IncrementUpdateLibs)，引入时，需注意，不能修改这个类的包名。

到此，增量更新引入完成。

使用
==

[下载bsdiff工具](http://download.csdn.net/detail/qxs965266509/9473251)，然后执行命令：bsdiff，会显示出命令提示

然后，执行正确的命令，结果如下：

命令：`bsdiff app_1.1.apk app_1.2.apk patch.patch `
参数： 
	app_1.1：已发布的旧版本 
	app_1.2：未发布的新版本 
	patch.patch：生成的差异文件

实际开发流程
==

1. 把新的Apk安装包上传到服务器，让服务器生成对应不同版本的差异文件。
2. 服务器需要提供一个接口，把你当前app的版本信息通过接口传递到服务器，服务器解析判断，完后响应数据告诉客户端是否需要下载差异文件，因为不同的版本下载的差异文件不同，此处需大家多多注意。
3. 如果接口返回的数据告诉客户端有差异文件下载，客户端使用子线程下载，然后执行增量更新的合并接口，然后生成新的Apk安装包，执行安装命令即完成增量更新的整个过程。 


具体使用方式，请参考博客：[Android-App增量更新的使用姿势](http://blog.csdn.net/qxs965266509/article/details/50987403)


#功能
##已完成的功能

 1. 根据差异文件合成新的安装包
 2. 文件的校验(旧安装包，差异文件，新安装包)，使用MD5校验文件是否正确
 3. 使用服务器接口下载差异文件
 4. Win/Linux的生成差异文件的工具

##待完善的功能

 1. 使用跨进程的方式，请求服务器接口并安装
 2. 在跨进程的基础上，给开发者提供安装后重启的功能
 3. 对不支持增量更新的系统进行的完善处理(当前测试未发现不支持的系统)
 4. 优化、迭代等


博主的热修复文章：
---
1. [Alibaba-AndFix Bug热修复框架的使用](http://blog.csdn.net/qxs965266509/article/details/49802429 "qxs965266509")<br/>
2. [Alibaba-AndFix Bug热修复框架原理及源码解析](http://blog.csdn.net/qxs965266509/article/details/49816007 "qxs965266509")<br/>
3. [Alibaba-Dexposed框架在线热补丁修复的使用](http://blog.csdn.net/qxs965266509/article/details/49821413 "qxs965266509")<br/>
4. [Alibaba-Dexposed Bug框架原理及源码解析](http://blog.csdn.net/qxs965266509/article/details/50117137 "qxs965266509")<br/>
