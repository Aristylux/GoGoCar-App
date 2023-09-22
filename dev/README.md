# Dev

This directory contains all test projects.

# Table of Content

- [Dev](#dev)
- [Table of Content](#table-of-content)
- [QR Code generator](#qr-code-generator)
- [HASH generator](#hash-generator)
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
  - [Communication](#communication)


# QR Code generator

This is a program for generate QR Code,

The QR codes generated can be printed and then stuck on the boxes, the user will only need to scan the QR code with the application to add their vehicle.

This part was developed by [Hamid](https://github.com/hamidajouaou).

[Available here](./QRcode_generator)

# HASH generator

This program allows you to hash passwords and others, it is a utility for testing the HASH in the application.

[Available here](./HASH_generator)

# Tests check

## Authentification (Login)

| From           | Action            | Result              |       Pass?        |
| :------------- | :---------------- | :------------------ | :----------------: |
| ***Login***    | login             | Move to ***Home***  | :heavy_check_mark: |
| ***Login***    | register          | Move to ***Home***  | :heavy_check_mark: |
| ***Settings*** | Log out           | Move to ***Login*** | :heavy_check_mark: |
| ***Settings*** | Delete my account | Move to ***Login*** | :heavy_check_mark: |

## Home

| From       | Action         | Result                 |       Pass?        |
| :--------- | :------------- | :--------------------- | :----------------: |
| ***Home*** | go to drive    | Move to ***Drive***    | :heavy_check_mark: |
| ***Home*** | go to Vehicles | Move to ***Vehicles*** | :heavy_check_mark: |
| ***Home*** | Cancel journey | Remove vehicle         | :heavy_check_mark: |

## Drive

| From        | Action             | Result             |       Pass?        |
| :---------- | :----------------- | :----------------- | :----------------: |
| ***Drive*** | Click on a vehicle | Open popup booking | :heavy_check_mark: |


## Vehicles

| From                | Action          | Result                     |       Pass?        |
| :------------------ | :-------------- | :------------------------- | :----------------: |
| ***Vehicles***      | Click on Edit   | Move to ***Vehicle Edit*** | :heavy_check_mark: |
| ***Vehicles***      | Click on Add    | Move to ***Vehicle Add***  | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Exit   | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Return | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Edit*** | Click on Save   | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Add***  | Click on Return | Move to ***Vehicles***     | :heavy_check_mark: |
| ***Vehicles Add***  | Click on Add    | Move to ***Vehicles***     | :heavy_check_mark: |

# Dev apps

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

## App 1

* List bluetooth devices
* Connect to bluetooth
* Displays the data received by the smartphone

[Description App1](./App1)

[Code Java App1](./App1/app/src/main/java/com/example/app1/)

## App 2

* List bluetooth devices
* Connect to bluetooth
* Send data with a button.

[Code Java App2](./App2/app/src/main/java/com/example/app2/)

## App 3

* List bluetooth devices
* Connect to bluetooth
* Displays the data received by the smartphone
* Send data with a button **in user box**.

App3 ~= App2 + App1

[Code Java App3](./App3/app/src/main/java/com/example/app3/)

## App 4

* Scan bluetooth devices in search

[Code Java App4](./App4/app/src/main/java/com/aristy/app4/)

## App 5

* Scan bluetooth devices in search
* Connect to the selected device

[Code Java App5](./App5/app/src/main/java/com/aristy/app5)

## App 6

* Try to implement Slidr lib for fragment
* ***Doesn't work***

## App 7

* Try to implement viewPager2
* ***Doesn't work***

## App 8

* Try to implement Slidr lib for activity
* (work)


## Communication

Detail the RSA and AES encryption

Used for bluetooth communication

[Available here](./Com)