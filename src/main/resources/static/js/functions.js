var maze;
var oldkeeper;
var gamestatus;
var timer;

// Hit the /maze endpoint to retrieve the status of the maze
function initialize() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        maze = JSON.parse(this.responseText);
        document.getElementById("theMaze").innerHTML = mazeToHtml();
        getStatus();
        }
  }
  xhttp.open("GET", "/api/maze/", true);
  xhttp.send();
}

// Hit the /status endpoint to retrieve the status of the maze
function getStatus() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        gamestatus = JSON.parse(this.responseText);
        react();
    }
  }
  xhttp.open("GET", "/api/status/", true);
  xhttp.send();
}

// updates the HTML to reflect the new status
function react() {
  document.getElementById("mazeHeight").innerHTML = maze.height;
  document.getElementById("mazeWidth").innerHTML = maze.width;
  document.getElementById("numberOfKeys").innerHTML = gamestatus.keys.length;
  document.getElementById("keysFound").innerHTML = gamestatus.found.length;
  document.getElementById("positionH").innerHTML = gamestatus.keeper.horizontal;
  document.getElementById("positionV").innerHTML = gamestatus.keeper.vertical;
  document.getElementById("h"+gamestatus.keeper.horizontal+"v"+gamestatus.keeper.vertical).className = "mazebrick keeper";
  if(oldkeeper) document.getElementById("h"+oldkeeper.horizontal+"v"+oldkeeper.vertical).className = "mazebrick path";
  oldkeeper=gamestatus.keeper;
  document.getElementById("h"+gamestatus.door.horizontal+"v"+gamestatus.door.vertical).className = "mazebrick door";
  if(gamestatus.complete==true) playerWins();
}

// Hit the /act endpoint to make the keeper act
function robotAct() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() { if (this.readyState == 4 && this.status == 200) getStatus(); };
  xhttp.open("GET", "/api/act/", true);
  xhttp.send();
}

// Hit the /reset endpoint to start afresh
function newWorld() {
  stopRobot();
  document.getElementById("win").innerHTML = "";
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() { if (this.readyState == 4 && this.status == 200) initialize(); };
  xhttp.open("GET", "/api/reset/", true);
  xhttp.send();
}

// starts the timer to act every x ms
function startRobot() {
    timer = setInterval(robotAct , 50);
}

// clears the timer
function stopRobot() {
    clearInterval(timer);
}

// maze has been solved
function playerWins() {
  stopRobot();
  document.getElementById("h"+gamestatus.door.horizontal+"v"+gamestatus.door.vertical).className = "mazebrick won";
  document.getElementById("win").innerHTML = "You escaped!";
}

// displays the maze in HTML format
function mazeToHtml() {
    html = "";
    for(var v = 0; v < maze.cells.length; v++) {
        html+="<div class=\"mazerow\">";
        var row = maze.cells[v];
        for(var h = 0; h < row.length; h++) html+="<div id=\"h"+h+"v"+v+"\" class=\"mazebrick "+row[h]+"\"></div>";
        html+="</div>";
    }
    return html;
}