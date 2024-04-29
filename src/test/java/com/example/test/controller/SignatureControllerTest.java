package com.example.test.controller;

import com.example.test.config.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
class SignatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppConfig appConfig;

    @Test
    @DisplayName("Успешный запрос с несколькими параметрами")
    void success() throws Exception {
        when(appConfig.getToken()).thenReturn("test_token");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signature/123")
                                .header("Token", "test_token")
                                .param("name1", "value1")
                                .param("name2", "value2")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].signature").exists());
    }

    @Test
    @DisplayName("Успешный запрос без параметров")
    void successWithNoParams() throws Exception {
        when(appConfig.getToken()).thenReturn("test_token");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signature/" + "123")
                                .header("Token", "test_token")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].signature").exists());
    }

    @Test
    @DisplayName("403 когда нет токена")
    void noToken() throws Exception {
        when(appConfig.getToken()).thenReturn("test_token");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signature/123")
                                .param("name1", "value1")
                                .param("name2", "value2")
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("403 когда токен не совпадает")
    void wrongToken() throws Exception {
        when(appConfig.getToken()).thenReturn("test_token");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signature/" + "123")
                                .header("Token", "wrong_token")
                                .param("name1", "value1")
                                .param("name2", "value2")
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}