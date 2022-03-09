package fi.whiteboardaalto;

import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args) {
        /*
        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException nfe) {
            port = 9000;
        }
        */
        BasicConfigurator.configure();
        new WhiteboardServer(4444).start();

        /*
        ActionMessage msg = new ActionMessage("abcdefgh");
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = new Message("ActionMessage", msg);
            String messageAsString = mapper.writeValueAsString(message);
            System.out.println(messageAsString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}