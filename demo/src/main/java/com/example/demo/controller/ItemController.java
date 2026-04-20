package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item add(@RequestBody Item item) {
        return service.add(item);
    }

    @GetMapping
    public List<Item> getAll(@RequestParam(required = false) String keyword) {
        if (keyword != null) {
            return service.search(keyword);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable int id, @RequestBody Item item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}