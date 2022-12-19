This tutorial is for

using postgresql in oracle cloud server.


Connect to our public server:

```bash
client@user:~$ ssh -i <private-key> <username>@<public_ip_address>
```



# Orale

ssh -i ssh-key-2022-12-17-oracle.key ubuntu@129.151.225.59


sudo lsof -P -i -n
COMMAND    PID            USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
...
docker-pr 1965            root    4u  IPv4  27136      0t0  TCP *:5432 (LISTEN)
docker-pr 1971            root    4u  IPv6  27143      0t0  TCP *:5432 (LISTEN)
