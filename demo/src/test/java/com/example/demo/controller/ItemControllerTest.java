package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testAdd() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setTitle("Java");

        when(service.add(Mockito.any())).thenReturn(item);

        mockMvc.perform(post("/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.title").value("Java"));
    }

    @Test
    @WithMockUser
    void testGetAll() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setTitle("Java");

        when(service.getAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"));
    }

    @Test
    @WithMockUser
    void testSearch() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setTitle("Java");

        when(service.search("java")).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .param("keyword", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"));
    }

    @Test
    @WithMockUser
    void testGetById() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setTitle("Java");

        when(service.getById(1)).thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java"));
    }

    @Test
    @WithMockUser
    void testUpdate() throws Exception {
        Item updated = new Item();
        updated.setId(1);
        updated.setTitle("Spring");

        when(service.update(Mockito.eq(1), Mockito.any()))
                .thenReturn(updated);

        mockMvc.perform(put("/items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring"));
    }

    @Test
    @WithMockUser
    void testDelete() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .with(csrf()))
                .andExpect(status().isNoContent()); // 204
    }
}