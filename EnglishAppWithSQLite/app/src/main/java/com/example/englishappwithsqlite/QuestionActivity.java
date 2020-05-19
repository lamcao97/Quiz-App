package com.example.englishappwithsqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.englishappwithsqlite.Adapter.AnswerSheetAdapter;
import com.example.englishappwithsqlite.Adapter.AnswerSheetHelperAdapter;
import com.example.englishappwithsqlite.Adapter.QuestionFragmentAdapter;
import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.DBHelper.DBHelper;
import com.example.englishappwithsqlite.DBHelper.OnlineDBHelper;
import com.example.englishappwithsqlite.Interface.MyCallBack;
import com.example.englishappwithsqlite.Model.CurrentQuestion;
import com.example.englishappwithsqlite.Model.Question;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int CODE_GET_RESULT = 9999;
    int time_play = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    CountDownTimer countDownTimer;

    RecyclerView answer_sheet_view,answer_sheet_helper;

    AnswerSheetAdapter answerSheetAdapter;
    AnswerSheetHelperAdapter answerSheetHelperAdapter;

    TextView txt_right_answer,txt_timer,txt_wrong_answer;

    ViewPager viewPager;
    TabLayout tabLayout;
    DrawerLayout drawer;

    @Override
    protected void onDestroy() {
        if(Common.countDownTimer !=null){
            Common.countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //First , we need take question from DB
        takeQuestion();


    }

    private void finishGame() {

        int position = viewPager.getCurrentItem();
        QuestionFragment questionFragment = Common.fragmentList.get(position);
        CurrentQuestion question_state = questionFragment.getSelectedAnswer();
        Common.answerSheetList.set(position,question_state); // Set question answer for answersheet
        answerSheetAdapter.notifyDataSetChanged(); // Change color in answer sheet
        answerSheetHelperAdapter.notifyDataSetChanged();

        countCorrectAnswer();

        txt_right_answer.setText(new StringBuilder(String.format("%d",Common.right_answer_count))
                .append("/")
                .append(String.format("%d",Common.questionList.size())).toString());
        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));


        if(question_state.getType() == Common.ANSWER_TYPE.NO_ANSWER) {

            questionFragment.showCorrectAnswer();
            questionFragment.disableAnswer();
        }

        //We will navigate to new Result Activity here
        Intent intent = new Intent(QuestionActivity.this,ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - time_play;
        Common.no_answer_count = Common.questionList.size() - (Common.wrong_answer_count+Common.right_answer_count);
        Common.data_question = new StringBuilder(new Gson().toJson(Common.answerSheetList));

        startActivityForResult(intent,CODE_GET_RESULT);

    }

    private void countCorrectAnswer() {
        // Reset variable
        Common.right_answer_count = Common.wrong_answer_count =0;
        for(CurrentQuestion item:Common.answerSheetList)
            if(item.getType() == Common.ANSWER_TYPE.RIGHT_ANSWER)
                Common.right_answer_count++;
            else if(item.getType() == Common.ANSWER_TYPE.WRONG_ANSWER)
                Common.wrong_answer_count++;
    }


    private void genFragmentList() {
        for(int i=0;i<Common.questionList.size();i++){
            Bundle bundle = new Bundle();
            bundle.putInt("index",i);
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);

            Common.fragmentList.add(fragment);
        }
    }

    private void countTimer() {
        if(Common.countDownTimer == null){
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    time_play-=1000;
                }

                @Override
                public void onFinish() {
                    // Finish Game
                    finishGame();
                }
            }.start();
        } else {
            Common.countDownTimer.cancel();
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    time_play-=1000;
                }

                @Override
                public void onFinish() {
                    //Finish Game
                }
            }.start();
        }
    }

    private void takeQuestion(){

        if(!Common.isOnlineMode){

            Common.questionList = DBHelper.getInstance(this)
                    .getQuestionByCategory(Common.selectedCategory.getId());


            if(Common.questionList.size() == 0){

                //If no question
                new MaterialStyledDialog.Builder(this)
                        .setTitle("Opps !!!")
                        .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                        .setDescription("We dont have any question in this"+Common.selectedCategory.getName()+" category")
                        .setPositiveText("OKE")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
            }
            else {
                if(Common.answerSheetList.size()>0)
                    Common.answerSheetList.clear();
                // Get answerSheet item from question
                //  30 question = 30 answer sheet item
                // 1 question = 1 answer sheet item
                for(int i =0;i<Common.questionList.size();i++) {
                    // because we need take index of Question in list , so we will use for i
                    Common.answerSheetList.add(new CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER)); // Default all answer is no 
                }
            }

            setupQuestion();

        }else {

            OnlineDBHelper.getInstance(this,FirebaseDatabase.getInstance())
                    .readData(new MyCallBack() {
                        @Override
                        public void setQuestionList(List<Question> questionList) {

                            Common.questionList.clear();
                            Common.questionList = questionList;

                            if(Common.questionList.size() == 0){

                                //If no question
                                new MaterialStyledDialog.Builder(QuestionActivity.this)
                                        .setTitle("Opps !!!")
                                        .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                                        .setDescription("We dont have any question in this"+Common.selectedCategory.getName()+" category")
                                        .setPositiveText("OKE")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).show();
                            }
                            else {
                                if(Common.answerSheetList.size()>0)
                                    Common.answerSheetList.clear();
                                // Get answerSheet item from question
                                //  30 question = 30 answer sheet item
                                // 1 question = 1 answer sheet item
                                for(int i =0;i<Common.questionList.size();i++) {
                                    // because we need take index of Question in list , so we will use for i
                                    Common.answerSheetList.add(new CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER)); // Default all answer is no
                                }
                            }

                            setupQuestion();

                        }
                    },Common.selectedCategory.getName().replace(" ","").replace("/","" + "_"));

        }



    }

    private void setupQuestion() {

        if(Common.questionList.size()>0){

            // Show TextView right answer and Text View Timer
            txt_timer = findViewById(R.id.txt_timer);
            txt_right_answer = findViewById(R.id.txt_question_right);

            txt_timer.setVisibility(View.VISIBLE);
            txt_right_answer.setVisibility(View.VISIBLE);

            txt_right_answer.setText(new StringBuilder(String.format("%d/%d"
                    ,Common.right_answer_count
                    ,Common.questionList.size())));

            countTimer();

            //View
            answer_sheet_view = findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);
            if(Common.questionList.size()>5){  // If question List have size >5 , we will sperate 2 rows
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this,Common.questionList.size()/2));
            }


            answer_sheet_helper = findViewById(R.id.answer_sheet);
            answer_sheet_helper.setHasFixedSize(true);
            if(Common.questionList.size()>5){  // If question List have size >5 , we will sperate 2 rows
                answer_sheet_helper.setLayoutManager(new GridLayoutManager(this,Common.questionList.size()/2));
            }


            answerSheetAdapter = new AnswerSheetAdapter(this,Common.answerSheetList);
            answer_sheet_view.setAdapter(answerSheetAdapter);

            answerSheetHelperAdapter = new AnswerSheetHelperAdapter(this,Common.answerSheetList);
            answer_sheet_helper.setAdapter(answerSheetHelperAdapter);

            viewPager = findViewById(R.id.viewpaper);
            tabLayout = findViewById(R.id.sliding_tabs);

            genFragmentList();

            QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(getSupportFragmentManager(),
                    this,
                    Common.fragmentList);

            viewPager.setAdapter(questionFragmentAdapter);
            viewPager.setOffscreenPageLimit(Common.questionList.size()); // Fixed ViewPager size
            tabLayout.setupWithViewPager(viewPager);

            //Event
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int SCROLLING_RIGHT =0;
                int SCROLLING_LEFT = 1;
                int SCROLLING_UNDETERMINED =2;

                int currentScrollDirection =2;

                private void setScrollingDirection(float positionOffset){
                    if((1-positionOffset)>=0.5) {
                        this.currentScrollDirection = SCROLLING_RIGHT;
                    }
                    else if((1-positionOffset)<=0.5) {
                        this.currentScrollDirection = SCROLLING_LEFT;
                    }
                }

                private boolean isScrollDirectionUndetermined(){
                    return currentScrollDirection == SCROLLING_UNDETERMINED;
                }

                private boolean isScrollingRight(){
                    return currentScrollDirection == SCROLLING_RIGHT;
                }

                private boolean isScrollingLeft(){
                    return currentScrollDirection == SCROLLING_LEFT;
                }


                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    if(isScrollDirectionUndetermined())
                        setScrollingDirection(positionOffset);

                }

                @Override
                public void onPageSelected(int position) {

                    QuestionFragment questionFragment;
                    int vitri = 0;

                    if (position > 0) {

                        if (isScrollingRight()) {

                            //If user scroll to right , get previous fragment to calculte result
                            questionFragment = Common.fragmentList.get(position - 1);
                            vitri = position - 1;

                        } else if (isScrollingLeft()) {

                            //If user scroll to left , get next fragment to calculte result
                            questionFragment = Common.fragmentList.get(position + 1);
                            vitri = position + 1;

                        } else {

                            questionFragment = Common.fragmentList.get(vitri);

                        }

                    } else {

                        questionFragment = Common.fragmentList.get(0);
                        vitri = 0;

                    }

                    //Optimize , only question have no answer just active this code
                    if (Common.answerSheetList.get(vitri).getType() == Common.ANSWER_TYPE.NO_ANSWER) {

                        CurrentQuestion question_state = questionFragment.getSelectedAnswer();
                        Common.answerSheetList.set(vitri, question_state);   // Set question answer for answersheet
                        answerSheetAdapter.notifyDataSetChanged();  // Change color in answer sheet
                        answerSheetHelperAdapter.notifyDataSetChanged();

                        countCorrectAnswer();

                        txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                                .append("/")
                                .append(String.format("%d", Common.questionList.size())).toString());
                        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

                        if (question_state.getType() == Common.ANSWER_TYPE.NO_ANSWER) {
                            questionFragment.showCorrectAnswer();
                            questionFragment.disableAnswer();
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state==ViewPager.SCROLL_STATE_IDLE)
                        this.currentScrollDirection = SCROLLING_UNDETERMINED;
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_wrong_answer);
        ConstraintLayout constraintLayout = (ConstraintLayout) item.getActionView();
        txt_wrong_answer = constraintLayout.findViewById(R.id.txt_wrong_answer);
        txt_wrong_answer.setText(String.valueOf(0));

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if its is present
        getMenuInflater().inflate(R.menu.question,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_finish_game){
            if(!isAnswerModeView){

                new MaterialStyledDialog.Builder(this)
                        .setTitle("Finish ?")
                        .setIcon(R.drawable.ic_mood_black_24dp)
                        .setDescription("Do you really want to finish ?")
                        .setNegativeText("No")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finishGame();
                                //drawer.closeDrawer(Gravity.LEFT);
                            }
                        }).show();

            }
            else
                finishGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here
        int id = menuItem.getItemId();

        if(id == R.id.nav_home){
            // Handle the camera action
        }else if(id == R.id.nav_gallery){

        }else if(id == R.id.nav_slideshow){

        }else if(id == R.id.nav_share){

        }else if(id == R.id.nav_send){

        }else if(id == R.id.nav_tools){

        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_GET_RESULT){

            if(resultCode == Activity.RESULT_OK){

                String action = data.getStringExtra("action");
                if(action == null || TextUtils.isEmpty(action)){
                    int questionNum = data.getIntExtra(Common.KEY_BACK_FROM_RESULT,-1);
                    viewPager.setCurrentItem(questionNum);

                    isAnswerModeView = true;
                    Common.countDownTimer.cancel();

                    txt_wrong_answer.setVisibility(View.GONE);
                    txt_right_answer.setVisibility(View.GONE);
                    txt_timer.setVisibility(View.GONE);
                }else {
                    if(action.equals("viewquizanswer"))
                    {
                        viewPager.setCurrentItem(0);

                        isAnswerModeView = true;
                        Common.countDownTimer.cancel();

                        txt_wrong_answer.setVisibility(View.GONE);
                        txt_right_answer.setVisibility(View.GONE);
                        txt_timer.setVisibility(View.GONE);

                        for(int i=0;i<Common.fragmentList.size();i++){

                            Common.fragmentList.get(i).showCorrectAnswer();
                            Common.fragmentList.get(i).disableAnswer();
                        }
                    }
                    else if(action.equals("doitagain"))
                    {
                        viewPager.setCurrentItem(0);

                        isAnswerModeView = false;
                        countTimer();

                        txt_wrong_answer.setVisibility(View.VISIBLE);
                        txt_right_answer.setVisibility(View.VISIBLE);
                        txt_timer.setVisibility(View.VISIBLE);

                        for (CurrentQuestion item:Common.answerSheetList)
                            item.setType(Common.ANSWER_TYPE.NO_ANSWER);
                        answerSheetAdapter.notifyDataSetChanged();
                        answerSheetHelperAdapter.notifyDataSetChanged();

                        for(int i=0;i<Common.fragmentList.size();i++)
                            Common.fragmentList.get(i).resetQuestion();

                    }
                }

            }
        }
    }
}
