package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class User_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="User_page_activity";
    private TextView username;
    private TextView vip_level;
    private SimpleDraweeView userAvatar;
    private Button profile;
    private Button tickets;
    private Button history;
    private Button membership;
    private Button settings;
    private Button logout;
    private AndroidDatabase androidDatabase;
    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_layout);

        userAvatar=findViewById(R.id.user_avatar);
        username=findViewById(R.id.username);
        vip_level=findViewById(R.id.vip_level);
        profile=findViewById(R.id.profile);
        tickets=findViewById(R.id.tickets);
        history=findViewById((R.id.history));
        membership=findViewById(R.id.membership);
        settings=findViewById(R.id.settings);
        logout=findViewById(R.id.logout);
        profile.setOnClickListener(this);
        tickets.setOnClickListener(this);
        membership.setOnClickListener(this);
        settings.setOnClickListener(this);
        history.setOnClickListener(this);
        logout.setOnClickListener(this);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            user_name=cursor.getString(cursor.getColumnIndex("Username"));

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_vip_level").post(formBuilder.build()).build();
            final Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, final IOException e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            showToast("Can not connect to networks！");
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException
                {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                JSONObject res_inform = new JSONObject(res);
                                String gender_text = res_inform.getString("gender");
                                String vip_level_text = res_inform.getString("vip_level");
                                if(!res_inform.getString("user_avatar").equals("null"))
                                {
                                    String user_avatar_uri="http://nightmaremlp.pythonanywhere.com/img/"+res_inform.getString("user_avatar");
                                    userAvatar.setImageURI(Uri.parse(user_avatar_uri));
                                }
                                String username_html;
                                if(gender_text.equals("null"))
                                    username.setText(user_name);
                                else{
                                    if(gender_text.equals("male"))
                                        username_html = user_name + "  " + "<img src='" + R.drawable.male + "'>";
                                    else
                                        username_html = user_name + "  " + "<img src='" + R.drawable.female + "'>";
                                    username.setText(Html.fromHtml(username_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 60 , 60);
                                            return drawable;
                                        }
                                    }, null));
                                }
                                if(!vip_level_text.equals("0"))
                                {
                                    String vip_html="<img src='" + R.drawable.icon_vip + "'>";
                                    vip_level.setText(Html.fromHtml(vip_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 120 , 80);
                                            return drawable;
                                        }
                                    }, null));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("User");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.profile:
                intent = new Intent(User_page_activity.this,Profile_page_activity.class);
                startActivity(intent);
                break;
            case R.id.membership:
                intent = new Intent(User_page_activity.this,Membership_page_activity.class);
                startActivity(intent);
                break;
            case R.id.tickets:
                intent = new Intent(User_page_activity.this,Order_page_activity.class);
                startActivity(intent);
                break;
            case R.id.history:
                intent = new Intent(User_page_activity.this,History_activity.class);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(User_page_activity.this,Setting_page_activity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SQLiteDatabase db = androidDatabase.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
                if(cursor.moveToFirst()){
                    ContentValues value = new ContentValues();
                    value.put("Islogin",0);
                    db.update("User",value,"Username=?", new String[]{cursor.getString(cursor.getColumnIndex("Username"))});
                }
                finish();
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

    private void showToast(String str) {
        Toast.makeText(User_page_activity.this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Tag,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Tag,"onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            user_name=cursor.getString(cursor.getColumnIndex("Username"));

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_vip_level").post(formBuilder.build()).build();
            final Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, final IOException e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            showToast("Can not connect to networks！");
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException
                {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                JSONObject res_inform = new JSONObject(res);
                                String gender_text = res_inform.getString("gender");
                                String vip_level_text = res_inform.getString("vip_level");
                                if(!res_inform.getString("user_avatar").equals("null"))
                                {
                                    String user_avatar_uri="http://nightmaremlp.pythonanywhere.com/img/"+res_inform.getString("user_avatar");
                                    userAvatar.setImageURI(Uri.parse(user_avatar_uri));
                                }
                                String username_html;
                                if(gender_text.equals("null"))
                                    username.setText(user_name);
                                else{
                                    if(gender_text.equals("male"))
                                        username_html = user_name + "  " + "<img src='" + R.drawable.male + "'>";
                                    else
                                        username_html = user_name + "  " + "<img src='" + R.drawable.female + "'>";
                                    username.setText(Html.fromHtml(username_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 60 , 60);
                                            return drawable;
                                        }
                                    }, null));
                                }
                                if(!vip_level_text.equals("0"))
                                {
                                    String vip_html="<img src='" + R.drawable.icon_vip + "'>";
                                    vip_level.setText(Html.fromHtml(vip_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 120 , 80);
                                            return drawable;
                                        }
                                    }, null));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
        Log.d(Tag,"onRestart");
    }
}
