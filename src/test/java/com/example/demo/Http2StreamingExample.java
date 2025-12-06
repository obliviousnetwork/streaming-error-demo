package com.example.demo;

import org.junit.jupiter.api.Test;

class DemoApplicationTests {
	@Test
	public void run() throws Exception {
		// Target Server URLs:
		// DIRECT_URL : direct backend hosted gateway
		// OBN_URL : Relay endpoints that stream traffic via relay to backend server.
		final String SPC_URL = "https://token-ohttp-dev.arcane.samsungspc.cloud/ogw";
		final String OBN_URL = "https://relay.oblivious.network/dreamy-snowflake-12";
		final String LOCAL_SPC = "https://localhost:9100/ogw";
		final String LOCAL_OBN = "https://localhost:9000/dreamy-snowflake-12";
		final String TEST_FLY = "https://fullduplex.fly.dev/500";
		final String TEST_RELAY = "https://relay.oblivious.network/fullduplex-500";

		Http2StreamingExample.main(LOCAL_SPC);
		Thread.sleep(5000);
	}
}
