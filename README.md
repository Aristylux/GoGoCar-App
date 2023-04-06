# GoGoCar-App

Android Application for GoGoCar

[Description GoGoCar](./GoGoCar)

[HTML Code](./GoGoCar/app/src/main/assets/)

# Table of Content

- [GoGoCar-App](#gogocar-app)
- [Table of Content](#table-of-content)
- [Documentation](#documentation)
  - [SQL](#sql)
  - [Local](#local)
- [Dev](#dev)
  - [Tests check](#tests-check)
    - [Authentification (Login)](#authentification-login)
    - [Home](#home)
    - [Drive](#drive)
    - [Vehicles](#vehicles)
  - [Dev apps](#dev-apps)
    - [App 1](#app-1)
    - [App 2](#app-2)
    - [App 3](#app-3)
    - [App 4](#app-4)
    - [App 5](#app-5)
    - [App 6](#app-6)
    - [App 7](#app-7)
    - [App 8](#app-8)


# Documentation

## SQL

[JDBC Postgres](https://jdbc.postgresql.org/documentation)

## Local

[Documentation Oracle Cloud Tutorial](./doc/DOC_Oracle_Cloud.md)

[Documentation SQL Queries](./doc/DOC_SQL.md)

[Documentation Docker](./doc/DOC_Docker.md)

[Documentation Archive code](./doc/DOC_Code_Archive.md)


# Dev

## Tests check

### Authentification (Login)

| From           | Action            | Result              |       Pass?        |
| :------------- | :---------------- | :------------------ | :----------------: |
| ***Login***    | login             | Move to ***Home***  | :heavy_check_mark: |
| ***Login***    | register          | Move to ***Home***  | :heavy_check_mark: |
| ***Settings*** | Log out           | Move to ***Login*** | :heavy_check_mark: |
| ***Settings*** | Delete my account | Move to ***Login*** | :heavy_check_mark: |

### Home

| From       | Action         | Result                 |       Pass?        |
| :--------- | :------------- | :--------------------- | :----------------: |
| ***Home*** | go to drive    | Move to ***Drive***    | :heavy_check_mark: |
| ***Home*** | go to Vehicles | Move to ***Vehicles*** | :heavy_check_mark: |
| ***Home*** | Cancel journey | Remove vehicle         | :heavy_check_mark: |

### Drive

| From        | Action             | Result             |       Pass?        |
| :---------- | :----------------- | :----------------- | :----------------: |
| ***Drive*** | Click on a vehicle | Open popup booking | :heavy_check_mark: |


### Vehicles

| From                | Action          | Result                     |       Pass?        |
| :------------------ | :-------------- | :------------------------- | :----------------: |
| ***Vehicles***      | Click on Edit   | Move to ***Vehicle Edit*** | :heavy_check_mark: |
| ***Vehicles***      | Click on Add    | Move to ***Vehicle Add***  | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Exit   | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Return | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Save   | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Add***  | Click on Return | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Add***  | Click on Add    | Move to ***Vehicles***     | :heavy_check_mark: |

## Dev apps

App 1, 2, 3, 4 and 5 are app test for bluetooth 

| Name |   Spec    |        List        |        Scan        |     Connection     |        Send        |      Recieve       |
| :--- | :-------: | :----------------: | :----------------: | :----------------: | :----------------: | :----------------: |
| App1 | Bluetooth | :heavy_check_mark: |        :x:         | :heavy_check_mark: |        :x:         | :heavy_check_mark: |
| App2 | Bluetooth | :heavy_check_mark: |        :x:         | :heavy_check_mark: | :heavy_check_mark: |        :x:         |
| App3 | Bluetooth | :heavy_check_mark: |        :x:         | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
| App4 | Bluetooth |        :x:         | :heavy_check_mark: |        :x:         |        :x:         |        :x:         |
| App5 | Bluetooth |        :x:         | :heavy_check_mark: | :heavy_check_mark: |        :x:         |        :x:         |

App 6, 7 and 8 are app test for structure app

| Name |     Spec      |
| :--- | :-----------: |
| App6 |    Slider     |
| App7 | Home Fragment |
| App8 |    Slider     |

### App 1

* List bluetooth devices
* Connect to bluetooth
* Displays the data received by the smartphone

[Description App1](./dev/App1)

[Code Java App1](./dev/App1/app/src/main/java/com/example/app1/)

### App 2

* List bluetooth devices
* Connect to bluetooth
* Send data with a button.

[Code Java App2](./dev/App2/app/src/main/java/com/example/app2/)

### App 3

* List bluetooth devices
* Connect to bluetooth
* Displays the data received by the smartphone
* Send data with a button **in user box**.

App3 ~= App2 + App1

[Code Java App3](./dev/App3/app/src/main/java/com/example/app3/)

### App 4

* Scan bluetooth devices in search

[Code Java App4](./dev/App4/app/src/main/java/com/aristy/app4/)

### App 5

* Scan bluetooth devices in search
* Connect to the selected device

[Code Java App5](./dev/App5/app/src/main/java/com/aristy/app5)

### App 6

* Try to implement Slidr lib for fragment
* ***Doesn't work***

### App 7

* Try to implement viewPager2
* ***Doesn't work***

### App 8

* Try to implement Slidr lib for activity
* (work)