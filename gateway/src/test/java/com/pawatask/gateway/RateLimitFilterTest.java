package com.pawatask.gateway;

import com.pawatask.gateway.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import util.IntTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest extends IntTestBase {
	private final String endpoint = "/api/task/all";

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
