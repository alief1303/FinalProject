package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Comment_activity extends AppCompatActivity {
    String id=null;
    String moviename=null;
    String username;
    String order_id;
    SimpleDraweeView Movie_img;
    private RatingBar ratings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("COMMENTS");
        Intent intent = getIntent();
        moviename = intent.getStringExtra("movie_name");
        order_id=intent.getStringExtra("order_id");
        AndroidDatabase androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex("Username"));
        }


        cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 order by movie_id  --case-insensitive", new String[]{moviename});
        if (cursor.moveToFirst()) {

            id=String.valueOf(cursor.getInt(cursor.getColumnIndex("movie_id")));
            Movie_img = (SimpleDraweeView) findViewById(R.id.comment_movie_post);
            Uri url = Uri.parse( cursor.getString(cursor.getColumnIndex("img_url")));
            Movie_img.setImageURI(url);
        }
        get_comment();
        //cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        Button button = (Button) findViewById(R.id.comment_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_comment();
            }
        });

        ratings=(RatingBar)findViewById(R.id.ratingBar);
        ratings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

            }
        });
    }
    public void get_comment()
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("username",  username);
        formBuilder.add("movie_id", id);
        formBuilder.add("order_id", order_id);

        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_user_comments").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());

                        get_comment();
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        try {
                            final String res = response.body().string();
                            JSONObject res_inform = null;
                            res_inform = new JSONObject(res);
                            String body = res_inform.getString("body");
                            String rate1 = res_inform.getString("mark");
                            float rate=Float.parseFloat(rate1);
                            EditText text=findViewById(R.id.comment_edit);
                            text.setText(body);
                            ratings.setRating(rate);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }


                });
            }
        });
    }
    public void set_comment()
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        EditText text=findViewById(R.id.comment_edit);
        formBuilder.add("text", text.getText().toString() );
        formBuilder.add("username",  username);
        formBuilder.add("movie_id", id);
        formBuilder.add("order_id", order_id);
        formBuilder.add("rating", String.valueOf((int)(2*ratings.getRating())));
        Log.d("okhttp_error", String.valueOf((int)ratings.getRating()));
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/set_user_comments").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());

                        set_comment();
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    final String res = response.body().string();
                    @Override
                    public void run() {
                        Toast error_toast = Toast.makeText(Comment_activity.this, "Comment successfully!", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                        finish();
                    }
                });
            }
        });
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
