This tutorial is for

using postgresql in oracle cloud server.


Connect to our public server:

```bash
client@user:~$ ssh -i <private-key> <username>@<public_ip_address>
```



# Orale

Connect to the server:

```bash
ssh -i ssh-key-2022-12-17-oracle.key ubuntu@129.151.251.242
```

Listing ports:

```
sudo lsof -P -i -n
COMMAND    PID            USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
...
docker-pr 1965            root    4u  IPv4  27136      0t0  TCP *:5432 (LISTEN)
docker-pr 1971            root    4u  IPv6  27143      0t0  TCP *:5432 (LISTEN)
```

# Restart Instance

## Verify running container

```bash
ubuntu@ubuntu:~$ sudo docker images
REPOSITORY   TAG       IMAGE ID       CREATED        SIZE
postgres     latest    4c6b3cc10e6b   4 months ago   379MB
ubuntu@ubuntu:~$ sudo docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
ubuntu@ubuntu:~$ 
```

## Restart container

```bash
ubuntu@ubuntu:~$ sudo docker restart postgres-0
postgres-0
```

**Note:** the database is now available.