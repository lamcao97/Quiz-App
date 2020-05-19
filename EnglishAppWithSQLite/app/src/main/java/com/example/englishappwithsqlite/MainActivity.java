package com.example.englishappwithsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.englishappwithsqlite.Adapter.CategoryAdapter;
import com.example.englishappwithsqlite.Common.Common;
import com.example.englishappwithsqlite.Common.SpaceDecoration;
import com.example.englishappwithsqlite.DBHelper.DBHelper;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView_category;
    CategoryAdapter adapter;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_settings)
        {
            showSettings();
        }
        return true;
    }

    private void showSettings() {
        View settings_layout = LayoutInflater.from(this)
            .inflate(R.layout.settings_layout,null);
        final CheckBox ckb_online_mode  = (CheckBox)settings_layout.findViewById(R.id.ckb_online_mode);

        // Load data from Paper, if not available just init default false
        ckb_online_mode.setChecked(Paper.book().read(Common.KEY_SAVE_ONLINE_MODE,false));

        // Show Dialog
        new MaterialStyledDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.ic_settings_white_24dp)
                .setTitle("Settings")
                .setDescription("Please choose action")
                .setCustomView(settings_layout)
                .setNegativeText("DISMISS")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("SAVE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(ckb_online_mode.isChecked()){
                            Common.isOnlineMode = true;
                        }else{
                            Common.isOnlineMode = false;
                        }

                        // Save
                        Paper.book().write(Common.KEY_SAVE_ONLINE_MODE,ckb_online_mode.isChecked());

                    }
                }).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Paper
        Paper.init(this);

        //Get value online Mode
        Common.isOnlineMode = Paper.book().read(Common.KEY_SAVE_ONLINE_MODE,false); // Default False

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("EDMT Quiz 2019");
        setSupportActionBar(toolbar);

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
