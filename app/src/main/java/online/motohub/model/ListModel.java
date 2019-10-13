package online.motohub.model;


import java.util.ArrayList;

public class ListModel {

    private ArrayList<PromoterVideoModel.Resource> postList;

    public ArrayList<PromoterVideoModel.Resource> getPostList() {
        return postList;
    }

    public ListModel setPostList(ArrayList<PromoterVideoModel.Resource> postList) {
        this.postList = postList;
        return this;
    }
}
