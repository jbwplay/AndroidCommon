package com.androidbase.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.androidbase.utils.javaio.FileUtils;
import com.androidbase.utils.javaio.StreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

/*---
Android 7.0开始,应用私有目录的访问权限被做限制。具体表现为，开发人员不能够再简单地通过file:// URI
访问其他应用的私有目录文件或者让其他应用访问自己的私有目录文件。
Android 7.0强制启用了被称作StrictMode的策略，带来的影响就是App对外无法暴露file://类型的URI了,
如果使用Intent携带这样的URI去打开外部App(比如：打开系统相机拍照)，那么会抛出FileUriExposedException异常。
Android7.0+需要使用FileProvider类提供的公有静态方法getUriForFile生成Content URI。

ContentProvider作用:进程间进行数据交互&共享,即跨进程通信。主要使用方式有:基于SQLite的ContentProvider(相册),
使用存储文件的ContentProvider(文件缓存)。
FileProvider是基于存储文件的。

FileProvider使用AndroidManifest.xml的<provider>节点指定共享目录xml:
<tag name="myname" path="mypath">
tag代表的目录+path指定的目录,保存在以name为key的hashmap中。
private final HashMap<String, File> mRoots = new HashMap<String, File>();
mRoots["myname"] = new File(tag,mypath)
应用调用getUriForFile生成共享Uri的时候,会遍历mRoots查找最佳的File目录，而name属性则是指定的
目录的一个别名。然后通过Uri.Builder生成content://uri。如果没有找到匹配的目录,则抛出异常IllegalArgumentException。

getUriForFile实现方法则主要是调用了parsePathStrategy方法从AndroidManifest.xml的<provider>节点解析根据tag生成共享目
录列表HashMap<String, File>。形成映射关系后,通过Uri得到对应的文件也变得简单了。
// Encode the tag and path separately
path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
return new Uri.Builder().scheme("content").authority(mAuthority).encodedPath(path).build();

暂时停用分区存储
在应用与分区存储完全兼容之前,可以使用以下方法之一暂时停用分区存储：
以Android 9（API 级别 28）或更低版本为目标平台。
如果以Android 10（API 级别 29）或更高版本为目标平台,请在应用的清单文件中将requestLegacyExternalStorage的值设置为true。

在Android 11上运行但以Android 10（API 级别 29）为目标平台的应用仍可请求requestLegacyExternalStorage属性。
应用可以利用此标记暂时停用与分区存储相关的变更，例如授予对不同目录和不同类型的媒体文件的访问权限。当您将应用更新为以
Android 11为目标平台后,系统会忽略requestLegacyExternalStorage标记。
保持与Android 10的兼容性如果应用在Android 10设备上运行时选择退出分区存储，建议您继续在应用的清单文件中将
requestLegacyExternalStorage设为true。这样，应用就可以在运行Android 10的设备上继续按预期运行。

Android 11上使用直接文件路径和原生库访问文件,为了帮助您的应用更顺畅地使用第三方媒体库,Android 11允许
使用除MediaStore API之外的API,通过直接文件路径访问共享存储空间中的媒体文件。
其中包括：
File API;
原生库,例如 fopen()
---*/

public final class UriUtils {

    private UriUtils() {
        throw new UnsupportedOperationException("can't been instantiated!");
    }

    public static Uri res2Uri(Context context, String resPath) {
        // 获取Android工程资源的Uri路径,一般是图片,res或者asset下的
        // 获取Res资源的url ContentResolver.SCHEME_ANDROID_RESOURCE*/
        // Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.mipmap.ic_launcher);
        // Uri uri = Uri.parse("res:///" + R.mipmap.ic_launcher);
        // 获取asset资源的url,ContentResolver.SCHEME_FILE
        // Uri assetUri = Uri.parse("file:///android_asset/" + "qq.png");
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + resPath);
    }

    /* Uri通用资源标志符（Universal Resource Identifier）Uri代表要操作的数据,Android中可用的每种资源 - 图像、视频片段等都可以用Uri来表示。
     Uri结构基本形式：[scheme:][//authority][path][?query][#fragment]
     path可以有多个,每个用/连接。
     示例：scheme://authority/path1/path2/path3?query#fragment
     query参数可以带有对应的值,也可以不带,如果带对应的值用=表示。
     示例：scheme://authority/path1/path2/path3?id=1#fragment,这里有一个参数id,它的值是1。
     query参数可以有多个,每个用&连接
     scheme://authority/path1/path2/path3?id=1&name=mingming&old#fragment
     这里有三个参数：参数1：id,其值是:1,参数2：name,其值是:mingming,参数3：old,没有对它赋值,所以它的值是null。
     在android中,除了scheme、authority是必须要有的,其它的几个path、query、fragment,它们每一个可以选择性的要或不要,但顺序不能变。
     示例：其中"path"可不要：scheme://authority?query#fragment
     其中"path"和"query"可都不要：scheme://authority#fragment
     其中"query"和"fragment"可都不要：scheme://authority/path
     其中"path","query","fragment"都不要：scheme://authority
     authority,又可以分为host:port的形式,即再次划分后形式：[scheme:][//host:port][path][?query][#fragment]
     常见http uri标识：http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic
     scheme:匹对上面的两个Uri标准形式,很容易看出在：前的部分是scheme,所以这个Uri字符串的sheme是：http
     scheme-specific-part:很容易看出scheme-specific-part是包含在scheme和fragment之间的部分,也就是包括第二部分的[//authority][path][?query]这几个小部分,
     所在这个Uri字符串的scheme-specific-part是：//www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4,注意要带上//,
     因为除了[scheme:]和[#fragment]部分全部都是scheme-specific-part,当然包括最前面的//；
     fragment:这个是更容易看出的,因为在最后用#分隔的部分就是fragment,所以这个Uri的fragment是：harvic。
     authority又可以划分为host:port形式,其中host:port用冒号分隔,冒号前的是host,冒号后的是port,所以：host:www.java2s.com port:8080
     getScheme():获取Uri中的scheme字符串部分,在这里即http
     getSchemeSpecificPart():获取Uri中的scheme-specific-part:部分,这里是：//www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4
     getFragment():获取Uri中的Fragment部分,即harvic
     getAuthority():获取Uri中Authority部分,即www.java2s.com:8080
     getPath():获取Uri中path部分,即/yourpath/fileName.htm
     getQuery():获取Uri中的query部分,即stove=10&path=32&id=4
     getHost():获取Authority中的Host字符串,即www.java2s.com
     getPost():获取Authority中的Port字符串,即8080*/
    public static Uri file2Uri(Context context, @NonNull final File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = AndroidUtils.getContext().getPackageName() + ".provider";
            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static String uri2File(Context context, Uri uri) {
        String path = "";
        try {
            path = getPath(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path == null) {
            path = "";
        }
        return path;
    }

    /**
     * 专为Android4.4以上设计的从Uri获取文件路径
     */
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = true;
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    try {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } catch (Exception exception) {
                        // 10.0+文件拷贝处理
                        return getPathByCopyFile(context, uri);
                    }
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                String[] contentUriPrefixesToTry = new String[]{"content://downloads/public_downloads", "content://downloads/my_downloads", "content://downloads/all_downloads"};
                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long
                            .parseLong(id));
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null) {
                            return path;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 在某些android8+的手机上，无法获取路径，所以用拷贝的方式，获取新文件名，然后把文件发出去
                return getPathByCopyFile(context, uri);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = MediaStore.Files.getContentUri("external");
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                String path = getDataColumn(context, contentUri, selection, selectionArgs);
                if (TextUtils.isEmpty(path)) {
                    path = getPathByCopyFile(context, uri);
                }
                return path;
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            String path = getDataColumn(context, uri, null, null);
            if (TextUtils.isEmpty(path)) {
                // 在某些华为android9+的手机上，无法获取路径，所以用拷贝的方式，获取新文件名，然后把文件发出去
                path = getPathByCopyFile(context, uri);
            }
            return path;
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 以file://开头的
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
     **/
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris,
     * and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static String getPathByCopyFile(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        File cacheDir = getDocumentCacheDir(context);
        File file = generateFileName(fileName, cacheDir);
        String destinationPath = null;
        if (file != null) {
            destinationPath = file.getAbsolutePath();
            copyUri2FilePath(context, uri, destinationPath);
        }
        return destinationPath;
    }

    private static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null && context != null) {
            filename = getName(uri.toString());
        } else {
            Cursor returnCursor = context.getContentResolver()
                    .query(uri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }
        return filename;
    }

    private static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }

    @Nullable
    private static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }
        File file = new File(directory, name);
        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }
            int index = 0;
            while (file.exists()) {
                index++;
                name = fileName + '(' + index + ')' + extension;
                file = new File(directory, name);
            }
        }
        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private static File getDocumentCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), "documents");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static void copyUri2FilePath(Context context, Uri uri, String filepath) {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            File file = new File(filepath);
            FileUtils.writeStream(file.getAbsolutePath(), is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 图片路径转uri
     *
     * @param context
     * @param path
     * @return
     */
    public static Uri getImageContentUri(Context context, String path) {
        try (Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{path}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (new File(path).exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, path);
                    return context.getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 视频路径转uri
     *
     * @param context
     * @param path
     * @return
     */
    public static Uri getVideoContentUri(Context context, String path) {
        try (Cursor cursor = context.getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ", new String[]{path}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (new File(path).exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, path);
                    return context.getContentResolver()
                            .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 判断 Uri 路径资源是否存在
     *
     * @param uriString uri 路径
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isUriExists(final String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return false;
        }
        return isUriExists(Uri.parse(uriString));
    }

    /**
     * 判断 Uri 路径资源是否存在
     *
     * @param uri {@link Uri}
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isUriExists(final Uri uri) {
        AssetFileDescriptor afd = null;
        try {
            afd = AndroidUtils.getContext()
                    .getContentResolver()
                    .openAssetFileDescriptor(uri, "r");
            return (afd != null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(afd);
        }
        return false;
    }

    /**
     * 获取 Uri InputStream
     *
     * @param uri {@link Uri} FileProvider Uri、Content Uri、File Uri
     * @return Uri InputStream
     */
    public InputStream openInputStream(final Uri uri) {
        if (uri == null) {
            return null;
        }
        try {
            ContentResolver resolver = AndroidUtils.getContext().getContentResolver();
            return resolver.openInputStream(uri);
        } catch (Exception e) {
            Log.e("UriUtils", "openInputStream " + uri.toString());
        }
        return null;
    }

    /**
     * 获取 Uri OutputStream
     *
     * @param uri {@link Uri} FileProvider Uri、Content Uri、File Uri
     * @return Uri OutputStream
     */
    public OutputStream openOutputStream(final Uri uri) {
        if (uri == null) {
            return null;
        }
        try {
            ContentResolver resolver = AndroidUtils.getContext().getContentResolver();
            return resolver.openOutputStream(uri);
        } catch (Exception e) {
            Log.e("UriUtils", "openOutputStream " + uri.toString());
        }
        return null;
    }

    /**
     * 获取 Uri OutputStream
     *
     * @param uri  {@link Uri} FileProvider Uri、Content Uri、File Uri
     * @param mode 读写模式
     * @return Uri OutputStream
     */
    public OutputStream openOutputStream(final Uri uri, final String mode) {
        if (uri == null) {
            return null;
        }
        try {
            ContentResolver resolver = AndroidUtils.getContext().getContentResolver();
            return resolver.openOutputStream(uri, mode);
        } catch (Exception e) {
            Log.e("UriUtils", "openOutputStream " + uri.toString() + " mode " + mode);
        }
        return null;
    }

    /**
     * Get File Mime Type
     *
     * @param uri File uri
     * @return Mime type string
     */
    public static String getMimeType(Uri uri) {
        String mimeType = null;
        try {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = AndroidUtils.getContext().getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(fileExtension.toLowerCase());
            }
        } catch (Exception e) {
            Log.e("Androidbase", "getMimeType Exception" + e.toString());
        }
        return mimeType;
    }

}
