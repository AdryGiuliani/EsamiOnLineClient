package com.ingsw.esamionline.esamionlineclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {
    public static String dateFormatter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }
}
