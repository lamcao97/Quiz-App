package com.example.englishappwithsqlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.englishappwithsqlite.Adapter.CategoryAdapter;
import com.example.englishappwithsqlite.Common.SpaceDecoration;
import com.example.englishappwithsqlite.DBHelper.DBHelper;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView_category;
    CategoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("EDMT Quiz 2019");

        recyclerView_category = (RecyclerView) findViewById(R.id.recycler_category);
        recyclerView_category.setHasFixedSize(true);
        recyclerView_category.setLayoutManager(new GridLayoutManager(this,2));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 8;
        adapter = new CategoryAdapter(MainActivity.this, DBHelper.getInstance(this).getAllCategories());
        int spaceInPixel =4;
        recyclerView_category.addItemDecoration(new SpaceDecoration(spaceInPixel));
        recyclerView_category.setAdapter(adapter);
    }
}
