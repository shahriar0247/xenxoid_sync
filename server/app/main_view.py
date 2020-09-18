from app import app
from flask import render_template, request
from app.common_functions import *
from app.server import start_server

@app.route("/")
def main():
    random_num = generate_random_num()
    ips = get_ip()
    server_status = "turned off"
    return render_template("main/main.html", random_num=random_num, ips=ips, server_status=server_status)


@app.route("/checkpass", methods=["POST"])
def check_pass():
    if request.method == "POST":
        password = (request.form["pass"])
        if password != "ok" and password != "no":
            add_pass(password)
        # start_server()
        info = ""
        while (info != "ok" and info != "no"):
            with open("temp", "r") as f:
                info = f.readline()
                print(info)
        print("done")
    return main()
