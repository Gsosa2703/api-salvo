package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String message;
    private LocalTime hora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer ;

    public Message (){
    }

    public Message (String message){
        this.message = message;
        this.hora = LocalTime.now();
    }

    public Map<String,Object> toDto(){
        Map <String,Object> dto = new LinkedHashMap<>();
        dto.put("date", this.hora);
        dto.put("message", this.message);
        return dto;
    }

    public String getMessage() {
        return message;
    }

    public LocalTime getHora() {
        return hora;
    }
    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

}
