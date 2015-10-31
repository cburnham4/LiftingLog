package tracker.lift_log.ListViewHelpers;

/**
 * Created by cvburnha on 10/30/2015.
 */
public class Set {
    public Set(int sid, int lid, int weight, int reps, String date) {
        this.sid = sid;
        this.lid = lid;
        this.weight = weight;
        this.reps = reps;
        this.date = date;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private int sid;
    private int lid;
    private int weight;
    private int reps;
    private String date;


}
