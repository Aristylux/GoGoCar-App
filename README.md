# GoGoCar-App

GoGoCar-App is the repository for the Android App called GoGoCar.

This repository is a part of `mi carro es tu carro` project.

All project repositories:

* [GoGoCar-App](https://github.com/Aristylux/GoGoCar-App) (This one)
* [GoGoCar-STM32](https://github.com/Aristylux/GoGoCar-STM32)

# The project

`Mi carro es tu carro` project is a master's project developed by 20 engineering students from ISEN in the 2nd year of the engineering cycle.


The startup "Go Go Car" wants to set up a personal vehicle loan service between individuals.
The loan service will be based on a smartphone application that will allow users to make their vehicles available and to book the vehicles of other users. Each vehicle will be equipped with a box to control the start of the vehicle and track driver behavior in real time and record the number of kilometers traveled, engine RPM and speed for example.

**Note:** This is not a B to C solution (Business-to-Consumer), nor a Carpooling solution.

### **Full requirements can be viewed [here](./doc/REQ-GGC-EMB-M1-2022-23-001.pdf).**

## Teams

We were divided into 6 different groups, called Work Package (WP):

* WP1: Data collection
* WP2: Car immobilizer systeme
* WP3: Tracker
* WP4: User experience
* WP5: Wireless communications
* WP6: Project Managment

# The Work Package 4

Our group was composed of 4 people with different tasks:
* QR code generator
* Dataset
* App & database
* Box

I developed the App and the database structure.

This repository includes:

* The Android app, GoGoCar available [here](./GoGoCar/),
* The app of the QR code generator [here](./dev/QRcode_generator/),
* The app of development [here](./dev/),
* The documentation [here](./doc/).

The design (HTML and CSS) is available [here](./GoGoCar/app/src/main/assets/).


# Security notes

The following points are the reasons why the application is not secure and has serious security vulnerabilities.

### Rest API:

I didn't have enough time to develop a REST API for this application.

Requests are made directly in the application instead of using a REST API. That is to say that SQL queries are written in variables.

This is a big security flaw, because a malicious individual can recover the application executable (.apk) and decompile, then decode and reconstruct a query directly on the database.


### Structure of the app:

To avoid using XML, I use `WebView` with html, css and js for the visual of the app.

The Problem is that to communicate between JAVA and WebView, I use Javascript.

It is also a big security problem since we can inject javascript.