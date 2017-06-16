package models;

import android.widget.CheckBox;

/**
 * Created by ayaz on 11/6/17.
 */

public class Symptoms {
    private String name;
    private boolean isChecked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void toggleChecked() {
        isChecked = !isChecked ;
    }
}
