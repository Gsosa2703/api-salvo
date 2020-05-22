package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.AppMessages;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    ///POST PARA CREAR JUEGOS
    @RequestMapping("games")
    public Map<String, Object> Games(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("playerLogged", null);
        } else {
            Player playerLogged = playerRepository.findByUserName(authentication.getName());
            dto.put("playerLogged", playerLogged.toDTO());

        }
        dto.put("games",
                gameRepository
                        .findAll()
                        .stream()
                        .map(Game::toDTO)
                        .collect(Collectors.toList()));
        return dto;
    }

    //POST PARA CREAR UN USER
    @PostMapping("players")
    public ResponseEntity<Object> createUser(@RequestParam String userName, @RequestParam String password, @RequestParam String name) {


        if (userName.isEmpty() || password.isEmpty() || name.isEmpty()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NO_NAME), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.USER_FOUND), HttpStatus.CONFLICT);
        }
        player = playerRepository.save(new Player(userName, name, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap(AppMessages.GPID, player.getId()), HttpStatus.CREATED);


    }

    //POST PARA CREAR JUEGOS Y  UNIRTE

    @PostMapping("games/{id}/players")
    public ResponseEntity<Object> joinGame(@PathVariable Long id, Authentication auth) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_FOUND), HttpStatus.UNAUTHORIZED);
        } else {
            Player playerLog = playerRepository.findByUserName(auth.getName());
            Optional<Game> game = gameRepository.findById(id);

            if (!game.isPresent()) {
                return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.GAME_NOT_FOUND), HttpStatus.FORBIDDEN);
            }

            if (game.get().getGamePlayers().size() > 1) {
                return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.GAME_FULL), HttpStatus.FORBIDDEN);
            }

            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(playerLog, game.get()));


            return new ResponseEntity<>(makeMap(AppMessages.GPID, newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }


    @PostMapping("games")
    public ResponseEntity<Object> createGame(Authentication auth) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_LOGGED), HttpStatus.UNAUTHORIZED);
        } else {
            Player playerLog = playerRepository.findByUserName(auth.getName());
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(playerLog, newGame));

            return new ResponseEntity<>(makeMap(AppMessages.GPID, newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    // MAPPING PARA GAME VIEW
    @RequestMapping("game_view/{id}")
    public ResponseEntity<Map<String, Object>> gpGame(@PathVariable Long id,
                                                      Authentication auth) {

        Player playerLog = playerRepository.findByUserName(auth.getName());
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(id);

        if (playerLog.getId() != gamePlayer.get().getPlayer().getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(gamePlayer.get().gameViewDTO(), HttpStatus.OK);
    }

    //POST PARA CREAR SHIPS
    @PostMapping("games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> Ships(Authentication auth, @PathVariable Long gamePlayerId, @RequestBody List<Ship> ships) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_LOGGED), HttpStatus.UNAUTHORIZED);
        }
        Player playerLog = playerRepository.findByUserName(auth.getName());
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

        if (!gamePlayer.isPresent() ) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_FOUND), HttpStatus.UNAUTHORIZED);
        }

        if (playerLog.getId() != gamePlayer.get().getPlayer().getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.get().getShips().size() > 0) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.FOUND_SHIPS), HttpStatus.UNAUTHORIZED);

        }
        ships.stream().forEach(ship -> {
            gamePlayer.get().addShip(ship);
        });


        gamePlayerRepository.save(gamePlayer.get());
        return new ResponseEntity<>(makeMap(AppMessages.success, AppMessages.SHIPS_HAVE_BEEN_SAVED), HttpStatus.CREATED);


    }

    // POST PARA CREAR SALVOES

    @PostMapping("games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> Salvoes(Authentication auth, @PathVariable Long gamePlayerId, @RequestBody Salvo salvo) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_LOGGED), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        Player playerLogged = playerRepository.findByUserName(auth.getName());
        Optional<GamePlayer> opponent = gamePlayer.get().getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer().getId() != playerLogged.getId()).findFirst();

        if (!gamePlayer.isPresent()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_FOUND), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.get().getPlayer().getId() != playerLogged.getId()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.FAIL_USER), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.get().getSalvoes().stream().anyMatch(s -> s.getTurn() == salvo.getTurn())) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.TURN_FOUND), HttpStatus.UNAUTHORIZED);
        }

        if (salvo.getTurn() != gamePlayer.get().getSalvoes().size() + 1) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.TURN_NOT_FOUND), HttpStatus.FORBIDDEN);
        }

        if (!opponent.isPresent()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.WAIT_OPPONENT), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.get().getSalvoes().size() > opponent.get().getSalvoes().size()) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.WAIT_TURN), HttpStatus.FORBIDDEN);
        }

        String stateGame = gamePlayer.get().getStateGame();
        if (stateGame.equals("YOU_WON") || stateGame.equals("BOTH_TIE") || stateGame.equals("YOU_LOST")) {
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.GAME_OVER), HttpStatus.FORBIDDEN);
        }
        if(!stateGame.equals("FIRE")){
            return new ResponseEntity<>(makeMap(AppMessages.error, AppMessages.NOT_SALVOES), HttpStatus.FORBIDDEN);
        }

        gamePlayer.get().addSalvoes(salvo);
        gamePlayerRepository.save(gamePlayer.get());

        stateGame = gamePlayer.get().getStateGame();

        switch (stateGame) {
            case "YOU_WON":
                scoreRepository.save(new Score(1.0, gamePlayer.get().getGame(), gamePlayer.get().getPlayer()));
                scoreRepository.save(new Score(0.0, opponent.get().getGame(), opponent.get().getPlayer()));
                break;
            case "YOU_LOST":
                scoreRepository.save(new Score(0.0, gamePlayer.get().getGame(), gamePlayer.get().getPlayer()));
                scoreRepository.save(new Score(1.0, opponent.get().getGame(), opponent.get().getPlayer()));
                break;
            case "BOTH_TIE":
                scoreRepository.save(new Score(0.5, gamePlayer.get().getGame(), gamePlayer.get().getPlayer()));
                scoreRepository.save(new Score(0.5, opponent.get().getGame(), opponent.get().getPlayer()));
                break;
        }


        return new ResponseEntity<>(makeMap(AppMessages.success, AppMessages.SALVOES_SAVED), HttpStatus.CREATED);


    }


    // METODOS ADICIONALES
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}