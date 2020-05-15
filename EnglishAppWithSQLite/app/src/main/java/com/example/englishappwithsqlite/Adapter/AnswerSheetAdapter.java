package com.example.englishappwithsqlite.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.Model.CurrentQuestion;
import com.example.englishappwithsqlite.R;

import java.util.List;

public class AnswerSheetAdapter extends RecyclerView.Adapter<AnswerSheetAdapter.VH> {

    Context context;

    public AnswerSheetAdapter(Context context, List<CurrentQuestion> currentQuestions) {
        this.context = context;
        this.currentQuestions = currentQuestions;
    }

    List<CurrentQuestion> currentQuestions;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_sheet_item,parent,false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        if(currentQuestions.get(position).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER){
            holder.question_item.setBackgroundResource(R.drawable.grid_question_right_answer);
        }else if(currentQuestions.get(position).getType() == Common.ANSWER_TYPE.WRONG_ANSWER){
            holder.question_item.setBackgroundResource(R.drawable.grid_question_wrong_answer);
        }else
            holder.question_item.setBackgroundResource(R.drawable.grid_question_no_answer);

    }

    @Override
    public int getItemCount() {
        return currentQuestions.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        View question_item;
        public VH(@NonNull View itemView) {
            super(itemView);

            question_item=itemView.findViewById(R.id.question_item);
        }
    }
}
