package com.pawatask.gateway;

import com.pawatask.gateway.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import util.IntTestBase;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = "url.task-service=http://localhost:${wiremock.server.port}")
public class AuthFilterTest extends IntTestBase {
  @Test
  void missingAuthHeader() {
    String authenticatedUrl = "/api/task/all";

    EntityExchangeResult<ErrorDto> result =
        client.get().uri(authenticatedUrl)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorDto.class)
            .returnResult();

    assertThat(result.getResponseBody()).isNotNull();
    assertThat(result.getResponseBody().message).isEqualTo("Missing credentials");
  }

  @Test
  void incorrectAuthToken() {
    String authenticatedUrl = "/api/task/all";

    EntityExchangeResult<ErrorDto> result =
        client.get().uri(authenticatedUrl)
            .header("Authorization", "Bearer not-a-proper-jwt")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorDto.class)
            .returnResult();

    assertThat(result.getResponseBody()).isNotNull();
    assertThat(result.getResponseBody().message).isEqualTo("Incorrect authentication token");
  }

  @Test
  void expiredToken() {
    String authenticatedUrl = "/api/task/all";
    String expiredToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjgsImVtYWlsIjoibmlnZXJAZW1haWwucnUiLCJpc" +
        "3MiOiJwYXdhVGFzayIsImlhdCI6MTcwOTQ2NDY2MiwiZXhwIjoxNzA5NDY0NjcyfQ.McYyiPDRK9XvBCc4TAHE5YLK4M4MSwWCDVssuZvuQ-" +
        "hHhg30b9SHJbh24XOL2qlWBmsmjouSSXLPhamBCVoNRQ";

    EntityExchangeResult<ErrorDto> result =
        client.get().uri(authenticatedUrl)
            .header("Authorization", "Bearer %s".formatted(expiredToken))
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorDto.class)
            .returnResult();

    assertThat(result.getResponseBody()).isNotNull();
    assertThat(result.getResponseBody().message).isEqualTo("The Token has expired on 2024-03-03T11:17:52Z.");
  }

  @Test
  void properToken_userIdIsSet() {
    String authenticatedUrl = "/api/task/all";
    String expiredToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjksImVtYWlsIjoiYWJhZGJiYWZkQGVtYWlsLnJ1I" +
        "iwiaXNzIjoicGF3YVRhc2siLCJpYXQiOjE3MDk0NjYwMjcsImV4cCI6MTk3MDk0NjYwMjd9.SBy-qcUiD4JlXEgi9knYzjY1Vhza6zlJJ9EH" +
        "OrygchVJdRIhUG6G-NlIpohduluOSw1tAYwJwfvOILoRSxl6LQ";

    stubFor(get(urlEqualTo(authenticatedUrl))
        .withHeader("userId", equalTo("9"))
        .willReturn(aResponse().withBody("OK")));

    EntityExchangeResult<String> result = client.get().uri(authenticatedUrl)
        .header("Authorization", "Bearer %s".formatted(expiredToken))
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult();

    assertThat(result.getResponseBody()).isNotNull();
    assertThat(result.getResponseBody()).isEqualTo("OK");
  }
}
