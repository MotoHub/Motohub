package online.motohub.fragment.track;


import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import online.motohub.R;
import online.motohub.fragment.BaseFragment;
import online.motohub.model.TrackResModel;
import online.motohub.model.promoter_club_news_media.PromotersResModel;

public class TrackContactFragment extends BaseFragment {

    @BindView(R.id.contact_person_text)
    TextView mContactNameTv;
    @BindView(R.id.contact_number_text)
    TextView mContactNumberTv;
    @BindView(R.id.contact_mail_text)
    TextView mContactMailTv;
    @BindView(R.id.location_text)
    TextView mLocationTv;
    @BindView(R.id.contact_location_map_view)
    MapView mMapView;
    @BindView(R.id.track_contact_lay)
    RelativeLayout mTrackContactLay;
    private Unbinder mUnBinder;
    private TrackResModel mTrackResModel;
    OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            double lat, lng;
            String trackName;

            if (mTrackResModel != null && !TextUtils.isEmpty(mTrackResModel.getLatitude())
                    && !TextUtils.isEmpty(mTrackResModel.getLongitude())) {

                lat = Double.parseDouble(mTrackResModel.getLatitude());
                lng = Double.parseDouble(mTrackResModel.getLongitude());
                trackName = mTrackResModel.getTrackName();

                LatLng mTrackLatLng = new LatLng(lat, lng);

                googleMap.addMarker(new MarkerOptions()
                        .position(mTrackLatLng)
                        .draggable(false)
                        .title(trackName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mTrackLatLng));
            }
        }
    };

    public TrackContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        //PromotersResModel mPromoterResModel = (PromotersResModel) getArguments().getSerializable(PromotersModel.PROMOTERS_RES_MODEL);
        //PromotersResModel mPromoterResModel = MotoHub.getApplicationInstance().getmPromoterResModel();
        PromotersResModel mPromoterResModel = EventBus.getDefault().getStickyEvent(PromotersResModel.class);
        assert mPromoterResModel != null;
        mTrackResModel = mPromoterResModel.getTrackByUserID();

        mUnBinder = ButterKnife.bind(this, view);
        if (mTrackResModel != null) {
            mContactNameTv.setText(mTrackResModel.getContactName());
            mContactNumberTv.setText(mTrackResModel.getContactNumber());
            mContactMailTv.setText(mTrackResModel.getContactMailId());
            mLocationTv.setText(mTrackResModel.getAddress());
        } else {
            mTrackContactLay.setVisibility(View.GONE);

        }

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        mMapView.getMapAsync(mOnMapReadyCallback);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }


}
