from app import app
from flask import render_template, request
from app.common_functions import *
from app.server import set_share_folder, start_server, get_sharefolder
import threading
import platform
import os

@app.route("/")
def main():
    random_num = generate_random_num()
    ips = get_ip()
    server_status = "disconnected"
    return render_template("main/main.html", random_num=random_num, ips=ips, server_status=server_status)


@app.route("/checkpass", methods=["POST"])
def check_pass():
    if request.method == "POST":
        
        password = (request.form["pass"])
        location = "desktop"
        if password != "ok" and password != "no":
            add_pass(password)
        set_share_folder(location)
        threading.Thread(target=start_server).start()
        info = ""
        while (info != "ok" and info != "no"):
            with open("temp", "r") as f:
                info = f.readline()
        if info == "no":
            error = ("Wrong password. Please enter right password with same ip")
            random_num = generate_random_num()
            ips = get_ip()
            server_status = "disconnected"
            return render_template("main/main.html", random_num=random_num, ips=ips, server_status=server_status, error=error)
        elif info == "ok":
            random_num = generate_random_num()
            ips = get_ip()
            server_status = "connected"
            return render_template("main/main.html", random_num=random_num, ips=ips, server_status=server_status)
    return main()



@app.route("/open_sharefolder", methods=["POST"])
def open_sharefolder():
    if (platform.system() == "Windows"):
        os.system("explorer " + get_sharefolder())
    elif (platform.system() == "Linux"):
        os.system("xdg-open " + get_sharefolder())
    random_num = generate_random_num()
    ips = get_ip()
    server_status = "connected"
    return render_template("main/main.html", random_num=random_num, ips=ips, server_status=server_status)