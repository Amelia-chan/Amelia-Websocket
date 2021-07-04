# Amelia-Websocket
The websocket client for Amelia which handles the processing and checking from the ScribbleHub server.

### Setting up Amelia Websocket.
Amelia Websocket, which is a new addition implemented on Amelia 2.0, is a separate process of Amelia which can be said as the heart or the brain
of the Discord bot. The websocket handles the checking for updates on RSS Feeds and also Trending Notifications.

### Installation of Amelia Websocket.
To install the websocket, you need to download the websocket jar from the [releases page](https://github.com/ManaNet/Amelia/releases) and then setup the following
environment variables.
```
amelia_auth=AUTHENTICATION
amelia_db=mongodb://user:pass@ip:port
(OPTIONAL) amelia_websocket=ws://ip:3201/
```

After the environment variables are set, you can now start up the websocket with no configuration via: `java -jar Amelia-Websocket.jar`.

## Contribute
[Amelia Websocket Repository](https://github.com/ManaNet/Amelia-Websocket)
[Amelia Client Repository](https://github.com/ManaNet/Amelia)
