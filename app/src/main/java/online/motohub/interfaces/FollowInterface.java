package online.motohub.interfaces;

import online.motohub.model.ProfileResModel;

public interface FollowInterface {

    void unFollowProfileRequest();

    void followProfileSuccess(ProfileResModel myProfileResModel, ProfileResModel otherProfileResModel);

    void unFollowProfileSuccess(ProfileResModel myProfileResModel, ProfileResModel otherProfileResModel);

}
