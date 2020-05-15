package com.example.englishappwithsqlite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.MainActivity;
import com.example.englishappwithsqlite.Model.Category;
import com.example.englishappwithsqlite.QuestionActivity;
import com.example.englishappwithsqlite.R;

import java.util.List;
import java.util.jar.Manifest;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_category,parent,false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.txt_category_name.setText(categories.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_category_name;
        CardView card_category;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_category = (CardView)itemView.findViewById(R.id.card_category);
            txt_category_name = (TextView)itemView.findViewById(R.id.txt_category_name);
            card_category.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Common.selectedCategory = categories.get(getAdapterPosition());
                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);
                }
            });

        }
    }
}
