// funcion para cambiar el div de LogIn y Register 
$(document).ready(function () {
  $("#button-register").click(function () {
    $("#form-logIn").hide();
  })
  $("#button-register").click(function () {
    $("#form-register").show();
  })

});


//funcion para loguearse 

function login() {

  var username = document.getElementById("email").value;
  var password = document.getElementById("password").value;
  var divRegister = document.getElementById("divRegister")
  if (username == "" || password == "") {
    alert("Please enter your mail and password")
  } else {

    $.post("/api/login", {
      name: username,
      pwd: password
    }).done(function () {

      console.log("you are login");
      //Recargo la pagina 
      location.reload();

    }).fail(function () {
      alert("username or password incorrect")

    })
  }
}

function signIn() {
  var username = document.getElementById("semail").value;
  var password = document.getElementById("spassword").value;
  var name = document.getElementById("name").value;

  $.post("/api/players", {
    userName: username,
    password: password,
    name: name
  }).done(function () {
    document.getElementById("email").value = username;
    document.getElementById("password").value = password;
    login()
  }).fail(function () {
    alert("No se pudo registrar");
  })

}



//funcion para desloguearse 

function logOut() {
  $.post("/api/logout").done(function () {
    console.log("you are logout");
    location.reload();
  })
}

//FUNCION PARA CREAR UN JUEGO
function crearJuego() {
  $.post("/api/games").done(function (data) {
    window.location.assign('/web/game.html?gp=' + data.gpid);
  }).fail(function (error) {
    alert("error");
  })

}

function joinJuego(gameId) {
  $.post("/api/games/" + gameId + "/players")
    .done(function (data) {
      window.location.assign('/web/game.html?gp=' + data.gpid);
    }).fail(function (error) {
      alert("error");
    })

}


//funcion para dibujar Welcome Jugador :) y esconder  el registro;


function interface(player) {
  if (player == null) {

    $(document).ready(function () {

      $("#div-allGames").hide();

    });

  } else {
    var divLogOut = "";
    divLogOut = "<p class=welcome>" + "Welcome to The Battle Ship" + " " + player.name + "</p>";
    document.getElementById("div-logOut").innerHTML += divLogOut;
    //funcion para hacer aparecer el log Out
    $(document).ready(function () {
      setTimeout(function () {
        $("#divRegister").fadeOut(0);
      }, 0);
      setTimeout(function () {
        $(".button-logOut").fadeIn(1000);
      }, 500);
    });
  }


}

//funcion para obtener los juegos y Crear la lista de jugadores
$.ajax({
  url: "/api/games",
  type: "GET",
  DataType: "json"

}).done(function (data) {
  function crearLista(data, playerLogged) {
    if (playerLogged == null) {
      console.log("Logueate para que disfrutes!!")
    } else {
      var valor = '';

      console.log(playerLogged);
      for (var i = 0; i < data.length; i++) {

        valor += "<li>" + data[i].created + "<ul>" + "<h2 class=player>" + "Players:" + "</h2>";

        var gamePlayers = data[i].gamesPlayers;

        for (var j = 0; j < gamePlayers.length; j++) {

          //dibujo la tabla y agrego el link

          var userLog = playerLogged.email;
          var playerGp = gamePlayers[j].player.email;

          var players = gamePlayers[j].player;

          if (userLog == playerGp) {
            var gpId = gamePlayers[j].id;

            valor += "<li>" + players.email + "</li>" + "<a  class=" + "btn" + "btn-ligth" + " href=" + '/web/game.html?gp=' + gpId + ">" + "Go to a Game" + "</a>"

          } else if ((userLog != playerGp) && (gamePlayers.length == 1)) {
            var gameId = data[i].id;
            console.log(gameId);
            var button = '<input type="button" value="Join into a Game" onclick="joinJuego(' + gameId + ')">';


            valor += "<li>" + players.email + "</li>" + button

          } else {
            valor += "</ul>" + "<ul>" + "<li>" + players.email + "</li>"
          }
        }

        valor += '</ul></li>';

        $(".list").html(valor);

      }

    }
  }

  crearLista(data.games, data.playerLogged);
  interface(data.playerLogged);
  // funcion para obtener el Json para la tabla de LeaderBoard 
  function jsonLeaderboard(data) {

    var info = [];
    for (i = 0; i < data.length; i++) {
      var gamePlayers = data[i].gamesPlayers;
      console.log(gamePlayers);

      for (j = 0; j < gamePlayers.length; j++) {
        var newPlayer = info.find(player => player.email == gamePlayers[j].player.email);
        if (newPlayer == undefined) {
          var player = {
            email: '',
            totalScores: 0,
            win: 0,
            losses: 0,
            tied: 0
          }

          var playerScore = gamePlayers[j].score.score;
          player.email = gamePlayers[j].player.email;

          if (playerScore === 1.0) {
            player.win++;
          } else if (playerScore === 0.5) {
            player.tied++
          } else if (playerScore === 0) {
            player.losses++
          } 

          player.totalScores = playerScore;

          info.push(player);
        } else {
          var playerScore = gamePlayers[j].score.score;

          if (playerScore === 1.0) {
            newPlayer.win++;
          } else if (playerScore === 0.5) {
            newPlayer.tied++
          } else if (playerScore === 0) {
            newPlayer.losses++
          } else if (playerScore == null) {
            player.totalScores = +0
          }
         
          newPlayer.totalScores += playerScore;

        }
      }
    }
    console.log(info);
    return info;

  }

  //funcion para ordenar los Scores de mayor a menor 
  function ordenMayoraMenor(listaLeaderboard) {
    listaLeaderboard.sort(function (a, b) { //ordenamos de mayor a menor
      return b.totalScores - a.totalScores
    })
    return listaLeaderboard;
  };

  //funcion para dibujar la tabla LeaderBoard

  function crearTablaLeaderBoard(infoTabla) {

    var tabla = "";


    for (i = 0; i < infoTabla.length; i++) {
      if (infoTabla[i].totalScores === undefined) {
        infoTabla[i].totalScores = 0

        tabla += "<tr>" + "<td>" + infoTabla[i].email + "</td>" +
          "<td>" + infoTabla[i].totalScores + "</td>" +
          "<td>" + infoTabla[i].win + "</td>" +
          "<td>" + infoTabla[i].losses + "</td>" +
          "<td>" + infoTabla[i].tied + "</td>" + "</tr>"
      } else {
        tabla += "<tr>" + "<td>" + infoTabla[i].email + "</td>" +
          "<td>" + infoTabla[i].totalScores + "</td>" +
          "<td>" + infoTabla[i].win + "</td>" +
          "<td>" + infoTabla[i].losses + "</td>" +
          "<td>" + infoTabla[i].tied + "</td>" + "</tr>"
      }
    }
    $(".leaderBoard").html(tabla);
  }
  crearTablaLeaderBoard(ordenMayoraMenor(jsonLeaderboard(data.games)));

});
