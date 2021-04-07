package cn.ecnu.tabusearch.utils;

import java.util.Date;

public class DateUtil {

    public static long TimeDifference(Date start, Date end) {
        long between = end.getTime() - start.getTime();
        long minutes =  (between / (1000));
        return minutes;
    }


}
