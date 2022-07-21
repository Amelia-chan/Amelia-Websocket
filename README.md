# Amelia-Websocket
The websocket client for Amelia which handles the processing and checking from the ScribbleHub server.

### Setting up Amelia Websocket.
Amelia Websocket, which is a new addition implemented on Amelia 2.0, is a separate process of Amelia which can be said as the heart or the brain
of the Discord bot. The websocket handles the checking for updates on RSS Feeds and also Trending Notifications.

### 🖱️ Self-Hosting

You can set up Amelia's client by running the simple command below.

> **Warning**
>
> This assumes that you have Docker installed otherwise please install Docker first.
```shell
git clone https://github.com/ManaNet/Amelia-Websocket && cd Amelia-Websocket && docker build -t amelia-websocket .
```

Please configure the `.env` file before continuing, here is a shortcut command to creating the .env file:
```shell
cp .env.example .env && nano .env
```

Afterwards, you can run the following command:
```shell
docker run --name amelia-websocket -p 3201:3201 --env-file .env amelia-websocket:latest
```
## Contribute
[Amelia Websocket Repository](https://github.com/ManaNet/Amelia-Websocket)
[Amelia Client Repository](https://github.com/ManaNet/Amelia)
