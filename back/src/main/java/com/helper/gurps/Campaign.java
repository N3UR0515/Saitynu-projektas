package com.helper.gurps;

import com.helper.gurps.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "campaign")
@Getter
@Setter
public class Campaign {

    // Getter and Setter methods
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Getter
    @Column(name = "ptsLimit")
    private int ptsLimit = -1;

    @OneToMany(mappedBy="campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Character> characters = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Campaign() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void addChar(Character character)
    {
        this.characters.add(character);
    }

    public void setPtsLimit(int ptsLimit) {
        this.ptsLimit = ptsLimit;
    }
}
