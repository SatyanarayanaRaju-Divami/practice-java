package com.example.practicejava.league.controller;

import com.example.practicejava.auth.JwtAuthenticationFilter;
import com.example.practicejava.auth.JwtService;
import com.example.practicejava.auth.SecurityConfig;
import com.example.practicejava.league.League;
import com.example.practicejava.league.LeagueNotFoundException;
import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.service.LeagueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeagueController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class LeagueControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean LeagueService leagueService;
    @MockBean JwtService jwtService;

    @Test
    void list_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/leagues"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void list_returnsPaginatedLeagues() throws Exception {
        League league = new League("IPL", "Indian Premier League");
        var page = new PageImpl<>(List.of(league), PageRequest.of(0, 20), 1);
        when(leagueService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/leagues?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("IPL"));
    }

    @Test
    @WithMockUser
    void get_returns404WhenLeagueNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(leagueService.findById(id)).thenThrow(new LeagueNotFoundException(id));

        mockMvc.perform(get("/api/v1/leagues/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201WhenAdmin() throws Exception {
        League league = new League("IPL", "desc");
        when(leagueService.create(any(CreateLeagueRequest.class))).thenReturn(league);

        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLeagueRequest("IPL", "desc"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("IPL"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_returns201ForAnyAuthenticatedUser() throws Exception {
        League league = new League("IPL", "desc");
        when(leagueService.create(any(CreateLeagueRequest.class))).thenReturn(league);

        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLeagueRequest("IPL", "desc"))))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/leagues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"desc\"}"))
                .andExpect(status().isBadRequest());
    }
}
