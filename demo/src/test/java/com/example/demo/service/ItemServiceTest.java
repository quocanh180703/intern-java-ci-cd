package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository repo;

    @InjectMocks
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item("Java", "Book", "Author B");
    }

    @Test
    void testAdd() {
        when(repo.save(item)).thenReturn(item);

        Item result = itemService.add(item);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Java", result.getTitle());
        verify(repo, times(1)).save(item);
    }

    @Test
    void testGetAll() {
        List<Item> list = Arrays.asList(item);

        when(repo.findAll()).thenReturn(list);

        List<Item> result = itemService.getAll();

        assertEquals(1, result.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testGetById_success() {
        when(repo.findById(1)).thenReturn(Optional.of(item));

        Item result = itemService.getById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Java", result.getTitle());
    }

    @Test
    void testGetById_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.getById(1);
        });
    }

    @Test
    void testSearch() {
        List<Item> list = Arrays.asList(item);

        when(repo.findByTitleContainingIgnoreCase("java")).thenReturn(list);

        List<Item> result = itemService.search("java");

        assertEquals(1, result.size());
        verify(repo, times(1)).findByTitleContainingIgnoreCase("java");
    }

    @Test
    void testUpdate_success() {
        Item newItem = new Item("Spring", "Book", "Author A");

        when(repo.findById(1)).thenReturn(Optional.of(item));
        when(repo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item result = itemService.update(1, newItem);

        assertEquals("Spring", result.getTitle());
        assertEquals("Book", result.getType());
        assertEquals("Author A", result.getAuthor());

        verify(repo).save(item);
    }

    @Test
    void testUpdate_notFound() {
        Item newItem = new Item();

        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.update(1, newItem);
        });
    }

    @Test
    void testDelete_success() {
        when(repo.findById(1)).thenReturn(Optional.of(item));

        itemService.delete(1);

        verify(repo).delete(item);
    }

    @Test
    void testDelete_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.delete(1);
        });
    }
}