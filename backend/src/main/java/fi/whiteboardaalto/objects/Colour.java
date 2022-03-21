package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Colour {
    private int r;
    private int g;
    private int b;

    @JsonCreator
    public Colour(@JsonProperty("r") int r, @JsonProperty("g") int g, @JsonProperty("b") int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
