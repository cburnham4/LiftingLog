package tracker.lift_log.ListViewHelpers;

import java.util.ArrayList;

/**
 * Created by cvburnha on 10/31/2015.
 */
public class PastDay {
    public ArrayList<Set> getSets() {
        return sets;
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addSet(Set set){
        sets.add(set);
    }

    public PastDay(ArrayList<Set> sets, String date) {

        this.sets = sets;
        this.date = date;
    }

    private ArrayList<Set> sets;
    private String date;
}
