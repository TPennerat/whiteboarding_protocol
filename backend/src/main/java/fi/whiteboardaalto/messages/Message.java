package fi.whiteboardaalto.messages;

public class Message {
    private int messageId;

    public Message(int messageId) {
        this.messageId = messageId;
    }


    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

}