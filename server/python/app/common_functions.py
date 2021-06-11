import random
import socket
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
        location = os.path.join(os.path.expanduser("~/Desktop"), "share folder")
    else:
        pass
    

    with open(share_loc, "w") as f:
        f.write(location)

    if not os.path.exists(location):
        os.mkdir(location)    
    return location


    
def get_sharefolder():
    current_loc = pathlib.Path(__file__).parent.absolute()
    share_loc = os.path.join(current_loc, "share_loc")
    try:
        with open(share_loc, "r") as f:
            loc = f.readline()
    except FileNotFoundError:
        set_share_folder("desktop")
        loc = get_sharefolder()
    return loc

def shell(cmd):
    output = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=subprocess.PIPE, shell=True)
    return output.stdout.read().decode("utf-8")

def generate_random_num():
    return random.randint(1, 2000000000)

def get_ip():
    return socket.gethostbyname(socket.gethostname())

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


def get_size(start_path = '.'):
    total_size = 0
    for dirpath, dirnames, filenames in os.walk(start_path):
        for f in filenames:
            fp = os.path.join(dirpath, f)
            # skip if it is symbolic link
            if not os.path.islink(fp):
                total_size += os.path.getsize(fp)

    return total_size

def listall(list_path):
    folder = []
    for name in os.listdir(list_path):
        folder.append(os.path.join(list_path,name))
    return folder

def get_name(folder):
    folder = folder.replace("\\", "/")
    folder = folder.split("/")[-1]
    return folder

class item:
    def __init__(self, name, size, location):
        self.name = name
        self.location = location
        size = size/1024
        
        if size > 1024*1024:
            size = str(size / 1024 /1024) + ".GB"
        elif size > 1024:
                size = str(size / 1024) + ".MB" 
        else:
            size = str(size) + ".KB"

        size = size.split(".")
        size = size[0] +"."+ (size[1])[0:2] + " " + size[2]
        self.size = size

def get_all_files():
    share_folder = get_sharefolder()
    all_folders = listall(share_folder)
    items = []
    for folder in all_folders:
        items.append(item(get_name(folder), get_size(folder),folder))
    return items

def get_all_files_name():
    share_folder = get_sharefolder()
    all_folders = listall(share_folder)
    all_folder = ""
    for folder in all_folders:
        all_folder = all_folder + get_name(folder)+ ", "
    all_folder = all_folder
    return all_folder
