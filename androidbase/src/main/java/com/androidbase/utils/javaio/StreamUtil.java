package com.androidbase.utils.javaio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class StreamUtil {

    /**
     * Closes silently the closable object. If it is <code>FLushable</code>, it will be
     * flushed first. No exception will be thrown if an I/O error occurs.
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            if (closeable instanceof Flushable) {
                try {
                    ((Flushable) closeable).flush();
                } catch (IOException ignored) {
                }
            }

            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Copies input stream to output stream using buffer. Streams don't have to be wrapped
     * to buffered, since copying is already optimized.
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        final int ioBufferSize = IODefault.ioBufferSize;
        byte[] buffer = new byte[ioBufferSize];
        int count = 0;
        int read;
        while (true) {
            read = input.read(buffer, 0, ioBufferSize);
            if (read == -1) {
                break;
            }
            output.write(buffer, 0, read);
            count += read;
        }
        return count;
    }

    /**
     * Copies specified number of bytes from input stream to output stream using buffer.
     */
    public static int copy(InputStream input, OutputStream output, int byteCount) throws
            IOException {
        final int ioBufferSize = IODefault.ioBufferSize;
        int bufferSize = Math.min(byteCount, ioBufferSize);

        byte[] buffer = new byte[bufferSize];
        int count = 0;
        int read;
        while (byteCount > 0) {
            if (byteCount < bufferSize) {
                read = input.read(buffer, 0, byteCount);
            } else {
                read = input.read(buffer, 0, bufferSize);
            }
            if (read == -1) {
                break;
            }
            byteCount -= read;
            count += read;
            output.write(buffer, 0, read);
        }
        return count;
    }


    /**
     * Copies input stream to writer using buffer - using jodds default encoding.
     */
    public static void copy(InputStream input, Writer output) throws IOException {
        copy(input, output, IODefault.encoding);
    }

    /**
     * Copies specified number of bytes from input stream to writer using buffer - using
     * jodds default encoding.
     */
    public static void copy(InputStream input, Writer output, int byteCount) throws
            IOException {
        copy(input, output, IODefault.encoding, byteCount);
    }

    /**
     * Copies input stream to writer using buffer and specified encoding.
     */
    public static void copy(InputStream input, Writer output, String encoding) throws
            IOException {
        copy(new InputStreamReader(input, encoding), output);
    }

    /**
     * Copies specified number of bytes from input stream to writer using buffer and
     * specified encoding.
     */
    public static void copy(InputStream input, Writer output, String encoding, int byteCount) throws
            IOException {
        copy(new InputStreamReader(input, encoding), output, byteCount);
    }

    /**
     * Copies reader to writer using buffer. Streams don't have to be wrapped to buffered,
     * since copying is already optimized.
     */
    public static int copy(Reader input, Writer output) throws IOException {
        final int ioBufferSize = IODefault.ioBufferSize;
        char[] buffer = new char[ioBufferSize];
        int count = 0;
        int read;
        while ((read = input.read(buffer, 0, ioBufferSize)) >= 0) {
            output.write(buffer, 0, read);
            count += read;
        }
        output.flush();
        return count;
    }

    /**
     * Copies specified number of characters from reader to writer using buffer.
     */
    public static int copy(Reader input, Writer output, int charCount) throws
            IOException {
        final int ioBufferSize = IODefault.ioBufferSize;
        int bufferSize = Math.min(charCount, ioBufferSize);

        char[] buffer = new char[bufferSize];
        int count = 0;
        int read;
        while (charCount > 0) {
            if (charCount < bufferSize) {
                read = input.read(buffer, 0, charCount);
            } else {
                read = input.read(buffer, 0, bufferSize);
            }
            if (read == -1) {
                break;
            }
            charCount -= read;
            count += read;
            output.write(buffer, 0, read);
        }
        return count;
    }


    /**
     * Copies reader to output stream using buffer - using jodd default encoding.
     */
    public static void copy(Reader input, OutputStream output) throws IOException {
        copy(input, output, IODefault.encoding);
    }

    /**
     * Copies specified number of characters from reader to output stream using buffer -
     * using jodd default encoding.
     */
    public static void copy(Reader input, OutputStream output, int charCount) throws
            IOException {
        copy(input, output, IODefault.encoding, charCount);
    }

    /**
     * Copies reader to output stream using buffer and specified encoding.
     */
    public static void copy(Reader input, OutputStream output, String encoding) throws
            IOException {
        try (Writer out = new OutputStreamWriter(output, encoding)) {
            copy(input, out);
            out.flush();
        }
    }

    /**
     * Copies specified number of characters from reader to output stream using buffer and
     * specified encoding.
     */
    public static void copy(Reader input, OutputStream output, String encoding, int charCount) throws
            IOException {
        try (Writer out = new OutputStreamWriter(output, encoding)) {
            copy(input, out, charCount);
            out.flush();
        }
    }

    /**
     * Reads all available bytes from InputStream as a byte array. Uses
     * <code>in.available()</code> to determine the size of input stream. This is the
     * fastest method for reading input stream to byte array, but depends on stream
     * implementation of <code>available()</code>. Buffered internally.
     */
    public static byte[] readAvailableBytes(InputStream in) throws IOException {
        int l = in.available();
        byte[] byteArray = new byte[l];
        int i = 0, j;
        while ((i < l) && (j = in.read(byteArray, i, l - i)) >= 0) {
            i += j;
        }
        if (i < l) {
            throw new IOException("Failed to completely read input stream");
        }
        return byteArray;
    }

    public static byte[] readBytes(InputStream input) throws IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] readBytes(InputStream input, int byteCount) throws IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output, byteCount);
        return output.toByteArray();
    }

    public static byte[] readBytes(Reader input) throws IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] readBytes(Reader input, int byteCount) throws IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output, byteCount);
        return output.toByteArray();
    }

    public static byte[] readBytes(Reader input, String encoding) throws IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    public static byte[] readBytes(Reader input, String encoding, int byteCount) throws
            IOException {
        FastByteArrayOutputStream output = new FastByteArrayOutputStream();
        copy(input, output, encoding, byteCount);
        return output.toByteArray();
    }

    public static char[] readChars(InputStream input) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output);
        return output.toCharArray();
    }

    public static char[] readChars(InputStream input, int charCount) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, charCount);
        return output.toCharArray();
    }

    public static char[] readChars(InputStream input, String encoding) throws
            IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, encoding);
        return output.toCharArray();
    }

    public static char[] readChars(InputStream input, String encoding, int charCount) throws
            IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, encoding, charCount);
        return output.toCharArray();
    }

    public static char[] readChars(Reader input) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output);
        return output.toCharArray();
    }

    public static char[] readChars(Reader input, int charCount) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, charCount);
        return output.toCharArray();
    }

    /**
     * Compares the content of two byte streams.
     *
     * @return <code>true</code> if the content of the first stream is equal to the
     * content of the second stream.
     */
    public static boolean compare(InputStream input1, InputStream input2) throws
            IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        int ch = input1.read();
        while (ch != -1) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        int ch2 = input2.read();
        return (ch2 == -1);
    }

    /**
     * Compares the content of two character streams.
     *
     * @return <code>true</code> if the content of the first stream is equal to the
     * content of the second stream.
     */
    public static boolean compare(Reader input1, Reader input2) throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }

        int ch = input1.read();
        while (ch != -1) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        int ch2 = input2.read();
        return (ch2 == -1);
    }

}
