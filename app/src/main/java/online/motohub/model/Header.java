package online.motohub.model;

import java.io.Serializable;

/**
 * Created by pyd10 on 10/31/2017.
 */

public class Header implements Serializable {

    public String CSeq;
    public String ErrorNum;
    public String ErrorString;
    public String MessageType;
    public String Version;

}
