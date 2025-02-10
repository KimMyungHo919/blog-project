package com.project.blog.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blog.domain.user.dto.request.UserDeleteRequestDto;
import com.project.blog.domain.user.dto.request.UserLoginRequestDto;
import com.project.blog.domain.user.dto.request.UserSignupRequestDto;
import com.project.blog.domain.user.dto.response.UserSignupResponseDto;
import com.project.blog.domain.user.entity.User;
import com.project.blog.domain.user.service.UserService;
import com.project.blog.global.constants.SessionAttributeKeys;
import com.project.blog.global.encoder.PasswordEncoder;
import com.project.blog.global.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private String email;
    private String password;
    private String nickname;
    private Role role;
    MockHttpSession session;
    private User user;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();

        email = "test123@naver.com";
        password = "sdfsdfe1A1!";
        nickname = "testUser";
        role = Role.USER;

        user = new User(email, password, nickname, role);
    }


    @Test
    @DisplayName("유저 회원가입")
    void signupUserTest() throws Exception {
        UserSignupRequestDto requestDto = new UserSignupRequestDto(email, password, nickname, Role.USER);
        UserSignupResponseDto responseDto = new UserSignupResponseDto(1L, email, nickname);

        given(userService.signupUser(any(UserSignupRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/public/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.nickname").value(nickname));
    }

    @Test
    @DisplayName("중복 로그인시도 에러반환")
    void alreadyLoginTest() throws Exception {
        session.setAttribute(SessionAttributeKeys.USER, user); // 세션 저장

        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);

        // 로그인된 유저가 요청을 날리면 에러가 난다.
        mockMvc.perform(post("/api/public/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session)) // 로그인된 유저의 session
                .andExpect(status().isBadRequest()); // 400 에러
    }

    @Test
    @DisplayName("로그인 성공")
    void loginUserTest() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);

        given(userService.loginUser(any(UserLoginRequestDto.class))).willReturn(user);

        mockMvc.perform(post("/api/public/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andExpect(jsonPath("$.data.role").value("일반유저"));
    }

    @Test
    @DisplayName("로그아웃 성공 - 세션만료 확인")
    void logoutTest() throws Exception {
        session.setAttribute(SessionAttributeKeys.USER, user);

        mockMvc.perform(post("/api/users/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    @DisplayName("회원탈퇴 성공 - 세션만료 확인")
    void deleteUserTest() throws Exception {
        session.setAttribute(SessionAttributeKeys.USER, user);

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(password);

        mockMvc.perform(delete("/api/users/me")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 암호화 정상작동 확인")
    void passwordEncoderTest() {
        String encodingPassword = passwordEncoder.encode(password);

        assertThat(password).isNotEqualTo(encodingPassword);
    }


}