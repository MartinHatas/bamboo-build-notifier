[![Build Status](https://travis-ci.org/MartinHatas/bamboo-build-notifier.svg?branch=master)](https://travis-ci.org/MartinHatas/bamboo-build-notifier)

# Bamboo build notifier
Lightweight windows tray Bamboo build notifier.

    Showing status changes of your favorite builds at Bamboo

## Features
* Showing worst favorite build state as windows tray icon (green/yellow/red)
* Bubble notification on any favorite build change state or dis/connect from bamboo server

## Get it
There are two options - build it yourself from source code or download compiled binaries
### Build it on your own
Check in project from github and simply build it by maven

    mvn clean install

Then you found everything you need in 'target/build' folder.

### Download compiled version
TODO: add link

## Configuration
* Configuration is read from file configuration.xml
* If configuration.xml is not found application create sample one before shutdown
* Password is converted from plaintext on first run and stored encrypted
* Polling interval is in seconds
* Adjust configuration by your environment

### Sample configuration

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Configuration>
        <Username>user</Username>
        <Password>DES:UQRsYb3pb1VRvbDNRAQVsg==</Password>
        <BambooUrl>http://bamboo.company.co.uk</BambooUrl>
        <PollingInterval>20</PollingInterval>
    </Configuration>

## Run it
You can run it standalone application or as a windows service

### Standalone run
Just run

    bamboo-build-notifier.bat

### Windows service
TODO: add how to run as service


