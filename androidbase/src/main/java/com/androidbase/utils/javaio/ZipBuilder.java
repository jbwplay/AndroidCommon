package com.androidbase.utils.javaio;

import com.androidbase.utils.StringConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipOutputStream;

/**
 * ZIP builder class for building both files or in-memory zips.
 */
public class ZipBuilder {

    private final ZipOutputStream zos;
    private final File targetZipFile;
    private final ByteArrayOutputStream targetBaos;

    public static ZipBuilder createZipFile(File zipFile) throws IOException {
        return new ZipBuilder(zipFile);
    }

    public static ZipBuilder createZipFile(String zipFile) throws IOException {
        return new ZipBuilder(new File(zipFile));
    }

    public static ZipBuilder createZipInMemory() {
        return new ZipBuilder();
    }

    protected ZipBuilder(File zipFile) throws IOException {
        if (!FileUtils.isExistingFile(zipFile)) {
            FileUtils.touch(zipFile);
        }
        zos = new ZipOutputStream(new FileOutputStream(zipFile));
        targetZipFile = zipFile;
        targetBaos = null;
    }

    protected ZipBuilder() {
        targetZipFile = null;
        targetBaos = new ByteArrayOutputStream();
        zos = new ZipOutputStream(targetBaos);
    }

    public File toZipFile() {
        StreamUtil.close(zos);

        return targetZipFile;
    }

    public byte[] toBytes() {
        StreamUtil.close(zos);

        if (targetZipFile != null) {
            try {
                return FileUtils.readBytes(targetZipFile);
            } catch (IOException ignore) {
                return null;
            }
        }

        return targetBaos.toByteArray();
    }

    public AddFileToZip add(File source) {
        return new AddFileToZip(source);
    }

    public class AddFileToZip {
        private final File file;
        private String path;
        private String comment;
        private boolean recursive = true;

        private AddFileToZip(File file) {
            this.file = file;
        }

        /**
         * Defines optional entry path.
         */
        public AddFileToZip path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Defines optional comment.
         */
        public AddFileToZip comment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Defines if folders content should be added. Ignored when used for files.
         */
        public AddFileToZip recursive() {
            this.recursive = true;
            return this;
        }

        /**
         * Stores the content into the ZIP.
         */
        public ZipBuilder save() throws IOException {
            ZipUtil.addToZip(zos, file, path, comment, recursive);
            return ZipBuilder.this;
        }
    }

    public AddContentToZip add(String content) {
        try {
            return new AddContentToZip(content.getBytes(StringConstant.UTF_8));
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
    }

    public AddContentToZip add(byte[] content) {
        return new AddContentToZip(content);
    }

    public class AddContentToZip {
        private final byte[] bytes;
        private String path;
        private String comment;

        private AddContentToZip(byte[] content) {
            this.bytes = content;
        }

        /**
         * Defines optional entry path.
         */
        public AddContentToZip path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Defines optional comment.
         */
        public AddContentToZip comment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Stores the content into the ZIP.
         */
        public ZipBuilder save() throws IOException {
            ZipUtil.addToZip(zos, bytes, path, comment);
            return ZipBuilder.this;
        }
    }

    // ---------------------------------------------------------------- folder

    public ZipBuilder addFolder(String folderName) throws IOException {
        ZipUtil.addFolderToZip(zos, folderName, null);
        return this;
    }

}