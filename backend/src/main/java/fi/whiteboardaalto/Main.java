package fi.whiteboardaalto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.object.DeleteObject;
import fi.whiteboardaalto.messages.client.object.EditObject;
import fi.whiteboardaalto.messages.client.object.SelectObject;
import fi.whiteboardaalto.messages.client.object.change.Change;
import fi.whiteboardaalto.messages.client.object.change.EditType;
import fi.whiteboardaalto.messages.client.object.change.PositionChange;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.client.session.JoinMeeting;
import fi.whiteboardaalto.messages.client.session.LeaveMeeting;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;
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

        // Tests objects
        BoardObject boardObject = new StickyNote(
                "oufbuofb",
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
                "fnoznf",
                ObjectType.STICKY_NOTE,
                boardObject
        );

        JoinMeeting joinMeeting = new JoinMeeting(
                123456,
                "",
                "dEiufboprzbE",
                "Lewdroth"
        );

        ObjectCreated objectCreated = new ObjectCreated(
                123456,
                "biuvuzb",
                "testchecksum_sha256_nojuzrbgibjovbéoagbouéb"
        );

        SelectObject selectObject = new SelectObject(
                123456,
                "abcdefgh",
                "jfrojfbz"
        );

        DeleteObject deleteObject = new DeleteObject(
                123456,
                "abcdefgh",
                "nforboejr"
        );

        LeaveMeeting leaveMeeting = new LeaveMeeting(
                9999545,
                "fbrziubfu",
                "fbrzivrzyrzp"
        );

        PositionChange positionChange = new PositionChange(
                123456,
                new Coordinates(
                        5,
                        6
                )
        );

        EditObject editObject = new EditObject(
                123456,
                "abcdefgh",
                "cbE4",
                EditType.POSITION_CHANGE,
                positionChange
        );


        SuperMessage superMessage = new SuperMessage(MessageType.EDIT, editObject);
        try {
            String serializedObject = superMessageSerialize(mapper, superMessage);
            System.out.println(serializedObject);
            SuperMessage superMessage2 = mapper.readValue(serializedObject, SuperMessage.class);
            System.out.println(superMessage2.getMessageType());

            EditObject editObject1 = (EditObject) superMessage2.getMessage();
            switch(editObject1.getEditType()) {
                case POSITION_CHANGE -> {
                    PositionChange positionChange1 = (PositionChange) editObject1.getChange();
                    System.out.println("X = " + positionChange1.getNewPosition().getX());
                    System.out.println("Y = " + positionChange1.getNewPosition().getY());
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        testFunction();

        //BasicConfigurator.configure();
        //new WhiteboardServer(4444).start();
    }
}