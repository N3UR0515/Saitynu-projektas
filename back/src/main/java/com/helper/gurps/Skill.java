package com.helper.gurps;

import jakarta.persistence.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name="description", length = 1000)
    private String description;

    @Column(name = "level")
    private int level;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;


    @ManyToOne
    @JoinColumn(name="character_id", nullable=false)
    private Character character;

    public Skill(){}

    public Skill(String name, Character character) {
        this.name = name;
        this.character = character;
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
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
