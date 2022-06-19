import socket
import os
import sys

host = '192.168.25.35' # 호스트 ip를 적어주세요
port = 9999            # 포트번호를 임의로 설정해주세요

server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_sock.bind((host, port))
server_sock.listen(5)
filename = "i.png"

print("기다리는 중")
client_sock, addr = server_sock.accept()
print('Connected by', addr)
# 연결 끝

f = open(filename,'rb')# open file as binary
data = f.read()
print(data, ',,,')
exx = client_sock.sendall(data)
print(exx, ',,,')
f.flush()
f.close()
client_sock.close()
server_sock.close()