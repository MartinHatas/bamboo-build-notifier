[![Build Status](https://travis-ci.org/MartinHatas/bamboo-build-notifier.svg?branch=master)](https://travis-ci.org/MartinHatas/bamboo-build-notifier)

# Bamboo build notifier
Lightweight windows tray Bamboo build notifier.

    Showing status changes of your favourite builds at Bamboo

##### This utility is not working yet. Minimal functionality that have to be implemented for using is:
* Reflect fetched status of build on tray icon
* Tray "bubble" notifications
* Generate configuration stub on first run if configuration file does not exists
* Fix logging into file
* Add winsw so you can run app as a service
* Simplify - only one Bamboo server could be queried per instance

##### Nice to have functionality
* Configurable build checking interval
* Open browser after click on the bubble

## Sample configuration
1) Configuration is self describing

2) Password is converted from plaintext on first run and stored encrypted


    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Configuration>
        <Username>user</Username>
        <Password>DES:UQRsYb3pb1VRvbDNRAQVsg==</Password>
        <BambooServers>
            <BambooUrl>http://bamboo.company.co.uk</BambooUrl>
        </BambooServers>
    </Configuration>
