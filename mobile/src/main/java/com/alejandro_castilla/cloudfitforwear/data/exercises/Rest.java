package com.alejandro_castilla.cloudfitforwear.data.exercises;

/**
 * Created by alejandrocq on 6/07/16.
 */
public class Rest extends Exercise {
    private int restp = -1; //Set by trainer
    private int restr; //Goal of athlete

    public Rest (String title) {
        super(title);
    }

    public int getRestp() {
        return restp;
    }

    public void setRestp(int restp) {
        this.restp = restp;
    }

    public int getRestr() {
        return restr;
    }

    public void setRestr(int restr) {
        this.restr = restr;
    }
}
