package models.playmodutils;

import java.util.List;
import java.util.ArrayList;

public class StatusItem{
    public String name;
    public boolean detected;
    public String status;
    public List<StatusItem> relatedItems;

    public StatusItem(){
        relatedItems = new ArrayList<StatusItem>();
    }
}