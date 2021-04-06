# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android Eclipse\sdk/tools/proguard/proguard-android.txt
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

#标识符压缩为更短名称
#资源压缩移除未使用资源
#代码压缩移除未使用代码
#keep——保留所有匹配类规范的类和方法
#keepclassmembers——类的指定成员变量将被保护
#keepclasseswithmembers——拥有指定成员的类将被保护，根据类成员确定一些将要被保护的类

# Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable,
# LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations,
# RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, and AnnotationDefault
# 进行发送CrashReport,定位问题所在行数
# -keepattributes **
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
# -ignorewarnings

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# androidx
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
# androidx end

#databinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
#databinding end

# ProGuard configurations for squareup
-keep class retrofit2.** { *; }
-keep class com.squareup.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn com.squareup.**
-dontwarn okhttp3.**
-dontwarn okio.**
# End squareup

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# End EventBus

# Tencent、Baidu、Sina、Alipay
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**
-keep class com.baidu.**{*;}
-dontwarn com.baidu.**
-keep class com.sina.**{*;}
-dontwarn com.sina.**
-keep class com.alipay.**{*;}
-dontwarn com.alipay.**
# End Tencent、Baidu、Alipay

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
# for DexGuard only
# VideoDecoder uses API >= 27 APIs which may cause proguard warnings even though the newer
# APIs won’t be called on devices with older versions of Android.
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
# End Glide

# JPush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.service.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
# End JPush

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# End Bugly

##--------------- arouter  ---------->
-keep public class com.alibaba.android.arouter.routes.*{*;}
-keep public class com.alibaba.android.arouter.facade.*{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
 -keep class * implements com.alibaba.android.arouter.facade.template.IProvider
##---------------  arouter  ----------

# Zxing
-dontwarn com.google.zxing.**
-keep public class com.google.zxing.**{*;}
# End Zxing

# AMap
-keep public class com.amap.api.**{*;}
-dontwarn com.amap.api.**
-keep public class com.autonavi.amap.mapcore2d.**{*;}
-dontwarn com.autonavi.amap.mapcore2d.**
-keep public class com.autonavi.aps.amapapi.model.**{*;}
-dontwarn com.autonavi.aps.amapapi.model.**
-keep public class com.loc.**{*;}
-dontwarn com.loc.**
-keep public class com.amap.api.services.**{*;}
-dontwarn com.amap.api.services.**
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
# End AMap

# Banner
-keep class com.youth.banner.** {*;}
# End Banner

# Renderscript Support
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class android.support.v8.renderscript.** { *; }
# End Renderscript Support

# 第三方R资源文件
-keep class **.R$* {*;}

# Geitui
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }
-keep class com.xiaomi.** { *; }
-dontwarn com.xiaomi.push.**
-keep class org.apache.thrift.** { *; }
-dontwarn com.huawei.hms.**
-keep class com.huawei.hms.** { *; }
-keep class com.huawei.android.** { *; }
-dontwarn com.huawei.android.**
-keep class com.hianalytics.android.** { *; }
-dontwarn com.hianalytics.android.**
-keep class com.huawei.updatesdk.** { *; }
-dontwarn com.huawei.updatesdk.**
-keep class com.meizu.** { *; }
-dontwarn com.meizu.**
#End Geitui

#----------阿里热修复,进行修复时需处理--------------
#基线包使用，生成mapping.txt
#-printmapping mapping.txt  ---todo
#生成的mapping.txt在app/buidl/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix

-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
#hotfix end


#自己项目特殊处理代码
#保留一个完整的包
#-keep class com.veidy.mobile.common.** {
#    *;
# }

#保留一个完整的类
#-keep class com.android.module.http.PreferencesCookieStore { *;}
