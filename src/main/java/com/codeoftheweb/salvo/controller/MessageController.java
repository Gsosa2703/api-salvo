package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.AppMessages;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Message;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/")
public class MessageController {

 @Autowired
    MessageRepository messageRepository;
 @Autowired
    GamePlayerRepository gamePlayerRepository;

 @PostMapping("games/players/{gamePlayerId}messages")
    public ResponseEntity<Map<String,Object>> createMessage(@PathVariable Long gamePlayerId, @RequestBody String message) {
     Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

      messageRepository.save(new Message(message));

     return new ResponseEntity<>(makeMap(AppMessages.success, AppMessages.SALVOES_SAVED), HttpStatus.CREATED);
 }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}


