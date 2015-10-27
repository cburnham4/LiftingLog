package tracker.lift_log.ListViewHelpers;

/**
 * Created by cvburnha on 10/26/2015.
 */
public class Day {

    public Day(int did, String day) {
        this.day = day;
        this.did = did;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    private String day;
    private int did;

}
