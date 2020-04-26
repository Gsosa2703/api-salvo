package com.codeoftheweb.salvo.Models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private int turn;

    @ElementCollection
    @Column(name = "salvoLocation")
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ship_id")
    private GamePlayer gamePlayer;

    public Salvo() {

    }

    public Salvo(int turn, List<String> salvoLocations) {
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> salvoDTO() {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.turn);
        dto.put("salvoLocations", this.salvoLocations);
        dto.put("player", this.gamePlayer.getPlayer().getId());
        return dto;

    }

    public Map<String, Object> hitsDto() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.turn);
        dto.put("hits", this.getHits());
        dto.put("ships", this.getShips());
        dto.put("left", this.getLeft());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getHits() {
        List<String> hits = new ArrayList<>();
        if (this.gamePlayer.getOpponent().isPresent()) {
            hits = this.salvoLocations.stream().filter(eachLocation -> {
                return this.gamePlayer.getOpponent().get().getShips()
                        .stream().anyMatch(ship -> ship.getLocations().contains(eachLocation));
            }).collect(Collectors.toList());
        }
        return hits;
    }

    public int getLeft() {
        List<String> allShips = new ArrayList<>();
        List<String> shipsHundidos = this.getSinks();
        if (this.gamePlayer.getOpponent().isPresent()) {
            allShips = this.gamePlayer.getShips().stream().map(Ship::getType).collect(Collectors.toList());

        }

        return allShips.size() - shipsHundidos.size();
    }


    public List<String> getShips() {
        List<String> ships = new ArrayList<>();
        if (this.gamePlayer.getOpponent().isPresent()) {
            ships = this.gamePlayer.getOpponent().get().getShips().stream().filter(ship -> {
                return this.gamePlayer.getSalvoes().stream().filter(salvo -> salvo.getTurn() == this.turn)
                        .anyMatch(salvo -> salvo.getSalvoLocations().stream()
                                .anyMatch(location -> ship.getLocations().contains(location)));

            }).map(Ship::getType).collect(Collectors.toList());
        }
        return ships;
    }


    public List<String> getSinks() {
        List<String> salvoes = new ArrayList<>();

        this.gamePlayer.getSalvoes().stream().filter(salvo -> salvo.getTurn() <= this.turn)
                .forEach(salvo -> salvoes.addAll(salvo.salvoLocations));

        return this.gamePlayer.getOpponent().get().getShips().stream().filter(ship ->
                salvoes.containsAll(ship.getLocations())
        ).map(Ship::getType).collect(Collectors.toList());

    }

    public Map<String, Object> sinkDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("ships", this.getSinks());

        return dto;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
}
