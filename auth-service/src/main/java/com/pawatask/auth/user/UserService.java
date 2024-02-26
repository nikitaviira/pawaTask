package com.pawatask.auth.user;

import com.pawatask.auth.dto.CredentialsDto;
import com.pawatask.auth.dto.LoginRequestDto;
import com.pawatask.auth.dto.RegisterRequestDto;
import com.pawatask.auth.exception.ServiceException;
import com.pawatask.auth.util.JwtGeneration;
import com.pawatask.auth.util.PasswordHashing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final JwtGeneration jwtGeneration;
  private final PasswordHashing passwordHashing;

  public CredentialsDto register(RegisterRequestDto request) {
    Optional<User> existingUser = userRepository.findByEmail(request.email());
    if (existingUser.isPresent()) {
      throw userAlreadyExists();
    }

    String hashedPassword = passwordHashing.createHash(request.password());

    var newUser = new User();
    newUser.setUserName(request.userName());
    newUser.setEmail(request.email());
    newUser.setHashedPassword(hashedPassword);
    userRepository.save(newUser);

    return createCredentialsResponse(newUser);
  }

  public CredentialsDto login(LoginRequestDto request) {
    return userRepository.findByEmail(request.email())
        .map(user -> {
          if (!passwordHashing.validatePassword(request.password(), user.getHashedPassword())) {
            throw invalidCredentials();
          }

          return createCredentialsResponse(user);
        })
        .orElseThrow(this::invalidCredentials);
  }

  private CredentialsDto createCredentialsResponse(User user) {
    return new CredentialsDto(jwtGeneration.generate(user));
  }

  private ServiceException invalidCredentials() {
    return new ServiceException("Incorrect email or password");
  }

  private ServiceException userAlreadyExists() {
    return new ServiceException("User with such email already exists");
  }
}
