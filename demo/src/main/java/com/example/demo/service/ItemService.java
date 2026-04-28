package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository repo;

    public Item add(Item item) {
        normalizeCopies(item);
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

    public Page<Item> searchPaged(String title, String type, String author, Pageable pageable) {
        Specification<Item> specification = Specification.where(null);

        if (StringUtils.hasText(title)) {
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(type)) {
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("type")), "%" + type.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(author)) {
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
        }

        return repo.findAll(specification, pageable);
    }

    public Item update(int id, Item newItem) {
        Item item = getById(id);

        item.setTitle(newItem.getTitle());
        item.setIsbn(newItem.getIsbn());
        item.setType(newItem.getType());
        item.setAuthor(newItem.getAuthor());
        item.setTotalCopies(newItem.getTotalCopies());
        item.setAvailableCopies(newItem.getAvailableCopies());
        normalizeCopies(item);

        return repo.save(item);
    }

    public void delete(int id) {
        Item item = getById(id);
        repo.delete(item);
    }

    private void normalizeCopies(Item item) {
        if (item.getTotalCopies() <= 0) {
            item.setTotalCopies(1);
        }

        if (item.getAvailableCopies() < 0) {
            item.setAvailableCopies(0);
        }

        if (item.getAvailableCopies() > item.getTotalCopies()) {
            item.setAvailableCopies(item.getTotalCopies());
        }
    }
}