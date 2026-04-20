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

    private String type;
    private String author;

    public Item() {}

    public Item(String title, String type, String author) {
        this.title = title;
        this.type = type;
        this.author = author;
    }
}