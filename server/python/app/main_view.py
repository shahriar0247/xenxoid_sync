
from app import app
from flask import render_template, request
from app.common_functions import *
from app.server import get_status, start_server, get_temp, stop
import threading
import platform
import os

server_status = "disconnected"

def load_site(server_status, error):
    random_num = generate_random_num()
    ip = get_ip()
    all_files = get_all_files()
    return render_template("main/main.html", random_num=random_num, ip=ip, server_status=server_status, all_files=all_files, error=error)

    

@app.route("/")
def main():
    return load_site("Disconnected", "")


@app.route("/test")
def test():
    all_files_name = get_all_files_name()
    return all_files_name
@app.route("/stop_server")
def stop_server():
    stop()
@app.route("/server_status")
def server_status():
    return get_status()

@app.route("/checkpass", methods=["POST"])
def check_pass():
    if request.method == "POST":
        
        password = (request.form["pass"])
        custom_ip = request.form["custom_ip"]
        location = "desktop"
        if password != "ok" and password != "no":
            add_pass(password)
        set_share_folder(location)
        threading.Thread(target=start_server, args=[custom_ip]).start()
        info = ""
        while (info != "ok" and info != "no" and info != "server_error"):
            temp = get_temp()
            with open(temp, "r") as f:
                info = f.readline()

        random_num = generate_random_num()
        ip = get_ip()
        error = ""
        if info == "no":
            error = ("Wrong password. Please enter right password with same IP address")
            server_status = "disconnected"
            return load_site(server_status, error)
        elif info == "ok":
            server_status = "connected"
            return load_site(server_status, error)
        elif info == "server_error":
            server_status = "unable to be created"
            error = "Unable to start server. Please check your internet connection or click on \"Server IP Address empty\""
            return load_site(server_status, error)
        
    
    return main()



@app.route("/open_sharefolder", methods=["POST"])
def open_sharefolder():
    if (platform.system() == "Windows"):
        os.system("explorer " + get_sharefolder())
    elif (platform.system() == "Linux"):
        os.system("xdg-open " + get_sharefolder())
    random_num = generate_random_num()
    ip = get_ip()
    return load_site(server_status, "")



@app.route("/donate")
def donate():
    os.system("start https://www.paypal.me/AhmedShahriar0247")