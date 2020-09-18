var all_ip = document.getElementsByClassName("all_ip")[0]
all_ip.style.display = "none"
show_all_ip = false

function toggle_all_ip(){
    if (show_all_ip == false){
        show_all_ip = true;
        all_ip.style.display = "block"
    }

    else if (show_all_ip == true){
        show_all_ip = false;
        all_ip.style.display = "none"
    }
}