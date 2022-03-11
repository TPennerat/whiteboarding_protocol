package fi.whiteboardaalto.messages.server.ack.object;

import fi.whiteboardaalto.messages.server.Answer;
import fi.whiteboardaalto.messages.server.AnswerType;

public class ObjectAck extends Answer {
    private String checksum;

    public ObjectAck(int messageId, AnswerType answerType, int code, String checksum) {
        super(messageId, answerType, code);
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
