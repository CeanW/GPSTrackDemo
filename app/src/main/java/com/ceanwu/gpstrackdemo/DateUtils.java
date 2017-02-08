package com.ceanwu.gpstrackdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class DateUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toDate(Date date){
        return sdf.format(date);
    }
}
