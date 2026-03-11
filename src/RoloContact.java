public class RoloContact {
    private String lname;
    private String fname;
    private String pn;
    private String ad;
    public RoloContact(String lname, String fname, String pn, String ad) {
        this.lname = lname;
        this.fname = fname;
        this.pn = pn;
        this.ad = ad;
    }
    public String getf() {
        return fname;
    }
    public String getl() {
        return lname;
    }
    public String getp() {
        return pn;
    }
    public String geta() {
        return ad;
    }
    public void setf(String f) {
        fname = f;
    }
    public void setl(String l) {
        lname = l;
    }
    public void setp(String p) {
        pn = p;
    }
    public void seta(String a) {
        ad = a;
    }
    public String toString() {
        return lname + "," + fname;
    }
}