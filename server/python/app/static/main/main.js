var all_ip = document.getElementsByClassName("all_ip")[0]
all_ip.style.display = "none"
show_all_ip = false

function toggle_all_ip() {
    if (show_all_ip == false) {
        show_all_ip = true;
        all_ip.style.display = "block"
    }

    else if (show_all_ip == true) {
        show_all_ip = false;
        all_ip.style.display = "none"
    }
}

function help() {
  
        document.getElementsByClassName("a4")[0].style.display = "block"
        document.getElementsByClassName("a5")[0].style.display = "block"
    
}

function okay(){
    document.getElementsByClassName("a4")[0].style.display = "none"
    document.getElementsByClassName("a5")[0].style.display = "none"
}

function donate(){
    var xhr = new XMLHttpRequest();
xhr.open("GET", "/donate", true);
xhr.send();
}

function server_started(){
    document.getElementById("server_stat").textContent = "Server Starting..."
    setTimeout(function() {
        document.getElementById("server_stat").textContent = "Server Started!"
}, 1000);
}