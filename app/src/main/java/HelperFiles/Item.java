package HelperFiles;

/**
 * Created by cvburnha on 2/22/2015.
 */
public class Item {

    private String title;
    private String description;

    public Item(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }
    // getters and setters...
    public void setTitle(String t){
        this.title = t;
    }
    public void setDescription(String d){
        this.description = d;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }

}
