import {WebsocketBuilder} from "websocket-ts";
import {MessageTypes} from "./MessageTypes";

const server = process.env.REACT_APP_WEBSOCKET_URL;
const port = process.env.REACT_APP_WEBSOCKET_PORT;
const ws = new WebsocketBuilder('ws://'+ server + ':' + port).build();

function sendDataToServer(type: MessageTypes, data: Object) {
    console.log(type, data);
    ws.send(JSON.stringify(data))
}

export default sendDataToServer;