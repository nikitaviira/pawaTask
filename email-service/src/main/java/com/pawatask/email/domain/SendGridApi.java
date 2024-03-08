package com.pawatask.email.domain;

import com.pawatask.email.config.SendGridCredentials;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sendgrid.Method.POST;

@Component
@Slf4j
public class SendGridApi {
  private final SendGrid sendGrid;

  public SendGridApi(SendGridCredentials sendGridCredentials) {
    sendGrid = new SendGrid(sendGridCredentials.getApiKey());
  }

  public boolean send(String body) throws IOException {
    String url = "/mail/send";
    Response response = executeRequest(POST, url, body);
    boolean successfulResponse = isSuccessfulResponse(response);

    if (!successfulResponse) {
      logSendGridError(url, response);
    }

    return successfulResponse;
  }

  private Response executeRequest(Method method, String url, String body) throws IOException {
    var request = new Request();
    request.setMethod(method);
    request.setEndpoint(url);
    request.setBody(body);
    return sendGrid.api(request);
  }

  private boolean isSuccessfulResponse(Response response) {
    return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
  }

  private void logSendGridError(String url, Response response) {
    log.error("SendGrid API error response; URL: {}, Code: {}, Body: {}", url, response.getStatusCode(), response.getBody());
  }
}