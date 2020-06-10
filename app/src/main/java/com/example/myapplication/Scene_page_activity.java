package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Scene_page_activity extends AppCompatActivity implements View.OnClickListener{
    private AndroidDatabase androidDatabase;
    private List<Fragment> fragmentList;
    private List<RadioButton> ButtonViewList;
    private FragmentManager fManager;
    private ViewPager mcContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_page_activity);

        Intent intent = getIntent();
        final String movie_name = intent.getStringExtra("movie_name");
        Log.d("test", movie_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        RadioButton data1=(RadioButton)findViewById(R.id.data1);
        RadioButton data2=(RadioButton)findViewById(R.id.data2);
        RadioButton data3=(RadioButton)findViewById(R.id.data3);
        data1.setOnClickListener(this);
        data2.setOnClickListener(this);
        data3.setOnClickListener(this);
        ButtonViewList=new ArrayList<RadioButton>();
        ButtonViewList.add(data1);
        ButtonViewList.add(data2);
        ButtonViewList.add(data3);
        fragmentList=new ArrayList<Fragment>();

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);

        final SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 ", new String[]{movie_name});

        SimpleDraweeView Movie_img;
        Movie_img = (SimpleDraweeView) findViewById(R.id.movie_post);

        if (cursor.moveToFirst()) {
            do {
                Uri url = Uri.parse(cursor.getString(cursor.getColumnIndex("img_url")));
                Movie_img.setImageURI(url);
            }
            while (cursor.moveToNext());
        }
        Date date=new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
        String compare=sf.format(date);
        cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 and whole_time > ? group by date", new String[]{movie_name,compare});
        int count=0;
        if(cursor.moveToFirst()) {
            do {
                if("None".compareTo(cursor.getString(cursor.getColumnIndex("date")))!=0) {
                    Scene_fragment page = new Scene_fragment(cursor.getString(cursor.getColumnIndex("movie_name")), cursor.getString(cursor.getColumnIndex("date")));
                    fragmentList.add(page);
                    ButtonViewList.get(count).setText(cursor.getString(cursor.getColumnIndex("date")));
                    if(++count==3)
                        break;
                }
            }
            while (cursor.moveToNext());
        }

        while(count<3)
        {

            Scene_fragment page = new Scene_fragment("name", "data");
            ButtonViewList.get(count).setText("No schedule");
            fragmentList.add(page);
            count++;
        }

        ButtonViewList.get(0).setTextColor(Color.RED);
        mcContainer=(ViewPager)findViewById(R.id.fragment_container);
        mcContainer.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragmentList));

        mcContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Do Nothing
            }

            @Override
            public void onPageSelected(int position) {
                for (RadioButton viewer :
                        ButtonViewList) {
                    viewer.setTextColor(Color.BLACK);
                }
                ButtonViewList.get(position).setTextColor(Color.RED);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Do Nothing
            }
        });

    }
    @Override
    public void onClick(View v){
        fManager = getSupportFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();
        switch (v.getId()) {
            case R.id.data1:
                for (RadioButton viewer :
                        ButtonViewList) {
                    viewer.setTextColor(Color.BLACK);
                }
                RadioButton data1=(RadioButton)v;
                data1.setTextColor(Color.RED);
                mcContainer.setCurrentItem(0);
                Log.d("page","1");
                break;
            case R.id.data2:
                for (RadioButton viewer :
                        ButtonViewList) {
                    viewer.setTextColor(Color.BLACK);
                }
                RadioButton data2=(RadioButton)v;
                data2.setTextColor(Color.RED);
                mcContainer.setCurrentItem(1);
                Log.d("page","2");
                break;
            case R.id.data3:
                for (RadioButton viewer :
                        ButtonViewList) {
                    viewer.setTextColor(Color.BLACK);
                }
                RadioButton data3=(RadioButton)v;
                data3.setTextColor(Color.RED);
                mcContainer.setCurrentItem(2);
                Log.d("page","3");
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
