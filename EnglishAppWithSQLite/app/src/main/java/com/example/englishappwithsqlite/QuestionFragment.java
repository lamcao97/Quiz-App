package com.example.englishappwithsqlite;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.Interface.IQuestion;
import com.example.englishappwithsqlite.Model.CurrentQuestion;
import com.example.englishappwithsqlite.Model.Question;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements IQuestion {

    TextView txt_question_text;
    CheckBox ckbA,ckbB,ckbC,ckbD;
    FrameLayout layout_image;
    ProgressBar progressBar;

    Question question;
    int questionIndex=-1;


    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_question, container, false);

        questionIndex = getArguments().getInt("index",-1);
        question = Common.questionList.get(questionIndex);

        if(question !=null) {
            layout_image = itemView.findViewById(R.id.layout_image);

            progressBar = itemView.findViewById(R.id.progress_bar);
            if(question.isImageQuestion())
            {
                ImageView img_question = itemView.findViewById(R.id.img_question);
                Picasso.get().load(question.getQuestionImage()).into(img_question, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }else
                layout_image.setVisibility(View.GONE);



            txt_question_text = itemView.findViewById(R.id.txt_question_text);
            txt_question_text.setText(question.getQuestionText());


            ckbA = itemView.findViewById(R.id.ckbA);
            ckbA.setText(question.getAnswerA());
            ckbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        Common.selected_values.add(ckbA.getText().toString());
                    else
                        Common.selected_values.remove(ckbA.getText().toString());
                }
            });
            ckbB = itemView.findViewById(R.id.ckbB);
            ckbB.setText(question.getAnswerB());
            ckbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        Common.selected_values.add(ckbB.getText().toString());
                    else
                        Common.selected_values.remove(ckbB.getText().toString());
                }
            });
            ckbC = itemView.findViewById(R.id.ckbC);
            ckbC.setText(question.getAnswerC());
            ckbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        Common.selected_values.add(ckbC.getText().toString());
                    else
                        Common.selected_values.remove(ckbC.getText().toString());
                }
            });
            ckbD = itemView.findViewById(R.id.ckbD);
            ckbD.setText(question.getAnswerD());
            ckbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        Common.selected_values.add(ckbD.getText().toString());
                    else
                        Common.selected_values.remove(ckbD.getText().toString());
                }
            });

        }

        return itemView;
    }

    @Override
    public CurrentQuestion getSelectedAnswer() {
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex,Common.ANSWER_TYPE.NO_ANSWER);
        StringBuilder result = new StringBuilder();
        if(Common.selected_values.size()>1){
            Object[] arrayAnswer = Common.selected_values.toArray();
            for(int i=0;i<arrayAnswer.length;i++){
                if(i<arrayAnswer.length-1){
                    result.append(new StringBuilder(((String)arrayAnswer[i]).substring(0,1))).append(",");
                }else
                    result.append(new StringBuilder((String)arrayAnswer[i]).substring(0,1));
            }
        }else if(Common.selected_values.size() == 1){
            Object[] arrayAnswer = Common.selected_values.toArray();
            result.append((String)arrayAnswer[0]).substring(0,1);

        }

        if(question !=null){
            if(!TextUtils.isEmpty(result)) {
                if(result.toString().equals(question.getCorrectAnswer()))
                    currentQuestion.setType(Common.ANSWER_TYPE.RIGHT_ANSWER);
                else
                    currentQuestion.setType(Common.ANSWER_TYPE.WRONG_ANSWER);

            }else
                currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
        }else {
            Toast.makeText(getContext(),"Can't get question",Toast.LENGTH_SHORT).show();
        }
        Common.selected_values.clear();
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswer() {

        String[] correctAnswer = question.getCorrectAnswer().split(",");
        for(String answer:correctAnswer){

            if(answer.equals("A"))
            {
                ckbA.setTypeface(null,Typeface.BOLD);
                ckbA.setTextColor(Color.RED);
            }else if(answer.equals("B")){
                ckbB.setTypeface(null,Typeface.BOLD);
                ckbB.setTextColor(Color.RED);
            }else if(answer.equals("C")){
                ckbC.setTypeface(null,Typeface.BOLD);
                ckbC.setTextColor(Color.RED);
            }else if(answer.equals("D")){
                ckbD.setTypeface(null,Typeface.BOLD);
                ckbD.setTextColor(Color.RED);
            }

        }

    }

    @Override
    public void disableAnswer() {

        ckbA.setEnabled(false);
        ckbB.setEnabled(false);
        ckbC.setEnabled(false);
        ckbD.setEnabled(false);
    }

    @Override
    public void resetQuestion() {
        ckbA.setEnabled(true);
        ckbB.setEnabled(true);
        ckbC.setEnabled(true);
        ckbD.setEnabled(true);

        ckbA.setEnabled(false);
        ckbB.setEnabled(false);
        ckbC.setEnabled(false);
        ckbD.setEnabled(false);

        ckbA.setTypeface(null, Typeface.NORMAL);
        ckbA.setTextColor(Color.BLACK);
        ckbB.setTypeface(null, Typeface.NORMAL);
        ckbB.setTextColor(Color.BLACK);
        ckbC.setTypeface(null, Typeface.NORMAL);
        ckbC.setTextColor(Color.BLACK);
        ckbD.setTypeface(null, Typeface.NORMAL);
        ckbD.setTextColor(Color.BLACK);
    }
}
