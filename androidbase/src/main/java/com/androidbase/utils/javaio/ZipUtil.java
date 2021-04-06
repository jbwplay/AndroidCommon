package com.androidbase.utils.javaio;

import com.androidbase.utils.StringConstant;
import com.androidbase.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Performs zip/gzip/zlib operations on files and directories. These are just tools over
 * existing <code>java.util.zip</code> classes, meaning that existing behavior and bugs
 * are persisted. Most common issue is not being able to use UTF8 in file names, because
 * implementation uses old ZIP format that supports only IBM Code Page 437. This bug was
 * resolved in JDK7: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4244499
 */
public class ZipUtil {

    public static final String ZIP_EXT = ".zip";
    public static final String GZIP_EXT = ".gz";
    public static final String ZLIB_EXT = ".zlib";

    /**
     * Compresses a file into zlib archive.
     */
    public static File zlib(String file) throws IOException {
        return zlib(new File(file));
    }

    /**
     * Compresses a file into zlib archive.
     */
    public static File zlib(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("Can't zlib folder");
        }
        FileInputStream fis = new FileInputStream(file);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

        String zlibFileName = file.getAbsolutePath() + ZLIB_EXT;

        DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(zlibFileName), deflater);

        try {
            StreamUtil.copy(fis, dos);
        } finally {
            StreamUtil.close(dos);
            StreamUtil.close(fis);
        }

        return new File(zlibFileName);
    }

    /**
     * Compresses a file into gzip archive.
     */
    public static File gzip(String fileName) throws IOException {
        return gzip(new File(fileName));
    }

    /**
     * Compresses a file into gzip archive.
     */
    public static File gzip(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("Can't gzip folder");
        }
        FileInputStream fis = new FileInputStream(file);

        String gzipName = file.getAbsolutePath() + GZIP_EXT;

        GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(gzipName));
        try {
            StreamUtil.copy(fis, gzos);
        } finally {
            StreamUtil.close(gzos);
            StreamUtil.close(fis);
        }

        return new File(gzipName);
    }

    /**
     * Decompress gzip archive.
     */
    public static File ungzip(String file) throws IOException {
        return ungzip(new File(file));
    }

    /**
     * Decompress gzip archive.
     */
    public static File ungzip(File file) throws IOException {
        String outFileName = FileNameUtil.removeExtension(file.getAbsolutePath());
        File out = new File(outFileName);
        out.createNewFile();

        FileOutputStream fos = new FileOutputStream(out);
        GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
        try {
            StreamUtil.copy(gzis, fos);
        } finally {
            StreamUtil.close(fos);
            StreamUtil.close(gzis);
        }

        return out;
    }

    /**
     * Zips a file or a folder.
     *
     * @see #zip(File)
     */
    public static File zip(String file) throws IOException {
        return zip(new File(file));
    }

    /**
     * Zips a file or a folder. If adding a folder, all its content will be added.
     */
    public static File zip(File file) throws IOException {
        String zipFile = file.getAbsolutePath() + ZIP_EXT;

        return ZipBuilder.createZipFile(zipFile).add(file).recursive().save().toZipFile();
    }

    /**
     * Lists zip content.
     */
    public static List<String> listZip(File zipFile) throws IOException {
        List<String> entries = new ArrayList<>();

        ZipFile zip = new ZipFile(zipFile);
        Enumeration zipEntries = zip.entries();

        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            String entryName = entry.getName();

            entries.add(entryName);
        }

        return Collections.unmodifiableList(entries);
    }

    /**
     * Extracts zip file content to the target directory.
     *
     * @see #unzip(File, File, String...)
     */
    public static void unzip(String zipFile, String destDir, String... patterns) throws
            IOException {
        unzip(new File(zipFile), new File(destDir), patterns);
    }

    /**
     * Extracts zip file to the target directory. If patterns are provided only matched
     * paths are extracted.
     *
     * @param zipFile  zip file
     * @param destDir  destination directory
     * @param patterns optional wildcard patterns of files to extract, may be
     *                 <code>null</code>
     */
    public static void unzip(File zipFile, File destDir, String... patterns) throws
            IOException {
        ZipFile zip = new ZipFile(zipFile);
        Enumeration zipEntries = zip.entries();

        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            String entryName = entry.getName();

            if (patterns != null && patterns.length > 0) {
                if (Wildcard.matchPathOne(entryName, patterns) == -1) {
                    continue;
                }
            }

            File file = (destDir != null) ? new File(destDir, entryName) : new File(entryName);
            if (entry.isDirectory()) {
                if (!file.mkdirs()) {
                    if (!file.isDirectory()) {
                        throw new IOException("Failed to create directory: " + file);
                    }
                }
            } else {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs()) {
                        if (!file.isDirectory()) {
                            throw new IOException("Failed to create directory: " + parent);
                        }
                    }
                }

                InputStream in = zip.getInputStream(entry);
                OutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    StreamUtil.copy(in, out);
                } finally {
                    StreamUtil.close(out);
                    StreamUtil.close(in);
                }
            }
        }

        close(zip);
    }

    /**
     * Adds single entry to ZIP output stream.
     *
     * @param zos       zip output stream
     * @param file      file or folder to add
     * @param path      relative path of file entry; if <code>null</code> files name will
     *                  be used instead
     * @param comment   optional comment
     * @param recursive when set to <code>true</code> content of added folders will be
     *                  added, too
     */
    public static void addToZip(ZipOutputStream zos, File file, String path, String comment, boolean recursive) throws
            IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }

        if (path == null) {
            path = file.getName();
        }

        while (path.length() != 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }

        boolean isDir = file.isDirectory();

        if (isDir) {
            // add folder record
            if (!StringUtils.endsWithChar(path, '/')) {
                path += '/';
            }
        }

        ZipEntry zipEntry = new ZipEntry(path);
        zipEntry.setTime(file.lastModified());

        if (comment != null) {
            zipEntry.setComment(comment);
        }

        if (isDir) {
            zipEntry.setSize(0);
            zipEntry.setCrc(0);
        }

        zos.putNextEntry(zipEntry);

        if (!isDir) {
            InputStream is = new FileInputStream(file);
            try {
                StreamUtil.copy(is, zos);
            } finally {
                StreamUtil.close(is);
            }
        }

        zos.closeEntry();

        // continue adding

        if (recursive && file.isDirectory()) {
            boolean noRelativePath = StringUtils.isEmpty(path);

            final File[] children = file.listFiles();

            if (children != null && children.length != 0) {
                for (File child : children) {
                    String childRelativePath = (noRelativePath ? StringConstant.EMPTY : path) + child
                            .getName();
                    addToZip(zos, child, childRelativePath, comment, recursive);
                }
            }
        }

    }

    /**
     * Adds byte content into the zip as a file.
     */
    public static void addToZip(ZipOutputStream zos, byte[] content, String path, String comment) throws
            IOException {
        while (path.length() != 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }

        if (StringUtils.endsWithChar(path, '/')) {
            path = path.substring(0, path.length() - 1);
        }

        ZipEntry zipEntry = new ZipEntry(path);
        zipEntry.setTime(System.currentTimeMillis());

        if (comment != null) {
            zipEntry.setComment(comment);
        }

        zos.putNextEntry(zipEntry);

        InputStream is = new ByteArrayInputStream(content);
        try {
            StreamUtil.copy(is, zos);
        } finally {
            StreamUtil.close(is);
        }

        zos.closeEntry();
    }

    public static void addFolderToZip(ZipOutputStream zos, String path, String comment) throws
            IOException {
        while (path.length() != 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }

        // add folder record
        if (!StringUtils.endsWithChar(path, '/')) {
            path += '/';
        }

        ZipEntry zipEntry = new ZipEntry(path);
        zipEntry.setTime(System.currentTimeMillis());

        if (comment != null) {
            zipEntry.setComment(comment);
        }

        zipEntry.setSize(0);
        zipEntry.setCrc(0);

        zos.putNextEntry(zipEntry);
        zos.closeEntry();
    }

    /**
     * Closes zip file safely.
     */
    public static void close(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException ioex) {
                // ignore
            }
        }
    }

}
