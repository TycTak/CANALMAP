# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Mike\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn com.jcraft.jsch.jcraft.Compression
-dontwarn com.jcraft.jsch.jgss.GSSContextKrb5
-dontwarn org.apache.commons.compress.compressors.**
-dontwarn org.apache.commons.compress.archivers.**

#-dontnote android.net.http.*
#-dontnote org.apache.commons.codec.**
#-dontnote org.apache.http.**

#-dontwarn okhttp3.**
-dontwarn okio.**
#-dontwarn com.google.j2objc.annotations.J2ObjCIncompatible

#-keep class org.apache.http.**
-keep public class com.android.vending.billing.IInAppBillingService
-keep class com.android.vending.billing.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.widget.SearchView { *; }

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
}

-assumenosideeffects class java.util.logging.Logger {
    public static *** info(...);
    public static *** warning(...);
}

-ignorewarnings
-keep class * {
    public private *;
}

-keepattributes Signature,SourceFile,LineNumberTable