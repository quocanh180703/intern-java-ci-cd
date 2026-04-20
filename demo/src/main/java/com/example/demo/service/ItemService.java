package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository repo;

    public Item add(Item item) {
        return repo.save(item);
    }

    public List<Item> getAll() {
        return repo.findAll();
    }

    public Item getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    public List<Item> search(String keyword) {
        return repo.findByTitleContainingIgnoreCase(keyword);
    }

    public Item update(int id, Item newItem) {
        Item item = getById(id);

        item.setTitle(newItem.getTitle());
        item.setType(newItem.getType());
        item.setAuthor(newItem.getAuthor());

        return repo.save(item);
    }

    public void delete(int id) {
        Item item = getById(id);
        repo.delete(item);
    }
}