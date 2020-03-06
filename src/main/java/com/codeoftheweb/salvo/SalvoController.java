package com.codeoftheweb.salvo;


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


    @RequestMapping(path = "players", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestParam String userName, @RequestParam String password, @RequestParam String name) {

        if (userName.isEmpty() || password.isEmpty() || name.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        player = playerRepository.save(new Player(userName, name, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("id", player.getId()), HttpStatus.CREATED);


    }

    @RequestMapping( path="games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame (@PathVariable Long id, Authentication auth ){
            if (isGuest(auth)) {
                return new ResponseEntity<>(makeMap("error", "You can't create games if you're not logged"), HttpStatus.UNAUTHORIZED);
            }
            else {
                Player playerLog = playerRepository.findByUserName(auth.getName());
                Optional<Game> game = gameRepository.findById(id);

                if(!game.isPresent()){
                    return new ResponseEntity<>(makeMap("error", "This game doesn't exist"), HttpStatus.FORBIDDEN);
                }

                if (game.get().getGamePlayers().size() > 1 ){
                    return new ResponseEntity<>(makeMap("error", "This game is full"), HttpStatus.FORBIDDEN);
                }

                GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(playerLog, game.get()));


                return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
            }
    }



    @RequestMapping(path = "games", method = RequestMethod.POST)
    public ResponseEntity<Object>createGame(Authentication auth ) {
        if (isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "You can't create games if you're not logged"), HttpStatus.UNAUTHORIZED);
        }
        else {
            Player playerLog = playerRepository.findByUserName(auth.getName());
            Game newGame = gameRepository.save (new Game(LocalDateTime.now()));
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(playerLog, newGame));

            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping("game_view/{id}")
    public ResponseEntity<Map<String, Object>> gpGame(@PathVariable Long id,
                                                      Authentication auth) {

        Player playerLog = playerRepository.findByUserName(auth.getName());
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(id);

        if (playerLog.getId() != gamePlayer.get().getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "No tiene acceso"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>( gamePlayer.get().gameViewDTO(), HttpStatus.OK);
    }
     @RequestMapping("games/players/{gamePlayerId}/ships")
     public ResponseEntity <Object> Ships (Authentication auth, @PathVariable Long gamePlayerId){
         if (isGuest(auth)) {
             return new ResponseEntity<>(makeMap("error", "You can't access if you're not logged"), HttpStatus.UNAUTHORIZED);
         }
         Player playerLog = playerRepository.findByUserName(auth.getName());
         Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

         if (playerLog.getId() != gamePlayer.get().getPlayer().getId()) {
             return new ResponseEntity<>(makeMap("error", "No tiene acceso"), HttpStatus.UNAUTHORIZED);
         }
         if(gamePlayer.get().getShips().size() > 0){
             return new ResponseEntity<>(makeMap("error", "You already has ships"), HttpStatus.UNAUTHORIZED);

         }

        return null;
     }
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;

    }
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}