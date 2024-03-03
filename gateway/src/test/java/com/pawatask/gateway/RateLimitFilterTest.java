package com.pawatask.gateway;

import com.pawatask.gateway.dto.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import util.RedisTestBase;
import util.SetRemoteAddressWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest extends RedisTestBase {
	private final String ipAddress = "127.0.0.1";
	private final String endpoint = "/api/task/all";

	private WebTestClient client;

	@BeforeEach
	void setUp(ApplicationContext context) {
		client = WebTestClient.bindToApplicationContext(context)
				.webFilter(new SetRemoteAddressWebFilter(ipAddress))
				.configureClient()
				.build();
	}

	@Test
	void requestPerSecondExceeded() {
		redisTemplate.opsForValue().set("rps:" + ipAddress, 99L).subscribe();

		client.get().uri(endpoint)
				.exchange()
				.expectStatus().isUnauthorized()
				.expectBody(Void.class);

		EntityExchangeResult<ErrorDto> result =
				client.get().uri(endpoint)
						.exchange()
						.expectStatus().isEqualTo(429)
						.expectBody(ErrorDto.class)
						.returnResult();

		assertThat(result.getResponseBody()).isNotNull();
		assertThat(result.getResponseBody().message).isEqualTo("Rate limit exceeded");
	}

	@Test
	void requestPerMinuteExceeded() {
		redisTemplate.opsForValue().set("rpm:" + ipAddress, 999L).subscribe();

		client.get().uri(endpoint)
				.exchange()
				.expectStatus().isUnauthorized()
				.expectBody(Void.class);

		EntityExchangeResult<ErrorDto> result =
				client.get().uri(endpoint)
						.exchange()
						.expectStatus().isEqualTo(429)
						.expectBody(ErrorDto.class)
						.returnResult();

		assertThat(result.getResponseBody()).isNotNull();
		assertThat(result.getResponseBody().message).isEqualTo("Rate limit exceeded");

		Long isBlocked = redisTemplate.opsForValue().get("blocked:" + ipAddress).block();
		assertThat(isBlocked).isEqualTo(-1L);
	}

	@Test
	void blocked() {
		redisTemplate.opsForValue().set("blocked:" + ipAddress, -1L).subscribe();

		EntityExchangeResult<ErrorDto> result =
				client.get().uri(endpoint)
						.exchange()
						.expectStatus().isEqualTo(429)
						.expectBody(ErrorDto.class)
						.returnResult();

		assertThat(redisTemplate.opsForValue().get("rpm:" + ipAddress).block()).isNull();
		assertThat(redisTemplate.opsForValue().get("rps:" + ipAddress).block()).isNull();
	}
}
