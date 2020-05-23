const queryString = window.location.search;


const urlParams = new URLSearchParams(window.location.search);
const myParam = urlParams.get('gp');
console.log(myParam);

var grid = null;


var mensajeError = {};

crearJson();

function crearJson() {
  fetch("/api/game_view/" + myParam, )
    .then(function (response) {

      return response.json()
    })
    .then((json) => {


      obtenerPlayers(json);

      app.game_view = json;
      app.shipsCurrentPlayer = app.game_view.ships;
      app.salvoesPlayer = json.salvoes.filter(salvo => salvo.player == app.player.id);
      app.salvoesOponent = json.salvoes.filter(salvo => salvo.player == app.oponent.id);
      app.hitsPlayer = json.hitsPlayer;
      app.hitsOpponent = json.hitsOponente;
      app.sinkPlayer = json.sinksPlayer;
      app.sinkOpponent = json.sinksOponente;
      app.stateGame = json.state;


      alertTurn();

      paintSalvoes();

      crearGrid();

      hitsOnOpponent();


      createHit(app.hitsOpponent);
      sunken()

   


    });

}


// VUE.JS
var app = new Vue({
  el: '#app',
  data: {
    player: [],
    oponent: [],
    numbers: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    letters: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'],
    game_view: [],
    shipsCurrentPlayer: [],
    shipLocalizado: [],
    id: "",
    salvo: {
      turn: 0,
      salvoLocations: []
    },
    salvoesPlayer: [],
    salvoesOponent: [],
    idSalvoPlayer: 0,
    firstTurn: 1,
    hitsPlayer: [],
    hitsOpponent: {},
    sinkPlayer: [],
    shipsunken: [],
    shipSunkenOponent: [],
    firstSink: [],
    firstSinkOponent: [],
    sinkOpponent: [],
    stateGame: ""
  },
  methods: {
    toUpper: function (str) {
      return str[0].toUpperCase() + str.slice(1);
    }
  }

});
//---------------------------------SHIPS-----------------------------------------------------------//
///FUNCION Post de Ships
function postShips() {
  $.post({
    url: "/api/games/players/" + myParam + "/ships",
    data: JSON.stringify(app.shipLocalizado),
    dataType: "text",
    contentType: "application/json"
  }).done(function () {
    location.reload();

  }).fail(function (jqXHR, error) {
    mensajeError = jqXHR.responseText;
    Swal.fire(mensajeError);
  })

}


//FUNCION PARA CREAR GRILLA
function crearGrid() {

  var options = {
    //grilla de 10 x 10
    column: 10,
    minRow: 10,
    //separacion entre elementos (les llaman widgets)
    verticalMargin: 0,
    //altura de las celdas
    cellHeight: 40,
    //desabilitando el resize de los widgets
    disableResize: true,
    //widgets flotantes
    float: true,
    //removeTimeout: 100,
    //permite que el widget ocupe mas de una columna
    disableOneColumnMode: true,
    //false permite mover, true impide
    staticGrid: 0,
    maxRow: 10,
    //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
    animate: true
  }

  grid = GridStack.init(options, '.grid-stack');

  if (app.game_view.ships.length == 0) {
    options.staticGrid = false;
    grid.addWidget('<div id="carrier"><div class="grid-stack-item-content carrierHorizontal"></div></div>', {
      width: 5,
      heigth: 1,
      x: 0,
      y: 0,
      noResize: true,
      id: "carrier"
    })
    grid.addWidget('<div id="battleship"><div class="grid-stack-item-content battleshipHorizontal"></div></div>', {
      width: 4,
      heigth: 1,
      x: 0,
      y: 0,
      noResize: true,
      id: "battleship"
    })
    grid.addWidget('<div id="submarine"><div class="grid-stack-item-content submarineHorizontal"></div></div>', {
      width: 3,
      heigth: 1,
      x: 0,
      y: 0,
      noResize: true,
      id: "submarine"
    })
    grid.addWidget('<div id="destroyer"><div class="grid-stack-item-content destroyerHorizontal"></div></div>', {
      width: 3,
      heigth: 1,
      x: 0,
      y: 0,
      noResize: true,
      id: "destroyer"
    })
    grid.addWidget('<div id="patrol"><div class="grid-stack-item-content patrolHorizontal"></div></div>', {
      width: 2,
      heigth: 1,
      x: 0,
      y: 0,
      noResize: true,
      id: "patrol"
    })


    $("#carrier,#battleship,#submarine,#destroyer,#patrol").click(function () {
      var idShip = $(this)[0].id;
      var widthShip = parseInt($(this)[0].dataset.gsWidth);
      var heigthShip = parseInt($(this)[0].dataset.gsHeight);
      var x = parseInt($(this)[0].dataset.gsX);
      var y = parseInt($(this)[0].dataset.gsY);
      var yShip = 10 - widthShip;
      var xShip = 10 - heigthShip;

      if ($(this).children().hasClass(idShip + "Horizontal") && y <= yShip && grid.isAreaEmpty(x, y + 1, heigthShip, widthShip - 1)) {
        grid.resize($(this), heigthShip, widthShip);
        $(this).children().removeClass(idShip + "Horizontal");
        $(this).children().addClass(idShip + "Vertical");
      } else if ($(this).children().hasClass(idShip + "Vertical") && x <= xShip && grid.isAreaEmpty(x + 1, y, heigthShip - 1, widthShip)) {
        grid.resize($(this), heigthShip, widthShip);
        $(this).children().addClass(idShip + "Horizontal");
        $(this).children().removeClass(idShip + "Vertical");
      } else {
        Swal.fire("Try another location");
      }
    });
  } else {

    options.staticGrid = true;

    dibujarShip(app.shipsCurrentPlayer);
    showAndHide("tableSalvoes", "block");
    showAndHide("titulo2", "block");
    showAndHide("buttonSalvo", "block");
    showAndHide("section-hits", "flex");
    showAndHide("buttonShips", "none");
    showAndHide("titulo1", "none");
    showAndHide("instructions", "none");


  }
};



//dibujo los ships que me vienen de la data
function dibujarShip(ships, letter) {

  grid = GridStack.init(options, '.grid-stack');

  var options = {
    //grilla de 10 x 10
    column: 10,
    minRow: 10,
    //separacion entre elementos (les llaman widgets)
    verticalMargin: 0,
    //altura de las celdas
    cellHeight: 50,
    //desabilitando el resize de los widgets
    disableResize: true,
    //widgets flotantes
    float: true,
    //removeTimeout: 100,
    //permite que el widget ocupe mas de una columna
    disableOneColumnMode: true,
    //false permite mover, true impide
    staticGrid: 0,
    maxRow: 10,
    //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
    animate: true
  }



  for (var i = 0; i < ships.length; i++) {

    var ship = ships[i];

    let xShip = parseInt(ship.locations[0].slice(1)) - 1;
    let yShip = parseInt(ship.locations[0].slice(0, 1).charCodeAt(0)) - 65;

    if (ship.locations[0][0] == ship.locations[1][0]) {

      widthShip = ship.locations.length;
      heigthShip = 1;



      grid.addWidget('<div id="' + ship.type + '"><div class="grid-stack-item-content' + " " + ship.type + 'Horizontal"></div></div>', {
        width: widthShip,
        heigth: heigthShip,
        x: xShip,
        y: yShip,
        noResize: true,
        id: ship.type
      })
    } else {
      widthShip = 1;
      heigthShip = ship.locations.length;

      grid.addWidget('<div id="' + ship.type + '"><div class="grid-stack-item-content' + " " + ship.type + 'Vertical"></div></div>', {
        width: widthShip,
        height: heigthShip,
        x: xShip,
        y: yShip,
        noResize: true,
        id: ship.type
      })


    }
  }

}

//GUARDAR LA DATA DE LOS SHIPS LOCALIZADOS:

$(".buttonShips").click(function () {

  $(".grid-stack-item").each(function () {
    var coordinate = [];
    var ship = {
      type: "",
      locations: ""
    };
    if ($(this).attr("data-gs-width") !== "1") {
      for (var i = 0; i < parseInt($(this).attr("data-gs-width")); i++) {
        coordinate.push(String.fromCharCode(parseInt($(this).attr("data-gs-y")) + 65) + (parseInt($(this).attr("data-gs-x")) + i + 1).toString());
      }
    } else {
      for (var i = 0; i < parseInt($(this).attr("data-gs-height")); i++) {
        coordinate.push(String.fromCharCode(parseInt($(this).attr("data-gs-y")) + i + 65) + (parseInt($(this).attr("data-gs-x")) + 1).toString());
      }
    }

    ship.type = $(this)[0].id;
    ship.locations = coordinate;
    app.shipLocalizado.push(ship);


  });
  postShips();


});


//-------------------------------------------SALVOES-----------------------------------------

//Funcion Post Salvoes


function postSalvoes(turn, salvoLocations) {

  if (app.salvo.salvoLocations.length == 5) {
    var newSalvo = {
      turn: turn,
      salvoLocations: salvoLocations
    };
    $.post({
      url: "/api/games/players/" + myParam + "/salvos",
      data: JSON.stringify(newSalvo),
      dataType: "text",
      contentType: "application/json"
    }).done(function () {
      location.reload();



    }).fail(function (jqXHR, error) {
      mensajeError = JSON.parse(jqXHR.responseText);
      Swal.fire(mensajeError.error);
    })
  } else {
    Swal.fire('Complete the five shoots');
  }
}

//Funcion para guardar los salvoLocations y verificar que no seleccione una celda que ya esta seleccionada 
function guardarSalvoLocations(id) {

  app.id = id;

  if (app.salvo.salvoLocations.length < 5) {


    if (app.salvo.salvoLocations.includes(id) == false) {

      app.salvo.salvoLocations.push(id);
      document.getElementById(id).classList.add("salvoesSelect");

      app.salvoesPlayer.forEach(sp => {

        sp.salvoLocations.forEach(eachSalvoLocation => {
          if (app.salvo.salvoLocations.includes(eachSalvoLocation)) {

            document.getElementById(id).classList.remove("salvoesSelect");
            quitarSalvoLocation(id)

            Swal.fire(
              'You already select this cell before',
              "Try another",
              'error'
            )

          }
        })
      })

    } else {
      document.getElementById(id).classList.remove("salvoesSelect");
      quitarSalvoLocation(id);
    }
  } else if (app.salvo.salvoLocations.length == 5) {
    document.getElementById(id).classList.remove("salvoesSelect");
    quitarSalvoLocation(id);
  }
};

//funcion para quitar un location por si el player se arrepiente de disparar en esa celda 

function quitarSalvoLocation(id) {
  var index = app.salvo.salvoLocations.indexOf(id);
  if (index > -1) {
    app.salvo.salvoLocations.splice(index, 1);
  }
}

//funcion para el click botton salvoes  
function buttonSalvoes() {
  postSalvoes(app.salvo.turn, app.salvo.salvoLocations);

}

function paintSalvoes() {
  if (app.game_view.gamePlayers.length > 1) {

    app.salvoesPlayer.forEach(sp => {
      sp.salvoLocations.forEach(eachSalvoLocation => {
        document.getElementById(eachSalvoLocation).classList.add("tdSalvoes");
      })
    })
    ordenMenoraMayor(app.hitsPlayer);
    ordenMenoraMayor(app.hitsOpponent);
    ordenMenoraMayor(app.sinkPlayer);
    ordenMenoraMayor(app.sinkOpponent);


  }
}

//funcion para mostrar y esconder 

function showAndHide(element, style) {
  document.getElementById(element).style.display = style;
}


//funcion para ordenar los Scores de mayor a menor 
function ordenMenoraMayor(el) {

  el.sort(function (a, b) { //ordenamos de mayor a menor
    return a.turn - b.turn
  })
  return el;
};

//Obtener currentPlayer y oponent

function obtenerPlayers(json) {

  var idGamePlayer = json.id;
  var gamePlayers = json.gamePlayers.filter(gamePlayer => gamePlayer.id == idGamePlayer);
  var gamePlayers2 = json.gamePlayers.filter(gamePlayer => gamePlayer.id != idGamePlayer);

  app.player = gamePlayers[0].player;
  if (gamePlayers2[0] != null) {
    app.oponent = gamePlayers2[0].player;
  }

}

function hitsOnOpponent() {
  app.hitsPlayer.forEach(hit => {
    hit.hits.forEach(eachHit => {
      document.getElementById(eachHit).classList.add("celdasTiroteadas")
    })
  });

}

function createHit(allHits) {


  for (i = 0; i < allHits.length; i++) {

    var hits = allHits.length == 1 ? allHits[0].hits : allHits[0].hits.concat(app.hitsOpponent[i].hits);


    for (j = 0; j < hits.length; j++) {

      var hit = document.createElement("div")
      document.getElementById("grid-ships").appendChild(hit);
      hit.classList.add("hit");

      var marginLeft = hits[j].length == 3 ? 40 * (parseInt(hits[j][2]) + 9) : 40 * (parseInt(hits[j][1]) - 1);

      var letra = hits[j][0];
      switch (letra) {
        case 'A':
          letra = 1;;
          break;
        case 'B':
          letra = 2;
          break;
        case 'C':
          letra = 3;
          break;
        case 'D':
          letra = 4;
          break;
        case 'E':
          letra = 5;
          break;
        case 'F':
          letra = 6;
          break;
        case 'G':
          letra = 7;
          break;
        case 'H':
          letra = 8;
          break;
        case 'I':
          letra = 9;
          break;
        case 'J':
          letra = 10;
          break;
        default:

      }
      var marginTop = 40 * (letra - 1);

      hit.style.marginLeft = marginLeft + "px";
      hit.style.marginTop = marginTop + "px";

    }
  }
}

function alertTurn() {
  if (app.stateGame == "FIRE") {
    if (app.salvoesPlayer.length == 0) {
      app.salvo.turn = app.salvoesPlayer.length + 1;
      Swal.fire({
        title: "Turn:" + app.firstTurn + ", you can do it!",
        text: "You can only fire 5 shoots",
        confirmButtonText: 'Cool'
      })
    } else {
      app.salvo.turn = app.salvoesPlayer.length + 1;

      Swal.fire({
        title: "Turn:" + app.salvo.turn + ", you can do it!",
        text: "You can only fire 5 shoots",
        confirmButtonText: 'Cool'
      })

    }
  }

}


function sunken() {

  let barcoDefault = {
    ships: []
  }

  app.shipsunken = app.salvoesPlayer.length == 0 ? barcoDefault : app.sinkPlayer.pop();
  var ultimoShipSunken = app.sinkPlayer.length == 0 ? app.shipsunken : app.sinkPlayer[app.sinkPlayer.length - 1];
  app.shipSunkenOponent = app.salvoesOponent.length == 0 ? barcoDefault : app.sinkOpponent.pop();
  var ultimoShipSunkenOponent = app.sinkOpponent == 0 ? app.shipSunkenOponent : app.sinkOpponent[app.sinkOpponent.length - 1];

  if (app.shipsunken.ships.length > 0) {

    if ((app.shipsunken.turn == 1 || app.shipsunken.ships.length != ultimoShipSunken.ships.length) && app.stateGame == "WAIT_OPPONENT_ATTACK") {

      document.getElementById("explosion").setAttribute("autoplay", "autoplay");
      aparecerYDesaparecer("#sunken");


    }
  }
  if (app.shipSunkenOponent.ships.length > 0) {
    if ((app.shipSunkenOponent.turn == 1 || app.shipSunkenOponent.ships.length != ultimoShipSunkenOponent.ships.length) && app.stateGame == "FIRE") {

      document.getElementById("explosion").setAttribute("autoplay", "autoplay");
      aparecerYDesaparecer("#alert");
      var p = document.getElementById("p-sunken");
      p.innerHTML = "Sorry ships have been sunk";

    }
  }
}




function aparecerYDesaparecer(div) {
  if (div == "#sunken") {
    $(document).ready(function () {
      setTimeout(function () {

        // Declaramos la capa mediante una clase para ocultarlo
        $(div).fadeIn(0);
        document.getElementById("fluid").style.filter = "blur(5px)";
      }, 0);
    });

    $(document).ready(function () {
      setTimeout(function () {
        // Declaramos la capa  mediante una clase para ocultarlo
        $(div).fadeOut(1500);
        // Transcurridos 5 segundos aparecera la capa midiv2
        document.getElementById("fluid").style.filter = "none";
      }, 7000);
    });
  } else {
    $(document).ready(function () {
      setTimeout(function () {

        // Declaramos la capa mediante una clase para ocultarlo
        $(div).fadeIn(0);
      }, 0);
    });

    $(document).ready(function () {
      setTimeout(function () {
        // Declaramos la capa  mediante una clase para ocultarlo
        $(div).fadeOut(1500);

      }, 7000);

    })
  }
}

function mensaje() {

  if (app.stateGame == "WAIT_OPPONENT_ATTACK") {
    console.log("actualizando");
    location.reload();
  } else if (app.stateGame == "WAIT_OPPONENT_SHIPS" || app.stateGame == "WAIT_OPPONENT") {
    location.reload();
  }

}

setTimeout(mensaje, 20000);



//
//    async function PrimerProceso(){
//            let valor = await crearJson();
//            return  valor;
//    }
//
//    async function llamarFunctionAsincrona(){
//        let resultado = await PrimerProceso();
//        return resultado * 2;
//    }
//    llamarFunctionAsincrona().then(val => {
//        console.log("el resultado final es " + val)
//    }, error => {
//        console.log(error);
//    })

