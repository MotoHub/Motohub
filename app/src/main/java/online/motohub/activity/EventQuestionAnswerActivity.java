package online.motohub.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.motohub.R;
import online.motohub.adapter.EventRegistrationQuestionAdapter;
import online.motohub.application.MotoHub;
import online.motohub.dialog.DialogManager;
import online.motohub.model.EventAnswersModel;
import online.motohub.model.EventCategoryModel;
import online.motohub.model.EventRegistrationQuestionModel;
import online.motohub.model.SessionModel;
import online.motohub.retrofit.RetrofitClient;
import online.motohub.util.PreferenceUtils;


public class EventQuestionAnswerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.qtn_list_rv)
    RecyclerView mQuestionListRecyclerView;

    @BindString(R.string.event_questions)
    String mToolbarTitle;

    @BindView(R.id.events_question_list_co_layout)
    CoordinatorLayout mEventQuestionListLayout;

    private ArrayList<EventRegistrationQuestionModel> mEventQuestionList;
    private EventRegistrationQuestionAdapter mEventQuestionAdapter;

    private ArrayList<EventAnswersModel> mEventAnswerModelList = new ArrayList<>();
    private ArrayList<EventCategoryModel> mEventCategoryList = new ArrayList<>();

    private int mProfileID, mEventID;

    private boolean isAnsweredAllQuestion = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_question_list);

        ButterKnife.bind(this);
        setupUI(mEventQuestionListLayout);

        initView();

    }

    @Override
    protected void onDestroy() {
        DialogManager.hideProgress();
        super.onDestroy();
    }

    private void initView() {

        setToolbar(mToolbar, mToolbarTitle);

        showToolbarBtn(mToolbar, R.id.toolbar_back_img_btn);

        setQuestions();
    }

    private void setQuestions() {

        if (getIntent().hasExtra("ProfileID"))
            mProfileID = getIntent().getIntExtra("ProfileID", 0);

        if (getIntent().hasExtra("EventID"))
            mEventID = getIntent().getIntExtra("EventID", 0);

        mEventQuestionList = (ArrayList<EventRegistrationQuestionModel>) getIntent().getSerializableExtra("QuestionList");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mQuestionListRecyclerView.setLayoutManager(mLayoutManager);
        mQuestionListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEventQuestionAdapter = new EventRegistrationQuestionAdapter(this, mEventQuestionList, null);
        mQuestionListRecyclerView.setAdapter(mEventQuestionAdapter);


    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    @OnClick({R.id.toolbar_back_img_btn, R.id.save_answers_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_img_btn:
                onBackPressed();
                break;

            case R.id.save_answers_btn:

                saveAnswers();

                break;
        }
    }

    private void saveAnswers() {
        String[] mAnswers = mEventQuestionAdapter.getAnswers();
        JsonArray mJsonArray = new JsonArray();

        for (int i = 0; i < mEventQuestionList.size(); i++) {
            if (mAnswers[i] == null || mAnswers[i].trim().isEmpty()) {
                isAnsweredAllQuestion = false;
                int mQtnPos = i + 1;
                String mErrToast = "Please answer the question no " + mQtnPos;
                Toast.makeText(this, mErrToast, Toast.LENGTH_SHORT).show();
                break;
            } else {
                isAnsweredAllQuestion = true;
            }
        }

        if (isAnsweredAllQuestion) {

            JsonObject mQuestionAnswer = new JsonObject();

            for (int j = 0; j < mEventQuestionList.size(); j++) {

                mQuestionAnswer.addProperty(mEventQuestionList.get(j).getQuestion(), mAnswers[j]);

                EventAnswersModel mEventAnswerModel = new EventAnswersModel();
                mEventAnswerModel.setAnswer(mAnswers[j]);
                mEventAnswerModel.setEventID(mEventID);
                mEventAnswerModel.setGroupID(mEventQuestionList.get(j).getGroupID());
                mEventAnswerModel.setProfileID(mProfileID);
                mEventAnswerModel.setQuestionID(mEventQuestionList.get(j).getQuestionID());

                JsonObject mItem = new JsonObject();
                mItem.addProperty(EventAnswersModel.ANSWER, mAnswers[j]);
                mItem.addProperty(EventAnswersModel.QUESTION_ID, mEventQuestionList.get(j).getQuestionID());
                mItem.addProperty(EventAnswersModel.EVENT_ID, mEventID);
                mItem.addProperty(EventAnswersModel.PROFILE_ID, mProfileID);
                mItem.addProperty(EventAnswersModel.GROUP_ID, mEventQuestionList.get(j).getGroupID());


                mEventAnswerModelList.add(mEventAnswerModel);
                mJsonArray.add(mItem);
            }

            MotoHub.getApplicationInstance().setEventQuestionAnswerObject(mQuestionAnswer);

            RetrofitClient.getRetrofitInstance().callPostAnswerForEventRegistrationQuestions(this, mJsonArray, RetrofitClient.POST_EVENT_ANSWERS);
        }
    }

    @Override
    public void retrofitOnResponse(Object responseObj, int responseType) {
        super.retrofitOnResponse(responseObj, responseType);
        if (responseObj instanceof EventAnswersModel) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("AnswerModel", mEventAnswerModelList);
            setResult(RESULT_OK, resultIntent);
            finish();

        } else if (responseObj instanceof SessionModel) {

            SessionModel mSessionModel = (SessionModel) responseObj;
            if (mSessionModel.getSessionToken() == null) {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionId());
            } else {
                PreferenceUtils.getInstance(this).saveStrData(PreferenceUtils.SESSION_TOKEN, mSessionModel.getSessionToken());
            }

        }
    }

    @Override
    public void retrofitOnError(int code, String message) {
        super.retrofitOnError(code, message);
        if (message.equals("Unauthorized") || code == 401) {
            RetrofitClient.getRetrofitInstance().callUpdateSession(this, RetrofitClient.UPDATE_SESSION_RESPONSE);
        } else {
            String mErrorMsg = code + " - " + message;
            showSnackBar(mEventQuestionListLayout, mErrorMsg);
        }
    }

    @Override
    public void retrofitOnFailure() {
        super.retrofitOnFailure();
    }


    @Override
    public void retrofitOnSessionError(int code, String message) {
        super.retrofitOnSessionError(code, message);
    }
}
