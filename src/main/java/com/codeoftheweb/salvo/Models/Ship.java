package com.codeoftheweb.salvo.Models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String type;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {

    }

    public Ship(String type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.type);
        dto.put("locations", this.locations);
        return dto;
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public List<String> getLocations() {
        return locations;
    }
}

