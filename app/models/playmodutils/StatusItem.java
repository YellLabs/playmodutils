package models.playmodutils;

import java.util.ArrayList;
import java.util.List;

public class StatusItem{
    public String type;

    public String name;
	public String value;

	public String status;
    
	public boolean detected;

    public List<StatusItem> relatedItems;

    public StatusItem(){
        relatedItems = new ArrayList<StatusItem>();
    }
}