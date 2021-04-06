package com.androidbase.utils.javaio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import androidx.annotation.NonNull;

public class FastByteArrayOutputStream extends OutputStream {

    private final FastByteBuffer buffer;

    /**
     * Creates a new byte array output stream. The buffer capacity is initially 1024
     * bytes, though its size increases if necessary.
     */
    public FastByteArrayOutputStream() {
        this(1024);
    }

    /**
     * Creates a new byte array output stream, with a buffer capacity of the specified
     * size, in bytes.
     *
     * @param size the initial size.
     * @throws IllegalArgumentException if size is negative.
     */
    public FastByteArrayOutputStream(int size) {
        buffer = new FastByteBuffer(size);
    }

    /**
     * @see OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) {
        buffer.append(b, off, len);
    }

    /**
     * Writes single byte.
     */
    @Override
    public void write(int b) {
        buffer.append((byte) b);
    }

    /**
     * @see java.io.ByteArrayOutputStream#size()
     */
    public int size() {
        return buffer.size();
    }

    /**
     * Closing a <code>FastByteArrayOutputStream</code> has no effect. The methods in this
     * class can be called after the stream has been closed without generating an
     * <code>IOException</code>.
     */
    @Override
    public void close() {
        //nop
    }

    /**
     * @see java.io.ByteArrayOutputStream#reset()
     */
    public void reset() {
        buffer.clear();
    }

    /**
     * @see java.io.ByteArrayOutputStream#writeTo(OutputStream)
     */
    public void writeTo(OutputStream out) throws IOException {
        int index = buffer.index();
        for (int i = 0; i < index; i++) {
            byte[] buf = buffer.array(i);
            out.write(buf);
        }
        out.write(buffer.array(index), 0, buffer.offset());
    }

    /**
     * @see java.io.ByteArrayOutputStream#toByteArray()
     */
    public byte[] toByteArray() {
        return buffer.toArray();
    }

    /**
     * @see java.io.ByteArrayOutputStream#toString()
     */
    @NonNull
    @Override
    public String toString() {
        return new String(toByteArray());
    }

    /**
     * @see java.io.ByteArrayOutputStream#toString(String)
     */
    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(toByteArray(), enc);
    }

}