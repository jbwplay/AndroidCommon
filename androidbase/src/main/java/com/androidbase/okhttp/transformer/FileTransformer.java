package com.androidbase.okhttp.transformer;

import com.androidbase.utils.javaio.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class FileTransformer implements HttpTransformer<File> {

    private String destFileDir;
    private String destFileName;

    public FileTransformer(String filefold, String filename) {
        destFileDir = filefold;
        destFileName = filename;
    }

    @Override
    public File transform(Response response) throws IOException {
        return saveFile(response);
    }

    private File saveFile(Response response) throws IOException {
        File dir = new File(destFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, destFileName);
        if (file.exists()) {
            file.delete();
        }
        InputStream input = response.body().byteStream();
        FileOutputStream output = new FileOutputStream(file);
        try {
            StreamUtil.copy(input, output);
        } finally {
            StreamUtil.close(input);
            StreamUtil.close(output);
        }
        return file;
    }
}
