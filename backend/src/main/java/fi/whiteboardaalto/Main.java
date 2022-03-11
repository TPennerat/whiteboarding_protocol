package fi.whiteboardaalto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.objects.*;
import org.apache.log4j.BasicConfigurator;

public class Main {

    public static String superMessageSerialize(ObjectMapper mapper, Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SuperMessage superMessageDeserialize(ObjectMapper mapper, String object) {
        try {
            return mapper.readValue(object, SuperMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void testFunction() {
        ObjectMapper mapper = new ObjectMapper();

        BoardObject boardObject = new StickyNote(
                456789,
                "abcdefgh",
                true,
                new Coordinates(
                        2,
                        3
                ),
                new Colour(
                        255,
                        255,
                        255
                ),
                "This is a test!",
                "Comic Sans MS",
                new Coordinates(
                        3,
                        4
                )
        );

        CreateMeeting createMeeting = new CreateMeeting(123456, "", "EFgh4s32qaq7kl9b", "Lewdroth571");

        CreateObject createObject = new CreateObject(
                123456,
                "abcdefgh",
                45678,
                ObjectType.STICKY_NOTE,
                boardObject
        );

        try {
            String serial = mapper.writeValueAsString(createObject);
            SuperMessage superMessage = new SuperMessage(MessageType.CREATE_OBJECT, createObject);
            String serial2 = mapper.writeValueAsString(superMessage);
            System.out.println(serial2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        /*
        SuperMessage superMessage = new SuperMessage(MessageType.CREATE_MEETING, createMeeting);
        try {
            String serializedObject = superMessageSerialize(mapper, superMessage);
            System.out.println(serializedObject);
            SuperMessage superMessage2 = mapper.readValue(serializedObject, SuperMessage.class);
            System.out.println(superMessage2.getMessageType());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        */
    }

    public static void main(String[] args) {
        //testFunction();

        BasicConfigurator.configure();
        new WhiteboardServer(4444).start();
    }
}