package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class Topup_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Topup_page_activity";
    private CheckBox topup_amount100;
    private CheckBox topup_amount200;
    private CheckBox topup_amount500;
    private Button topupConfirmButton;
    private String login_username;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup_page_layout);

        topup_amount100=findViewById(R.id.topupAmount100);
        topup_amount200=findViewById(R.id.topupAmount200);
        topup_amount500=findViewById(R.id.topupAmount500);
        topup_amount100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    topup_amount200.setChecked(false);
                    topup_amount500.setChecked(false);
                }
            }
        });
        topup_amount200.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    topup_amount100.setChecked(false);
                    topup_amount500.setChecked(false);
                }
            }
        });
        topup_amount500.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    topup_amount100.setChecked(false);
                    topup_amount200.setChecked(false);
                }
            }
        });
        topupConfirmButton=findViewById(R.id.topupConfirmButton);
        topupConfirmButton.setOnClickListener(this);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst())
            login_username=cursor.getString(cursor.getColumnIndex("Username"));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Top up");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.topupConfirmButton:
                if(!(topup_amount100.isChecked()||topup_amount200.isChecked()||topup_amount500.isChecked()))
                {
                    showToast("Please choose an amount to top up!");
                    return;
                }

                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("username", login_username);
                if(topup_amount100.isChecked())
                    formBuilder.add("amountCode", "1");
                else if(topup_amount200.isChecked())
                    formBuilder.add("amountCode", "2");
                else
                    formBuilder.add("amountCode", "3");
                Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/top_up").post(formBuilder.build()).build();
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
                                showToast("Can't connect to networks！");
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
                                    String flag = res_inform.getString("Flag");
                                    if(flag.equals("1"))
                                        finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
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
        Toast.makeText(Topup_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
        topup_amount100.setChecked(false);
        topup_amount200.setChecked(false);
        topup_amount500.setChecked(false);
        Log.d(Tag,"onRestart");
    }
}
