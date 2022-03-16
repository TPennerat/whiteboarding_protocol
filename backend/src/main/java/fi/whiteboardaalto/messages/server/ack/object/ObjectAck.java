package fi.whiteboardaalto.messages.server.ack.object;

import fi.whiteboardaalto.messages.server.Answer;

public class ObjectAck extends Answer {
    private String checksum;

    public ObjectAck(int messageId, int code, String checksum) {
        super(messageId, code);
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
