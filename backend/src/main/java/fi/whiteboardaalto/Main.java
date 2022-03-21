package fi.whiteboardaalto;

import org.apache.log4j.BasicConfigurator;

public class Main {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        new WhiteboardServer(44567).start();
    }

}