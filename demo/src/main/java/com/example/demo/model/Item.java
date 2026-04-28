package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String isbn;

    private String type;
    private String author;

    @Column(nullable = false)
    private int totalCopies = 1;

    @Column(nullable = false)
    private int availableCopies = 1;

    public Item() {}

    public Item(String title, String type, String author) {
        this.title = title;
        this.type = type;
        this.author = author;
        this.totalCopies = 1;
        this.availableCopies = 1;
    }
}