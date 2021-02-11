import random
import subprocess
import os
import tempfile
import math
import time
import struct
import os

import platform
import pathlib


def set_share_folder(location):
    current_loc = pathlib.Path(__file__).parent.absolute()
    share_loc = os.path.join(current_loc, "share_loc")
    if (location == "desktop"):
        if (platform.system()) == "Windows":
            location = os.path.join(os.path.join(os.path.join(os.environ['USERPROFILE']), 'Desktop'), "share folder")
        elif (platform.system()) == "Linux":
            location = os.path.join(os.path.join(os.path.join(os.path.expanduser('~')), 'Desktop'), "share folder")
         
    else:
        pass
    
    with open(share_loc, "w") as f:
        f.write(location)

    try:
        os.mkdir(location)
    except:
        pass
    
    return location


    
def get_sharefolder():
    current_loc = pathlib.Path(__file__).parent.absolute()
    share_loc = os.path.join(current_loc, "share_loc")
    with open(share_loc, "r") as f:
        loc = f.readline()
    return loc

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
    for ip in ip_list:
        if ip.startswith("192.168.0."):
            return ip
        elif ip.startswith("192.168.1."):
            return ip
        elif ip.startswith("3.1.5."):
            return ip
    return ip_list

def get_temp():
    return os.path.join(tempfile.gettempdir(), "xenxoid_sync_temp")

def pass_check(password):
    return get_pass() == password

def get_pass():
    temp = get_temp()
    with open(temp, "r") as t:
        return (t.readline())

def add_pass(password):
    temp = get_temp()
    with open(temp, "w") as t:
        t.write(password)

def wrong_pass():
    pass

def get_all_files():
    print(os.walk(get_sharefolder()))
