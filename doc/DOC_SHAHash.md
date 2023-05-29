

This document gathers the actions to set up the HASH generator for the modules "mi carro es tu carro".

# Frist time

To use the software you must have java installed on your machine.

## Installation

```bash
~$ sudo dnf install java-11-openjdk
```

## Execution

```bash
~$ java SHAHash
```

## Exemple of use

### Hash simple

```bash
~$ java SHAHash "hes"
Hashed text: 19dd4469b2fc7b78eca66ab48dcfb52654842866c6587d5e01a67845e41c53466ce5851ecd809c52b906976530a8637ed857dc301c8b24a221cbec0f8155a16c , len 128
~$
```

### Hash with domain

```bash
~$ java SHAHash "hes" "f"
Hashed text: 4d385ee3b33c37ba8e317f071d432a19ce53cdad9679c3059266888b96bb891503236acc65e09736f1f3cee6222b75b4dcd0b852b0312d0e8a83465324b78f27 , len 128
~$
```

### Exemple with MAC address

```bash
~$ java SHAHash "4A:E5:FF:A7:25:9F"
Hashed text: 588d80d54a9c0af7cc5f034e013d17732221a5d583bc5812e21b62d7299c98660080c9910bf62385d5e89d486a2622157db777078576f94f5a17d0098e1a4968, len 128
~$
```


# Install from source

To run the program from the .java file, you must have the development version.

## Installation

```bash
~$ sudo dnf install java-11-openjdk-devel
```

## Execution

```bash
~$ javac SHAHash.java 

~$ java SHAHash 
Need an argument. abord. 
Try 'cat --help' for more information.
```