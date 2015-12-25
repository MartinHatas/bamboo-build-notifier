# Bamboo build notifier
Lightweight windows tray Bamboo build notifier.

    Showing status changes of your favourite builds at Bamboo

This utility is not working yet. Minimal functionality that have to be implemented for using is:
* Bamboo HTTP client
* Tray "bubble" notifications

## Sample configuration
1) Configuration is self describing
2) Password is converted from plaintext on first run and stored encrypted


    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Configuration>
        <Username>user</Username>
        <Password>DES:UQRsYb3pb1VRvbDNRAQVsg==</Password>
        <BambooServers>
            <BambooUrl>http://bamboo.company.co.uk</BambooUrl>
            <MonitoredBuilds>
                <BuildId>MyProject</BuildId>
            </MonitoredBuilds>
        </BambooServers>
    </Configuration>
