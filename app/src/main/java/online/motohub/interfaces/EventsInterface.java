package online.motohub.interfaces;

import android.support.annotation.NonNull;
import android.widget.Filter;

import online.motohub.model.EventsWhoIsGoingResModel;

public interface EventsInterface {

    void bookAnEventSuccess(EventsWhoIsGoingResModel eventsWhoIsGoingResModel);

    @NonNull
    Filter getFilter();
}
