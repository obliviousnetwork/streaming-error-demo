package com.example.demo;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Custom RequestBody implementation for handling streamed data.
 */
class CustomRequestBody extends RequestBody {

    private byte[] data;

    /**
     * Read the data if present and write on connection
     */
    @Override
    public void writeTo(BufferedSink out) throws IOException {
        if (data != null) {
            out.write(data); // <- ## write data on client connection
            out.flush();
            System.out.println("completed, writing queued data on client call");
        }
    }

    /**
     * Set request content type to message/ohttp-chunked-req
     */
    @Override
    public MediaType contentType() {
        return MediaType.get("message/ohttp-chunked-req");
    }

    /**
     * Basically enable full duplex streaming mode if true Note: OBN relay not able
     * to send error body
     * when true
     */
    @Override
    public boolean isDuplex() {
        return true;
    }

    /**
     * Sets the data to be sent in the request.
     *
     * @param data the byte array containing the data
     */
    public void setData(byte[] data) {
        this.data = data; // <- ## set data to be written
        System.out.println("Data to be sent, queued successfully");
    }
}
