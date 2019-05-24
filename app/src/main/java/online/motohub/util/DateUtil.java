package online.motohub.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static final String FORMAT_DMY_HMS = "yyyy-MM-dd hh:mm:ss";
    public static final String FORMAT_DMY = "yyyy-MM-dd";
    public static final String FORMAT_HMS = "hh:mm:ss";
    public static final String FORMAT_H_AP = "h a";
    public static final String FORMAT_HM_AP = "h:mm a";
    private static DateUtil mInstance = null;
    private static DateFormat sSourceFormat, sDestinationFormat;


    private DateUtil() {

    }

    public static DateUtil getInstance() {
        if (mInstance == null) {
            mInstance = new DateUtil();
        }
        return mInstance;
    }

    public static String getTime(String dateTime, String sourceFormat, String destinationFormat) throws ParseException {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        // sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        Date date = sSourceFormat.parse(dateTime);
        return getTime(date, destinationFormat);
    }

    public static String getTime(Date date, String destinationFormat) {
        sDestinationFormat = new SimpleDateFormat(destinationFormat, Locale.getDefault());
        //  sDestinationFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        return sDestinationFormat.format(date);
    }

    public static String getDate(String dateTime, String sourceFormat, String destinationFormat) throws ParseException {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        // sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        Date date = sSourceFormat.parse(dateTime);
        return getDate(date, destinationFormat);
    }

    public static String getDate(Date date, String destinationFormat) {
        sDestinationFormat = new SimpleDateFormat(destinationFormat, Locale.getDefault());
        // sDestinationFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        return sDestinationFormat.format(date);
    }

    public static Date getDateTime(String dateTime, String sourceFormat) throws ParseException {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        //  sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        return sSourceFormat.parse(dateTime);
    }

    public static Date getDateWoTime(String dateTime, String sourceFormat) throws ParseException {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        // sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        Date date = sSourceFormat.parse(dateTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateWoTime(Date dateTime, String sourceFormat) {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        // sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateTimeRoundOff(String dateTime, String sourceFormat) throws ParseException {
        sSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        // sSourceFormat.setTimeZone(TimeZone.getTimeZone("New Zealand"));
        Date date = sSourceFormat.parse(dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getFirstHourOfDay(Date date) {

        Calendar srcCal = Calendar.getInstance();
        srcCal.setTime(date);

        srcCal.set(Calendar.HOUR_OF_DAY, 6);
        srcCal.set(Calendar.MINUTE, 0);
        srcCal.set(Calendar.SECOND, 0);
        return srcCal.getTime();
    }

    public static Date nextHour(Date currentHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentHour);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date previousHour(Date currentHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentHour);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    public static Date getFirstPreviousHour(Date currentHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentHour);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date nextDay(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date previousDay(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }


}
