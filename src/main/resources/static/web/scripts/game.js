const queryString = window.location.search;
console.log(queryString);

const urlParams = new URLSearchParams(window.location.search);
const myParam = urlParams.get('gp');
console.log(myParam);

var grid = null;





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

      obtenerShips(json);
      obtenerSalvoes(json);
      crearGrid();


    });

}
crearJson();

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
    salvo: {
      turn: 0,
      salvoLocations: []
    },
    salvoesPlayer: [],
    salvoesOponent: [],
    idSalvoPlayer: 0,
    firstTurn: 1,
  },
  methods: {

    gamePlayerId: function () {

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

  }).fail(function (error) {
    console.log("Error");
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

      if ($(this).children().hasClass(idShip + "Horizontal") && y <= yShip && grid.isAreaEmpty(x, y + 1, heigthShip, widthShip)) {
        console.log(yShip)
        console.log("vertical")
        grid.resize($(this), heigthShip, widthShip);
        $(this).children().removeClass(idShip + "Horizontal");
        $(this).children().addClass(idShip + "Vertical");
      } else if ($(this).children().hasClass(idShip + "Vertical") && x <= xShip && grid.isAreaEmpty(x + 1, y, heigthShip, widthShip)) {
        console.log(xShip)
        console.log("horizontal")
        grid.resize($(this), heigthShip, widthShip);
        $(this).children().addClass(idShip + "Horizontal");
        $(this).children().removeClass(idShip + "Vertical");
      } else {
        alert("Try another location");
      }
    });
  } else {

    options.staticGrid = true;
    // var letters = cambiarLetraporNumero(app.shipsCurrentPlayer);
    dibujarShip(app.shipsCurrentPlayer);
    show("tableSalvoes");
    hide("buttonShips");
    hide("titulo1")
    show("titulo2")
    show("buttonSalvo");

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
};



//Obtengo los ships
function obtenerShips(json) {
  var ships = json.ships;
  for (var i = 1; i < ships.length; i++) {
    var shipLocations = ships[0].locations.concat(ships[i].locations);

  }
  return shipLocations;
}


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

      console.log(xShip);

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
    console.log(coordinate);

  });
  postShips();


});


//-------------------------------------------SALVOES-----------------------------------------

//Funcion Post Salvoes
var mensajeError = {};

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
      crearJson();

    }).fail(function (jqXHR, error) {
      mensajeError = jqXHR.responseText;
      alert(mensajeError);
    })
  } else {
    Swal.fire('Complete the five shoots');
  }
}

//Funcion para guardar los salvoLocations y verificar que no seleccione una celda que ya esta seleccionada 
function guardarSalvoLocations(id) {

  if (app.salvo.salvoLocations.length < 5) {


    if (app.salvo.salvoLocations.includes(id) == false ) {

      app.salvo.salvoLocations.push(id);
      document.getElementById(id).classList.add("salvoesSelect");

      app.salvoesPlayer.forEach(sp => {
        app.idSalvoPlayer = sp.player;
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

//obtengo y dibujos los salvoes

function obtenerSalvoes(json) {


  if (app.game_view.gamePlayers.length > 1) {

    paintSalvoes();


  } else if (app.game_view.gamePlayers.length == 1) {

    for (let k = 0; k < app.salvoesPlayer.length; k++) {
      var finalCells = app.salvoesPlayer[k].salvoLocations;
      finalCells.forEach(salvoLocation => {
        document.getElementById(salvoLocation).classList.add("tdSalvoes");
      })
    }
  }

}




/*
//dibujo los ships en la grilla
function dibujarLocations(posiciones) {
  for (i = 0; i < posiciones.length; i++) {
    document.getElementById(posiciones[i]).classList.add("tdColoreado");
  }
}
*/
function show(element) {
  document.getElementById(element).style.display = "block";
}

function hide(element) {
  document.getElementById(element).style.display = "none";
}

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

function paintSalvoes() {
  app.salvoesPlayer.forEach(sp => {
    sp.salvoLocations.forEach(eachSalvoLocation => {
      document.getElementById(eachSalvoLocation).classList.add("tdSalvoes");
    })
  })
}
