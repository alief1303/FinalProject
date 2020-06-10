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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class Membership_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Membership_activity";
    private SimpleDraweeView userAvatar;
    private TextView username;
    private TextView vip_level;
    private TextView balance;
    private TextView progress_label;
    private TextView membership_message;
    private TextView vip1_label;
    private TextView vip2_label;
    private ProgressBar membership_progress;
    private Button topup_button;
    private AndroidDatabase androidDatabase;

    private String user_name;
    private String str_gender;
    private String str_vip_level;
    private int balance_amount;
    private int accumulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membership_page_layout);

        userAvatar=findViewById(R.id.membership_user_avatar);
        username=findViewById(R.id.membership_username);
        vip_level=findViewById(R.id.membership_vip_level);
        balance=findViewById(R.id.membership_balance);
        membership_progress=findViewById(R.id.membership_progressbar);
        vip1_label=findViewById(R.id.vip1_label);
        vip2_label=findViewById(R.id.vip2_label);
        progress_label=findViewById(R.id.membership_progresslabel);
        membership_message=findViewById(R.id.membership_message);
        topup_button=findViewById(R.id.membership_topup);
        topup_button.setOnClickListener(this);

        int screen_width = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)vip1_label.getLayoutParams();
        params.leftMargin=(screen_width-60)/5+10;
        vip1_label.setLayoutParams(params);
        ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams)vip2_label.getLayoutParams();
        params2.leftMargin=(screen_width-60)/5*2;
        vip2_label.setLayoutParams(params2);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            user_name=cursor.getString(cursor.getColumnIndex("Username"));

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_membership").post(formBuilder.build()).build();
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
                                str_gender = res_inform.getString("gender");
                                str_vip_level = res_inform.getString("vip_level");
                                balance_amount = res_inform.getInt("balance");
                                accumulation = res_inform.getInt("accumulation");
                                if(!res_inform.getString("user_avatar").equals("null"))
                                {
                                    String user_avatar_uri=""+res_inform.getString("user_avatar");
                                    userAvatar.setImageURI(Uri.parse(user_avatar_uri));
                                }
                                String username_html;
                                if(str_gender.equals("null"))
                                    username.setText(user_name);
                                else{
                                    if(str_gender.equals("male"))
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
                                if(!str_vip_level.equals("0"))
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
                                    String discount="";
                                    switch (str_vip_level)
                                    {
                                        case "1": discount="10%"; break;
                                        case "2": discount="20%"; break;
                                        case "3": discount="30%"; break;
                                    }
                                    membership_message.setText("You are VIP"+str_vip_level+" now. All tickets enjoy "+
                                            discount+" discount !");
                                }
                                balance.setText("Rp"+Integer.toString(balance_amount));
                                if(accumulation>=500)
                                    membership_progress.setProgress(100);
                                else
                                    membership_progress.setProgress(accumulation/5);
                                progress_label.setText("Rp."+Integer.toString(accumulation)+"/500+");
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
        actionBar.setTitle("Membership");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.membership_topup:
                Intent intent = new Intent(Membership_page_activity.this,Topup_page_activity.class);
                startActivity(intent);
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
        Toast.makeText(Membership_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            user_name=cursor.getString(cursor.getColumnIndex("Username"));

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_membership").post(formBuilder.build()).build();
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
                                str_gender = res_inform.getString("gender");
                                str_vip_level = res_inform.getString("vip_level");
                                balance_amount = res_inform.getInt("balance");
                                accumulation = res_inform.getInt("accumulation");
                                if(!res_inform.getString("user_avatar").equals("null"))
                                {
                                    String user_avatar_uri="http://nightmaremlp.pythonanywhere.com/img/"+res_inform.getString("user_avatar");
                                    userAvatar.setImageURI(Uri.parse(user_avatar_uri));
                                }
                                String username_html;
                                if(str_gender.equals("null"))
                                    username.setText(user_name);
                                else{
                                    if(str_gender.equals("male"))
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
                                if(!str_vip_level.equals("0"))
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
                                    String discount="";
                                    switch (str_vip_level)
                                    {
                                        case "1": discount="10%"; break;
                                        case "2": discount="20%"; break;
                                        case "3": discount="30%"; break;
                                    }
                                    membership_message.setText("You are VIP"+str_vip_level+" now. All tickets enjoy "+
                                            discount+" discount !");
                                }
                                balance.setText("Rp"+Integer.toString(balance_amount));
                                if(accumulation>=500)
                                    membership_progress.setProgress(100);
                                else
                                    membership_progress.setProgress(accumulation/5);
                                progress_label.setText("Rp"+Integer.toString(accumulation)+"/500+");
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
