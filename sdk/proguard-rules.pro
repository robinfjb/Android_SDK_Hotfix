# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#基本组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
#Google 原生服务
-keep public class com.google.*
#dalvik
-keep class dalvik.** {*;}
-dontwarn dalvik.**
#java
-keep class java.** {*;}
-dontwarn java.**
#android
-keep class android.** {*;}
-dontwarn android.**
-keep public class com.android.** {*;}
#json
-dontwarn org.json.*
-keep class org.json.* {
    *;
}
#Support
-dontwarn android.support.**
-keep public class * extends android.support.v4.*
-keep public class * extends android.support.v7.*
-keep public class * extends android.support.annotation.*
#AndroidX
-keep public class androidx.*
#native
-keepclasseswithmembernames class * {
    native <methods>;
}
#自定义控件
-keep public class * extends android.view.View { ####
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#指定格式的构造方法
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#enum 类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#在Activity中的方法参数是view的方法(避免布局文件里面onClick被影响)
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
#R
-keep class **.R$* { *; }
#Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#Serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#BaseAdapter
-keep public class * extends android.widget.BaseAdapter { *; }

# --------------------------------------------webView区--------------------------------------------#
# WebView处理，项目中没有使用到webView忽略即可
# 保持Android与JavaScript进行交互的类不被混淆
-keep class **.AndroidJavaScript { *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}
#反射类
-keep public class robin.sdk.sdk_impl.ServiceImpl {*;}
-keep class robin.sdk.sdk_impl.ServiceImpl$* {*;}
#sdk
-keep public class robin.sdk.hotfix.RobinClient {*;}
-keep class robin.sdk.hotfix.RobinClient$* {*;}
-keep public class robin.sdk.hotfix.RobinService {
    public <methods>;
}
-keep public interface * {*;}
-keepclassmembers class robin.sdk.service_dynamic.net.HttpUrlTask {
    protected <methods>;
}
-keepclassmembers class robin.sdk.service_dynamic.net.DownloadTask {
    protected <methods>;
}
-dontwarn robin.sdk.hotfix.**
-dontwarn robin.sdk.service_dynamic.**