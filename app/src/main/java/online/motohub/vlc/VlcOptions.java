package online.motohub.vlc;

import java.util.ArrayList;

class VlcOptions {

    ArrayList<String> getDefaultOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add("-vvv");
        return options;
    }
}
