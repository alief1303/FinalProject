package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Setting_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Setting_page_activity";
    private TextView currentVersion;
    private TextView latestVersion;
    private TextView website;
    private Button passwordChangeButton;
    private String user_name;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page_layout);

        currentVersion=findViewById(R.id.current_version);
        latestVersion=findViewById(R.id.latest_version);
        website=findViewById(R.id.setting_website);
        passwordChangeButton=findViewById(R.id.password_change_button);
        passwordChangeButton.setOnClickListener(this);

        final SpannableStringBuilder style = new SpannableStringBuilder();

        style.append("");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Uri uri = Uri.parse("");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        };
        style.setSpan(clickableSpan,0,39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
        style.setSpan(foregroundColorSpan, 0, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        website.setMovementMethod(LinkMovementMethod.getInstance());
        website.setText(style);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.query("version",new String[]{"current_version"},null,null,null,null,null);
        cursor.moveToFirst();
        currentVersion.setText(cursor.getString(cursor.getColumnIndex("current_version")));
        cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        cursor.moveToFirst();
        user_name=cursor.getString(cursor.getColumnIndex("Username"));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_version").get().build();
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
                            latestVersion.setText(res_inform.getString("version_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Settings");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.password_change_button:
                Intent intent = new Intent(Setting_page_activity.this,Password_change_page_activity.class);
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
        Toast.makeText(Setting_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_version").get().build();
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
                            latestVersion.setText(res_inform.getString("version_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        Log.d(Tag,"onRestart");
    }
}
