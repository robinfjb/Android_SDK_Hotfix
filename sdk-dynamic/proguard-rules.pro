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
#dalvik
-keep class dalvik.** {*;}
-dontwarn dalvik.**
#java
-keep class java.** {*;}
-dontwarn java.**
#android
-keep class android.** {*;}
-dontwarn android.**
-dontwarn org.json.*
-keep class org.json.* {
    *;
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
-dontwarn robin.sdk.hotfix.**
-dontwarn robin.sdk.service_dynamic.**
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