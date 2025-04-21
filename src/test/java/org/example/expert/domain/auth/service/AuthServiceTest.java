package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원 가입이 정상적으로 등록된다.")
    void signup_successfully() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "PwdPwdPwd1", "USER");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false); // 이메일 검증
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");

        // user
        User user = new User(signupRequest.getEmail(), "encodedPassword", UserRole.of(signupRequest.getUserRole()));
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // token
        String bearerToken = "MockToken";
        when(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(bearerToken);

        // when
        SignupResponse signupResponse = authService.signup(signupRequest);

        // then
        assertNotNull(signupResponse);
        assertEquals(bearerToken, signupResponse.getBearerToken());
    }

    @Test
    @DisplayName("회원 가입이 이메일이 존재하지 않아 실패한다")
    void signup_failsWhenUserEmailDoesExist() {
        // given
        String email = "test@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        SignupRequest signupRequest = new SignupRequest(email, "PwdPwdPwd1", "USER");

        // when, then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인이 정상적으로 실행된다.")
    void signin_successfully() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "PwdPwdPwd1");

        User user = new User(signinRequest.getEmail(), "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn("TestToken");

        // when
        SigninResponse signinResponse = authService.signin(signinRequest);

        // then
        assertNotNull(signinResponse);
        assertEquals("TestToken", signinResponse.getBearerToken());
    }

    @Test
    @DisplayName("가입되지 않은 유저일 경우 로그인이 실패한다.")
    void signin_failsWhenUserDoesNotExist() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "PwdPwdPwd1");
        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signin(signinRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("가입되지 않은 유저입니다.");
    }

    @Test
    @DisplayName("이메일과 회원비밀번호가 일치하지 않을 경우 로그인이 실패한다")
    void signin_fails_whenPasswordDoesNotMatch() {
        // given
        String requestUnEncoded = "PwdPwdPwd1";
        String requestEncoded = passwordEncoder.encode(requestUnEncoded);
        String MockEncoded = passwordEncoder.encode(requestEncoded+"1");
        SigninRequest signinRequest = new SigninRequest("test@test.com", requestEncoded);
        User user = new User(signinRequest.getEmail(), MockEncoded, UserRole.USER);
        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.signin(signinRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("잘못된 비밀번호입니다.");

        // verify
        assertEquals(true, passwordEncoder.matches(requestUnEncoded, requestEncoded));
    }
}