package online.motohub.activity.promoter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.activity.BaseActivity;
import online.motohub.adapter.promoter.PromoterImgListAdapter;

public class PromotersImgListActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.img_recylerview)
    RecyclerView mImgListView;

    private PromoterImgListAdapter promoterImgListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoters_img_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();

        setToolbar(mToolbar, "Photos");
        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        mImgListView.setLayoutManager(new LinearLayoutManager(this));

        promoterImgListAdapter = new PromoterImgListAdapter((intent.getStringArrayExtra("img")), this);
        mImgListView.setAdapter(promoterImgListAdapter);
    }

    @OnClick(R.id.toolbar_back_img_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                finish();
                break;
        }
    }

}
