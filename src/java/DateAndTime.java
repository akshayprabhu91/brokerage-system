/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brokeragesystem_fall2016;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author PrabhuA6510
 */
public class DateAndTime {

    public static final String DATE_FORMAT_NOW = "yyyy-mm-dd hh:mm:ss";

    public static String DateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}
