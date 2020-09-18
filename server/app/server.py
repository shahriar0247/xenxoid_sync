import socket
import math
import time
import struct
import os
from app.common_functions import pass_check, wrong_pass

def create_socket(ip, port):
    sock = socket.socket()
    sock.bind((ip,port))
    sock.listen(5)
    return sock

def connect(sock):
    s, address = sock.accept()
    return s, address

def send(s, text):
    s.send(bytes(text + "\n", "utf-8"))

def recv(s):
    buffer = s.recv(32).decode("utf-8").replace("buffer: ", "")
    new_buffer = 1025
    buffer = int(buffer) + 1
    send(s, "ok")
    times_to_recieve = buffer / new_buffer

    output = ""
   
    while len(output) != buffer:
        
        output = output + s.recv(buffer).decode("utf-8")
    return output

def cd(cmd, output, client):
    currentpath = output.replace("currentpath:", "")
    currentpath = currentpath.split("/")

    newpath = ""
    if cmd.replace("cd ", "") == "..":
        currentpath = currentpath[:-1]
        
        for a in currentpath:
            if a != "":
                newpath = newpath + "/" + a
        newpath = newpath.replace("/ ","")
    
    else:
        currentpath[-1] = currentpath[-1][:-1]
        
        for a in currentpath:
            if a != "":
                newpath = newpath + "/" + a
        newpath = newpath +"/"+cmd.replace("cd ","")
        newpath = newpath.replace("/ ","")
    return newpath


def download2(all_files, client):
    filetype = recv(client)
    send(client, "ok")
    if filetype == "file\n":
        downloadfile(all_files, client)
        send(client, "done")
        recv(client)

    elif filetype == "dir\n":
        folder_name = recv(client)[:-1]
        try:
            os.mkdir(folder_name)
        except:
            pass
   
        send(client, "ok")
        number_of_files = recv(client)

        number_of_files = int(number_of_files.replace("number_of_files: ",""))

        for a in range(0, number_of_files):
            download2(all_files, client)
        

def downloadfile(all_files, client):
    filename = recv(client)
    filename = filename.replace("downloading ","")[:-1]
    if ("./"+filename in all_files):
        send(client, "dont")
        print("dont")
        recv(client)
    else:
        send(client,"send")
        recv(client)
        buf = bytes()
        while len(buf) < 4:
            buf += client.recv(4 - len(buf))
        size = struct.unpack('!i', buf)[0]

        print("[*] Getting " + filename)

        if "/" in filename:
            temp_filename_list = filename.split("/")
            temp_filename_list = temp_filename_list[:-1]
    
            temp_current_loc = ""
            for temp_filename in temp_filename_list:
               
                if (temp_current_loc == ""):
                    temp_current_loc = temp_filename
                else:
                    temp_current_loc = temp_current_loc + "/" + temp_filename
                try:
                    os.mkdir(temp_current_loc)
                except:
                    pass
        with open(filename, 'wb') as f:
            while size > 0:
                data = client.recv(1024)
                f.write(data)
                size -= len(data)
      
def listall(list_path):
    all_folders = []
    all_files = []
    for root, folders, files in os.walk(list_path):

        for eachfold in folders:
            all_folders.append(root.replace("\\", "/") +
                               "/"+eachfold.replace("\\", "/"))

        for eachfile in files:
            all_files.append(root.replace("\\", "/") +
                               "/"+eachfile.replace("\\", "/"))
        

    return all_files

def start_server():
    try:

        sock = create_socket("3.1.5.104",4422)
        client, address = connect(sock)

        password = (recv(client))
        if pass_check(password) == True:
            with open("temp", "w") as f:
                f.write("ok")
            send(client,"hey sup")

            try:
                os.mkdir("share folder")
            except:
                pass
            os.chdir("share folder")
            
            while True:
                all_files = listall(".")
            
                download2(all_files, client)
        else:
            with open("temp", "w") as f:
                f.write("no")
            

    except Exception as e:
        print(e)
        exit(1)

if __name__ == "__main__":
    start_server()