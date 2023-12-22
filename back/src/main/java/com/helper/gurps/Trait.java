package com.helper.gurps;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trait")
public class Trait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trait_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name="description", length = 1000)
    private String description;

    @Column(name = "price")
    private int price;

    @ManyToOne
    @JoinColumn(name="character_id", nullable=false)
    private Character character;
    public Trait(){}

    public Trait(String name, Character character, int price) {
        this.name = name;
        this.character = character;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
