const queryString = window.location.search;
console.log(queryString);

const urlParams = new URLSearchParams(window.location.search);
const myParam = urlParams.get('gp');
console.log(myParam);


function crearJson() {
  fetch("http://localhost:8080/api/game_view/" + myParam, )
    .then(function (response) {
      return response.json()
    })
    .then((json) => {



      obtenerPlayers(json);
      obtenerShips(json);
      obtenerSalvoes(json);
      console.log(app.oponent.id);

    });

}
crearJson();

var app = new Vue({
  el: '#app',
  data: {
    player: [],
    oponent: [],
    numbers: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    letters: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'],
  }
});

function obtenerPlayers(json) {
  var idGamePlayer = json.id;
  var gamePlayers = json.gamePlayers.filter(gamePlayer => gamePlayer.id == idGamePlayer);
  var gamePlayers2 = json.gamePlayers.filter(gamePlayer => gamePlayer.id != idGamePlayer);

  app.player = gamePlayers[0].player;
  if (gamePlayers2[0] != null) {
    app.oponent = gamePlayers2[0].player;
  }
}

function obtenerShips(json) {
  var ships = json.ships;
  for (var i=1; i< ships.length; i++) {
    var shipLocations = ships[0].locations.concat(ships[i].locations);
    dibujarLocations(shipLocations);
  }
  return shipLocations;
}

function obtenerSalvoes(json) {
  var salvoesPlayer = json.salvoes.filter(salvo => salvo.player == app.player.id);
  var salvoesOponent = json.salvoes.filter(salvo => salvo.player == app.oponent.id);
  for (let i = 0; i < salvoesPlayer.length; i++) {
    for (let j = 0; j < salvoesOponent.length; j++) {
      dibujarSalvoes(salvoesPlayer[i].locations, salvoesPlayer[i].turn, salvoesOponent[j].locations, obtenerShips(json) );
    }
  }

}

function dibujarLocations(posiciones) {
  for (i = 0; i < posiciones.length; i++) {
    document.getElementById(posiciones[i]).classList.add("tdColoreado");
  }
}

function dibujarSalvoes(salvoesPlayer, turn, salvoesOponent, shipLocations) {

  for (let i = 0; i < salvoesPlayer.length; i++) {
    document.getElementById(salvoesPlayer[i] + "s").innerHTML = turn;
    document.getElementById(salvoesPlayer[i] + "s").classList.add("tdSalvoes");
  }

  for (let k = 0; k < shipLocations.length; k++) {
    for (let j = 0; j < salvoesOponent.length; j++) {
      if (salvoesOponent[j] == shipLocations[k]) {
        document.getElementById(salvoesOponent[j]).innerHTML = "x";
        document.getElementById(salvoesOponent[j]).classList.add("celdasTiroteadas");
      }
    }
  }
}

