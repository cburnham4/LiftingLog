package tracker.lift_log.ListViewHelpers;

/**
 * Created by cvburnha on 10/26/2015.
 */
public class Lift {

    private int lid;
    private int did;
    private String lift;

    public Lift(int lid, int did, String lift) {
        this.lid = lid;
        this.did = did;
        this.lift = lift;
    }

    public void setLift(String lift) {

        this.lift = lift;
    }

    public int getLid() {
        return lid;
    }

    public int getDid() {
        return did;
    }

    public String getLift() {
        return lift;
    }

}
