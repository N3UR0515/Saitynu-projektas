package com.helper.gurps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helper.gurps.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="characters")
@Getter
@Setter
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "photo")
    private String photo;

    @Column(name="strength")
    private int strength = 0;

    @Column(name="dex")
    private int dex = 0;

    @Column(name="intelligence")
    private int intelligence = 0;

    @Column(name="health")
    private int health = 0;

    @Column(name="hitPoints")
    private int hitPoints = 0;

    @Column(name="will")
    private int will = 0;

    @Column(name="perception")
    private int perception = 0;

    @Column(name="basicSpeed")
    private float basicSpeed = 0;

    @Column(name="basicMove")
    private int basicMove = 0;

    @Column(name="hidden")
    private boolean hidden = false;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="campaign_id", nullable=false)
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy="character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trait> traits = new ArrayList<>();


    public Character() {}

    public Character(String first_name, String last_name, Campaign campaign) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.campaign = campaign;
    }

    public Long getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void setTraits(List<Trait> traits) {
        this.traits = traits;
    }

    public Campaign getCampaign()
    {
        return campaign;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getWill() {
        return will;
    }

    public void setWill(int will) {
        this.will = will;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public float getBasicSpeed() {
        return basicSpeed;
    }

    public void setBasicSpeed(float basicSpeed) {
        this.basicSpeed = basicSpeed;
    }

    public int getBasicMove() {
        return basicMove;
    }

    public void setBasicMove(int basicMove) {
        this.basicMove = basicMove;
    }
}
