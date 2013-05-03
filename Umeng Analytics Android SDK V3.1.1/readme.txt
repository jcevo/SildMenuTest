V3.1.1更新：

1.修复android 4.0 中的自动更新下载bug
2.添加 session 长度（default 30s）控制的接口
3.增加了按天自动更新的接口

V3.1更新：

1.增加判断下载进度的回调

2.增加在线参数回调接口

3.是否使用地理位置的开关

4.增加log打印的开关（修改了部分log打印的内容）

5.简化了客户端反馈的存储逻辑。

6.反馈模块减少了不必要的请求。

V3.0更新:

1. 增加双向用户反馈支持
2. 支持动态调整发送策略

3. 支持在线参数配置

注：

在3.0以上（包括3.0）版本中我们对资源文件做了稍微的改动，需要注意

1.在使用自动更新功能时，引入values和values-cn文件夹下的umeng_analyse_strings.xml
  文件，同时替换umeng_download_notification.xml为umeng_analyse_download_notification.xml

2.使用反馈功能时，需要把我们提供的（resource文件夹下）所有资源复制到对应的文件夹下。

3.如果您要混淆apk，要添加如下的混淆参数

 -keep public class [package name].R$*{
     public static final int *;
 }

 将[package name]替换为您的包名，避免R文件被中的资源被混淆。

 -keep public class com.feedback.ui.ThreadView { }
 在使用双向反馈时，避免我们自定义的UI类被混淆。