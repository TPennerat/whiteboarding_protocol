import {sendDataToServer} from "../utils/websocket";
import {MessageTypes} from "../utils/MessageTypes";

function Whiteboard(){

    sendDataToServer(MessageTypes.JOIN_MEETING, {test:'test'});

    return(
        <div>
            WORK IN PROGRESS ...
        </div>
    )
}

export default Whiteboard