# server.py : 연결해 1 보낼 수 있음.
import socket

host = '192.168.0.6'  # 호스트 ip를 적어주세요
port = 5555            # 포트번호를 임의로 설정해주세요

server_sock = socket.socket(socket.AF_INET)
server_sock.bind((host, port))
server_sock.listen(1)

print("기다리는 중")
client_sock, addr = server_sock.accept()

if(client_sock):
    print("연결됨")
    print('Connected by', addr)

# 서버에서 "안드로이드에서 서버로 연결요청" 한번 받음
data = client_sock.recv(1024)
print(data.decode("utf-8"), len(data))

while (True):
    #보낼때
    data2 = int(input("보낼 값 : "))
    # 값하나 보냄(사용자가 입력한 숫자)
    client_sock.send(data2.to_bytes(4, byteorder='little'))
    # 안드로이드에서 값 받으면 "하나받았습니다 : 숫자" 보낼 것 받음
    data = client_sock.recv(1024)
    print(data.decode("utf-8"), len(data))
    #받을때
    # print("받는 값 : ", end="")
    # print(data.decode("utf-8"))

    if (data2 == 99):
        break

# 연결끊겠다는 표시 보냄
# i=99
# client_sock.send(i.to_bytes(4, byteorder='little'))
client_sock.close()
server_sock.close()