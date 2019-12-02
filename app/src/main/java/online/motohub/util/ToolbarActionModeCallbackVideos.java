package online.motohub.util;

import android.content.Context;
import android.os.Build;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import online.motohub.R;
import online.motohub.activity.ProfileVideoGalleryActivity;
import online.motohub.adapter.GalleryVideoAdapter;
import online.motohub.model.GalleryVideoResModel;

/**
 * Created by Prithiv on 5/14/2018.
 */

public class ToolbarActionModeCallbackVideos implements ActionMode.Callback {

    private Context context;
    private GalleryVideoAdapter GalleryVideoAdapter;
    private List<GalleryVideoResModel> message_models;


    public ToolbarActionModeCallbackVideos(Context context, GalleryVideoAdapter recyclerView_adapter, List<GalleryVideoResModel> message_models) {
        this.context = context;
        this.GalleryVideoAdapter = recyclerView_adapter;
        this.message_models = message_models;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_main, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                ((ProfileVideoGalleryActivity) context).deleteRows();//delete selected rows
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        GalleryVideoAdapter.removeSelection();  // remove selection
        ((ProfileVideoGalleryActivity) context).setNullToActionMode();//Set action mode null
    }
}