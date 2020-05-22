package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Message> messages;

    private LocalDateTime joinDate;

    GamePlayer() {
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("player", this.player.toDTO());
        dto.put("messages",this.messages.stream().map(Message :: toDto).collect(Collectors.toList()));

        Score score = getScore();

        if (score != null) {
            dto.put("score", score.toDTO());
        } else {
            dto.put("score", null);
        }

        Message message = getMessage();
        if (message != null) {
            dto.put("message", message.toDto());
        } else {
            dto.put("message", null);
        }




        return dto;
    }

    public Score getScore() {
        Score score = this.getPlayer().getScore(this.getGame());
        return score;
    }
    public Message getMessage(){
        Message message = this.player.getMessage(this.game);
        return message;
    }

    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("created", this.joinDate);
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::toDTO).collect(Collectors.toList()));
        dto.put("ships", this.ships.stream().map(Ship::shipDTO).collect(Collectors.toList()));
        dto.put("salvoes", this.game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::salvoDTO)).collect(Collectors.toList()));
        dto.put("hitsPlayer", this.salvoes.stream().map(Salvo::hitsDto).collect(Collectors.toList()));
        dto.put("sinksPlayer", this.salvoes.stream().map(Salvo::sinkDTO).collect(Collectors.toList()));
        dto.put("state", this.getStateGame());
        if (!this.getOpponent().isPresent()) {
            dto.put("hitsOponente", "");
            dto.put("sinksOponente", "");
        } else {
            dto.put("hitsOponente", this.getOpponent().get().salvoes.stream().map(Salvo::hitsDto).collect(Collectors.toList()));
            dto.put("sinksOponente", this.getOpponent().get().salvoes.stream().map(Salvo::sinkDTO).collect(Collectors.toList()));

        }
        return dto;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Optional<GamePlayer> getOpponent() {
        return this.game.getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != id).findFirst();

    }


    public String getStateGame() {
        String state = " ";

        if (this.getOpponent().isEmpty()) {
            if (this.getShips().isEmpty()) {
                state = "PLACE_SHIPS";
            } else if (this.getOpponent().isEmpty()) {
                state = "WAIT_OPPONENT";
            }
        } else {

            int myTurn = this.getSalvoes().size();
            int oponentTurn = this.getOpponent().get().getSalvoes().size();
            if (this.getShips().isEmpty()) {
                state = "PLACE_SHIPS";
            } else if (this.getOpponent().get().getShips().isEmpty()) {
                state = "WAIT_OPPONENT_SHIPS";
            } else if (myTurn > oponentTurn) {
                state = "WAIT_OPPONENT_ATTACK";
            } else if (myTurn < oponentTurn){
                state = "FIRE";
            } else if (myTurn == oponentTurn) {
                boolean sinkPlayer = this.getSalvoes().stream().anyMatch(salvo -> salvo.getLeft() == 0);
                boolean sinkOpponent = this.getOpponent().get().getSalvoes().stream().anyMatch(salvo -> salvo.getLeft() == 0);

                if (sinkPlayer && !sinkOpponent) {
                    state = "YOU_WON";
                } else if (!sinkPlayer && sinkOpponent) {
                    state = "YOU_LOST";
                } else if (sinkPlayer && sinkOpponent) {
                    state = "BOTH_TIE";
                } else if(this.getId() > this.getOpponent().get().getId()){
                    state = "FIRE";
                }else if(this.getId() < this.getOpponent().get().getId()){
                    state = "WAIT_OPPONENT_ATTACK";
                }


            }
        }
        return state;
    }


    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvoes(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public long getId() {
        return id;
    }
}
