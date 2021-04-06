package com.androidbase.okhttp;

import com.androidbase.utils.MiscUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MultiPartFile {
    public final String name;
    public final String contentType;
    public final File file;
    public final String fileName;
    private RequestBody body;

    public MultiPartFile(String name, File file, String mimeType, String fileName) {
        MiscUtils.notNull(name, "name can not be null.");
        MiscUtils.notNull(file, "file can not be null.");
        MiscUtils.notNull(mimeType, "mimeType can not be null.");
        MiscUtils.notNull(fileName, "fileName can not be null.");
        this.name = name;
        this.contentType = mimeType;
        this.file = file;
        this.fileName = fileName;
        this.body = RequestBody.create(MediaType.parse(contentType), file);
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public RequestBody getBody() throws IOException {
        return body;
    }
}
