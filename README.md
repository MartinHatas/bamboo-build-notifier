[![Build Status](https://travis-ci.org/MartinHatas/bamboo-build-notifier.svg?branch=master)](https://travis-ci.org/MartinHatas/bamboo-build-notifier)

# Bamboo build notifier
Lightweight windows tray Bamboo build notifier.

    Showing status changes of your favorite builds at Bamboo

## Features
* Showing worst favorite build state as windows tray icon (green/yellow/red)
* Bubble notification on any favorite build change state or dis/connect from bamboo server

##### This utility is not working yet. Minimal functionality that have to be implemented for using is:
* Add winsw so you can run app as a service

##### Nice to have functionality
* Open browser after click on the bubble

## Configuration
* Configuration is read from file configuration.xml
* If configuration.xml is not found application create sample one before shutdown
* Password is converted from plaintext on first run and stored encrypted
* Polling interval is in seconds

#### Sample configuration

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Configuration>
        <Username>user</Username>
        <Password>DES:UQRsYb3pb1VRvbDNRAQVsg==</Password>
        <BambooUrl>http://bamboo.company.co.uk</BambooUrl>
        <PollingInterval>20</PollingInterval>
    </Configuration>
