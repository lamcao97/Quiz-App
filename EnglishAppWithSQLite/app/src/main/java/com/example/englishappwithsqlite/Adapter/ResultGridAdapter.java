package com.example.englishappwithsqlite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.Model.CurrentQuestion;
import com.example.englishappwithsqlite.R;

import java.util.List;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.MyViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionsList;

    public ResultGridAdapter(Context context, List<CurrentQuestion> currentQuestionsList) {
        this.context = context;
        this.currentQuestionsList = currentQuestionsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_result_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Drawable img;

        holder.btn_question.setText(new StringBuilder("Question ").append(currentQuestionsList.get(position)
        .getQuestionIndex()+1));
        if(currentQuestionsList.get(position).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER){
            holder.btn_question.setBackgroundColor(Color.parseColor("#ff99cc00"));
            img = context.getResources().getDrawable(R.drawable.ic_check_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        }
        else if(currentQuestionsList.get(position).getType() == Common.ANSWER_TYPE.WRONG_ANSWER){
            holder.btn_question.setBackgroundColor(Color.parseColor("#ffcc0000"));
            img = context.getResources().getDrawable(R.drawable.ic_clear_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        }
        else
        {
            img = context.getResources().getDrawable(R.drawable.ic_error_outline_white_24dp);
            holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button btn_question;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_question = itemView.findViewById(R.id.btn_question);
            btn_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(new Intent(Common.KEY_BACK_FROM_RESULT).putExtra(Common.KEY_BACK_FROM_RESULT,
                                    currentQuestionsList.get(getAdapterPosition()).getQuestionIndex())) ;
                }
            });
        }
    }
}
