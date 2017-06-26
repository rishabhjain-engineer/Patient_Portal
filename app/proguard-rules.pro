# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ashish\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
-keep class com.hs.userportal.UploadService {
public *;
}
-keep public class org.apache.commons.** { *; }
# Class names are needed in reflection
-keepnames class com.amazonaws.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**
-dontwarn org.joda.**
-dontwarn org.bouncycastle.**
-dontwarn org.apache.**
-dontwarn org.w3c.**
-dontwarn javax.xml.**
-dontwarn java.awt.**
-dontwarn javax.imageio.**
-dontwarn com.itextpdf.**
-dontwarn android.util.**
 #keep json classes
 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
     !static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
     !static !transient <fields>;
 }
 #GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class com.google.gson.** { *; }
-keep class com.applozic.audiovideo.authentication.** { *; }
-keep class org.webrtc.** { *; }
-keep class com.twilio.** { *; }

-dontwarn com.fasterxml.jackson.core.*






