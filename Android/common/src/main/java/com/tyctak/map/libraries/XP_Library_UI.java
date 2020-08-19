package com.tyctak.map.libraries;

public class XP_Library_UI {

    public String displayUpdateText(Integer length) {
        String retval;

        if (length == 0) {
            retval = "280 characters";
        } else if (length < 280) {
            retval = length + " / 280 characters";
        } else {
            retval = "280 character maximum reached";
        }

        return retval;
    }

}
