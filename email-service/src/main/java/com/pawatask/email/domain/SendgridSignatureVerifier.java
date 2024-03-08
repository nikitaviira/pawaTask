package com.pawatask.email.domain;

import com.pawatask.email.config.SendGridCredentials;
import com.sendgrid.helpers.eventwebhook.EventWebhook;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

@Component
@RequiredArgsConstructor
public class SendgridSignatureVerifier {
  private final SendGridCredentials sendGridCredentials;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public void verifySignature(String body, String signature, String timestamp) {
    var eventWebhook = new EventWebhook();
    try {
      eventWebhook.VerifySignature(publicKey(eventWebhook), body, signature, timestamp);
    }
    catch (Exception e) {
      throw new RuntimeException("Signature verification failed", e);
    }
  }

  private ECPublicKey publicKey(EventWebhook eventWebhook) {
    try {
      return eventWebhook.ConvertPublicKeyToECDSA(sendGridCredentials.getVerificationKey());
    }
    catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
      throw new RuntimeException(e);
    }
  }
}
