package online.motohub.interfaces;

import java.util.List;

public interface PermissionViewModelCallback {

    void requestPermission(List<String> strings, int code);
}
