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
		final String OB2_URL = "https://relay2.oblivious.network/dreamy-snowflake-12";
		final String LOCAL_SPC = "https://localhost:9100/ogw";
		final String LOCAL_OBN = "https://localhost:9000/dreamy-snowflake-12";
		final String LOCAL_DEV = "https://localhost:9200/dreamy-snowflake-12";
		final String TEST_FLY = "https://fullduplex.fly.dev/500";
		final String TEST_RELAY = "https://relay.oblivious.network/fullduplex-500";
		final String LOCAL_OB2 = "http://127.0.0.1:7676/dreamy-snowflake-12";

		// Http2StreamingExample.main(SPC_URL);
		Http2StreamingExample.main(OB2_URL);
		// Http2StreamingExample.main(LOCAL_OB2);
		Thread.sleep(10000);
	}
}
