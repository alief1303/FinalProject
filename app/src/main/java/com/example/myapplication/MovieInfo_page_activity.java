package com.example.myapplication;

import android.app.AppComponentFactory;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfo_page_activity extends AppCompatActivity {
    private AndroidDatabase androidDatabase;
    private String id;
    SimpleDraweeView Movie_img;
    SimpleDraweeView Movie_img2;
    TextView Movie_name;
    TextView movie_release_data;
    TextView movie_length;
    TextView movie_score;
    TextView movie_type;
    TextView movie_actors;
    TextView movie_director;
    TextView introduction;
    Button purchase_button;
    String movie_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movieinfo_page_layout);
        Intent intent = getIntent();
        movie_name= intent.getStringExtra("movie_name");
        Log.d("test", movie_name);
/*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");*/
        androidx.appcompat.widget.Toolbar toolbar=(androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MOVIE INFO");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE scene=? and instr(upper(movie_name), upper(?)) > 0   --case-insensitive", new String[]{"0",movie_name});


            if (cursor.moveToFirst()) {
            id=String.valueOf(cursor.getInt(cursor.getColumnIndex("movie_id")));

            Log.d("okhttp_error", cursor.getString(cursor.getColumnIndex("scene")));
            Movie_img = (SimpleDraweeView) findViewById(R.id.movieinfo_img);
            Movie_img2 = (SimpleDraweeView) findViewById(R.id.post_img);
            Movie_name = (TextView) findViewById(R.id.movie_name);
            movie_release_data = (TextView) findViewById(R.id.movie_release_date);
            movie_length = (TextView) findViewById(R.id.movie_length);
            movie_score = (TextView) findViewById(R.id.movie_score);
            movie_type = (TextView) findViewById(R.id.movie_type);
            movie_actors = (TextView) findViewById(R.id.movie_actor);
            movie_director = (TextView) findViewById(R.id.movie_director);
            purchase_button = (Button) findViewById(R.id.purchase_button);
            introduction=(TextView) findViewById(R.id.introduction);

            Uri url = Uri.parse( cursor.getString(cursor.getColumnIndex("img_url")));
            Movie_img.setImageURI(url);

            Uri url2 = Uri.parse(cursor.getString(cursor.getColumnIndex("img_url")));
            Movie_img2.setImageURI(url2);

            Movie_name.setText(cursor.getString(cursor.getColumnIndex("movie_name")));
            movie_release_data.setText("premiere date : "+ cursor.getString(cursor.getColumnIndex("premiere_date")));
            movie_length.setText("Length : " + cursor.getString(cursor.getColumnIndex("movie_length")));
            movie_score.setText(cursor.getString(cursor.getColumnIndex("score")) + "/10");
            movie_type.setText(cursor.getString(cursor.getColumnIndex("country")) + "/" + cursor.getString(cursor.getColumnIndex("movie_type")));
            movie_actors.setText("Actors : " + cursor.getString(cursor.getColumnIndex("actors")));
            movie_director.setText("Director : " + cursor.getString(cursor.getColumnIndex("director")));
            introduction.setText(cursor.getString(cursor.getColumnIndex("introduction")));

            purchase_button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view)
                {
                    Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
                    if(cursor.getCount()==0)
                    {
                        Intent intent = new Intent(MovieInfo_page_activity.this,Login_page_activity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent=new Intent(MovieInfo_page_activity.this,Scene_page_activity.class);
                        intent.putExtra("movie_name",movie_name);
                        MovieInfo_page_activity.this.startActivity(intent);
                    }
                }
            });

        }
        Date date=new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
        String compare=sf.format(date);
        cursor= db.rawQuery("select * from Movie where whole_time > ? and instr(upper(movie_name), upper(?)) > 0", new String[]{compare,movie_name});
        if(cursor.getCount()<=1) {
            purchase_button.setEnabled(false);
            purchase_button.setText("No schedule");
            //purchase_button.setBackgroundColor(Color.WHITE);
        }
        RequestForMovieInform();
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

    GridLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    WaterFallAdapter mAdapter;
    private void init(List<Adapater_common_type> list) {

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mLayoutManager = new GridLayoutManager(MovieInfo_page_activity.this,1);
        mAdapter = new WaterFallAdapter(MovieInfo_page_activity.this, list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public List<Adapater_common_type> RequestForMovieInform()  {
        OkHttpClient client = new OkHttpClient();
        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();
        final List<Adapater_common_type> list=new ArrayList<>();
        FormBody.Builder formBuilder = new FormBody.Builder();

        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        String user_name=null;
        if(cursor.moveToFirst())
        {
            user_name = cursor.getString(cursor.getColumnIndex("Username"));


        }
        //formBuilder.add("User_name", user_name);
        // formBuilder.add("key_word", Login_page_activity.password);
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_comments").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        final String movie_id=id;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());
                        Toast error_toast = Toast.makeText(MovieInfo_page_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                        RequestForMovieInform();
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();

                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        try {

                            Log.d("okhttp_error", res);
                            JSONArray jsonArray = new JSONArray(res);

                            Cursor cursor = db.rawQuery("select * from comments", new String[]{});
                            int[] arr = new int[200];
                            if(cursor.getCount()!=0) {

                                if(cursor.moveToFirst()) {
                                    do {
                                        arr[cursor.getInt(cursor.getColumnIndex("comment_id"))]=1;
                                    }
                                    while(cursor.moveToNext());

                                }
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                if (arr[jsonObject.getInt("comment_id")]!=1) {
                                    Log.d("okhttp_error", String.valueOf(jsonObject.getInt("comment_id")) + " " + cursor.getCount());
                                    db.execSQL("insert into comments values(?,?,?,?,?,?,?,?)"
                                            , new Object[]{jsonObject.getInt("comment_id"), jsonObject.getString("body"),
                                                    jsonObject.getString("author_id"), jsonObject.getString("movie_id"),
                                                    jsonObject.getString("User_name"), "http://nightmaremlp.pythonanywhere.com/img/"+jsonObject.getString("User_avatar"),
                                                    jsonObject.getString("mark"),jsonObject.getString("order_id")
                                            });
                                }
                                else
                                {
                                    ContentValues values = new ContentValues();
                                    values.put("body", jsonObject.getString("body"));
                                    values.put("mark", jsonObject.getString("mark"));
                                    db.update("comments", values, "comment_id=?",
                                            new String[]{String.valueOf(jsonObject.getInt("comment_id"))});
                                }
                            }



                            final SQLiteDatabase db1 = androidDatabase.getWritableDatabase();

                            cursor = db1.rawQuery("select * from comments where movie_id=?", new String[]{movie_id});
                            if(cursor.moveToFirst()) {
                                do {

                                    Adapter_comments comment= new Adapter_comments();

                                    comment.comment = cursor.getString(cursor.getColumnIndex("body"));
                                    comment.user_img = cursor.getString(cursor.getColumnIndex("User_avatar"));
                                    comment.user_name = cursor.getString(cursor.getColumnIndex("User_name"));
                                    String rate1 = cursor.getString(cursor.getColumnIndex("mark"));
                                    float rate=Float.parseFloat(rate1);
                                    comment.mark=rate;
                                    list.add(comment);
                                }
                                while(cursor.moveToNext());

                            }
                            init(list);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }

        });


        return list;

    }

}
