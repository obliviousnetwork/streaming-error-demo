package com.example.demo;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Protocol;
import okio.BufferedSink;

/**
 * Example demonstrating HTTP/2 streaming using OkHttp. This class sends
 * streamed data to a
 * specified endpoint using HTTP/2.
 */
public class Http2StreamingExample {

	// Target Server URLs:
	// DIRECT_URL : direct backend hosted gateway
	// OBN_URL : Relay endpoints that stream traffic via relay to backend server.
	private static final String DIRECT_URL = "https://token-ohttp-dev.arcane.samsungspc.cloud/ogw";
	private static final String OBN_URL = "https://relay.oblivious.network/dreamy-snowflake-12";
	private final OkHttpClient httpClient;

	public Http2StreamingExample() {
		// Configure the HTTP client to support both HTTP/2 and HTTP/1.1
		this.httpClient = new OkHttpClient.Builder()
				.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
				.build();
	}

	/**
	 * Entry point to simulation
	 *
	 * @apiNote : init the http2 streaming example class and start streaming
	 */
	public static void main(String[] args) throws IOException {
		Http2StreamingExample example = new Http2StreamingExample();
		example.executeStreamingWorkflow();
	}

	/**
	 * @throws IOException if an I/O error occurs
	 * @apiNote : Start streamed data to the OBN endpoint.
	 */
	public void executeStreamingWorkflow() throws IOException {

		// create streaming request and setup a network call
		CustomRequestBody requestBody = initStreaming();

		// Send server handshake data
		sendHandShakeData(requestBody);

		// if above work send more data like handshake in bytes

	}

	/**
	 * @apiNote : Create a full duplex request with provided URL (@DIRECT_URL
	 *          / @OBN_URL)
	 *          <p>
	 *          Setup full duplex request with client and attach a callback as
	 *          listener of data received
	 */
	private CustomRequestBody initStreaming() {
		CustomRequestBody requestBody = new CustomRequestBody();
		Request request = new Request.Builder()
				.url(OBN_URL) // <- ## set url for request
				.post(requestBody)
				.build();

		// set the request to api client
		httpClient.newCall(request)
				// set-up the client callback <- ## attach response listener
				.enqueue(new ClientCallback());
		return requestBody;
	}

	/**
	 * Sets the key configuration data to be sent in the request.
	 *
	 * @param requestBody the request body to which the data will be set
	 * @throws IOException if an I/O error occurs
	 */
	private void sendHandShakeData(CustomRequestBody requestBody) throws IOException {
		// Example key configuration data (byte array)
		byte[] keyConfigBytes = new byte[] {
				10, -117, 1, 10, 64, 101, 99, 48, 99, 98, 56, 56, 52, 51, 51, 50,
				53, 98, 53, 100, 50, 52, 54, 98, 101, 52, 55, 48, 99, 54, 50, 50,
				54, 57, 102, 100, 50, 49, 54, 97, 56, 53
		};
		requestBody.setData(keyConfigBytes);
	}

	/**
	 * @apiNote : Client listener callback to keep a note of data/error recieved
	 */
	private static class ClientCallback implements Callback {

		/**
		 * @apiNote : Called when the request could not be executed due to cancellation,
		 *          a connectivity
		 *          problem or timeout.
		 */
		@Override
		public void onFailure(Call call, IOException e) {
			e.printStackTrace();
		}

		/**
		 * @apiNote : called when client get response (data/error) from server
		 */
		@Override
		public void onResponse(Call call, Response response) throws IOException {
			if (response.isSuccessful()) {
				System.out.println("Response received successfully");
			} else {
				try {
					System.out.println("Response status code: " + response.code());

					String responseBody = response.body().string();
					System.out.println("Response body: " + responseBody);
				} catch (IOException e) {
					System.out.println("Error reading response body: " + e.getMessage());
				}
			}
		}
	}
}

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

class DemoApplicationTests {
	@Test
	public void run() throws Exception {
		Http2StreamingExample.main(null);
	}
}
