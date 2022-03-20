package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope= ObjectType.class, generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public enum ObjectType {
    STICKY_NOTE,
    IMAGE,
    DRAWING
}
