package com.example.englishappwithsqlite.Adapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.Interface.IRecyclerHelperClick;
import com.example.englishappwithsqlite.Model.CurrentQuestion;
import com.example.englishappwithsqlite.R;

import java.util.List;


public class AnswerSheetHelperAdapter extends RecyclerView.Adapter<AnswerSheetHelperAdapter.MyViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionList;

    public AnswerSheetHelperAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_answer_sheet_helper,viewGroup,false);
//        return new MyViewHolder(itemView);
//    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_answer_sheet_helper,parent,false);
        return new MyViewHolder(itemView);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
//
//        myViewHolder.txt_question_num.setText(String.valueOf(i+1)); // Show question number
//        if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER)
//            myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_right_answer);
//        else if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER)
//            myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_wrong_answer);
//        else
//            myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_no_answer);
//        myViewHolder.setiRecyclerHelperClick(new IRecyclerHelperClick() {
//            @Override
//            public void onClick(View view, int position) {
//                //When user click to item , navigate to this question on Question Activity
//                LocalBroadcastManager.getInstance(context)
//                        .sendBroadcast(new Intent(
//                                Common.KEY_GO_TO_QUESTION
//                        ).putExtra(Common.KEY_GO_TO_QUESTION,position));
//            }
//        });
//    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_question_num.setText(String.valueOf(position+1)); // Show question number
        if(currentQuestionList.get(position).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_right_answer);
        else if(currentQuestionList.get(position).getType() == Common.ANSWER_TYPE.WRONG_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_wrong_answer);
        else
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_no_answer);
        holder.setiRecyclerHelperClick(new IRecyclerHelperClick() {
            @Override
            public void onClick(View view, int i) {
                //When user click to item , navigate to this question on Question Activity
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(new Intent(
                                Common.KEY_GO_TO_QUESTION
                        ).putExtra(Common.KEY_GO_TO_QUESTION,i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_question_num;
        LinearLayout layout_wrapper;
        IRecyclerHelperClick iRecyclerHelperClick;

        public void setiRecyclerHelperClick(IRecyclerHelperClick iRecyclerHelperClick) {
            this.iRecyclerHelperClick = iRecyclerHelperClick;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_question_num = (TextView)itemView.findViewById(R.id.txt_question_num);
            layout_wrapper = (LinearLayout) itemView.findViewById(R.id.layout_wrapper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerHelperClick.onClick(view,getAdapterPosition());
        }
    }
}