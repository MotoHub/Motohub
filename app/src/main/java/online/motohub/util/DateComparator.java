package online.motohub.util;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import online.motohub.model.EventCategoryModel;

public class DateComparator implements Comparator<EventCategoryModel> {
    private String format;

    public DateComparator(String format) {
        this.format = format;
    }

    @Override
    public int compare(EventCategoryModel model, EventCategoryModel model1) {

        try {

            Date date = DateUtil.getDateTime(model.getStartTime(), format);
            Date date1 = DateUtil.getDateTime(model1.getStartTime(), format);

            if (date.after(date1)) {
                return -1;
            } else if (date.before(date1)) {
                return 1;
            } else {
                return 0;
            }

        } catch (ParseException e) {
            return -1;
        }

    }

}
