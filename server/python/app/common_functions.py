import random
import subprocess

def shell(cmd):
    output = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=subprocess.PIPE, shell=True)
    return output.stdout.read().decode("utf-8")

def generate_random_num():
    return random.randint(1, 2000000000)

def get_ip():
    output = shell("ipconfig")
    output = output.split("\n")
    ip_list = []
    for line in output:
        if "IPv4" in line:
            line = line.split(":")[1]
            line = line.replace(" ","").replace("\r","")
            ip_list.append(line)
    return ip_list

def pass_check(password):
    with open("temp", "r") as t:
        return (t.readline() == password)

def add_pass(password):
    with open("temp", "w") as t:
        t.write(password)

def wrong_pass():
    pass
