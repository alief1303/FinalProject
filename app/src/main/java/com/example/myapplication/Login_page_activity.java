package com.example.myapplication;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



import okhttp3.Call;

import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.Response;

import okhttp3.*;

public class Login_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Login_page_activity";
    private EditText inputAccount;
    private EditText inputPassword;
    private CheckBox passIsRemembered;
    private Button loginButton;
    private Button registerButton;
    private AndroidDatabase androidDatabase;
    public static String password=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_layout);
        loginButton=findViewById(R.id.loginButton);
        registerButton=findViewById(R.id.registerButton);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        inputAccount=findViewById(R.id.account_input_text);
        inputPassword=findViewById(R.id.password_input_text);
        passIsRemembered=findViewById(R.id.remember_pass);
        inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("User Login");

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where PasswordIsRemembered=?",new String[]{"1"});
        if(cursor.moveToFirst())
        {
            String account = cursor.getString(cursor.getColumnIndex("Username"));
            inputAccount.setText(account);
            inputPassword.setText("password");
            passIsRemembered.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        final String account = inputAccount.getText().toString();
        final String passWord = inputPassword.getText().toString();
        password=passWord;
        final int passRemembered;
        if(passIsRemembered.isChecked())
            passRemembered=1;
        else
            passRemembered=0;

        switch (v.getId())
        {
            case R.id.loginButton:
                if(account.equals("")||passWord.equals(""))
                {
                    showToast("Username or password can't be empty！");
                    return;
                }
                boolean flag=true;
                if(passRemembered==1)
                {
                    SQLiteDatabase db = androidDatabase.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select * from User where Username=?",new String[]{account});
                    if(cursor.moveToFirst()&&cursor.getInt(cursor.getColumnIndex("PasswordIsRemembered"))==1&&passWord.equals("password"))
                    {
                        ContentValues value = new ContentValues();
                        value.put("Islogin",1);
                        value.put("PasswordIsRemembered",passRemembered);
                        db.update("User",value,"Username=?", new String[]{account});
                        showToast("Log in successfully!");
                        flag=false;
                    }
                }
                if(flag)
                {
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    formBuilder.add("username", account);
                    formBuilder.add("password", passWord);
                    Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/login").post(formBuilder.build()).build();
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
                                        String message = res_inform.getString("message");
                                        String error_code = res_inform.getString("error_code");
                                        showToast(message);
                                        if(error_code.equals("2"))
                                        {
                                            SQLiteDatabase db = androidDatabase.getWritableDatabase();
                                            Cursor cursor = db.rawQuery("select * from User where Username=?",new String[]{account});
                                            db.execSQL("DELETE FROM Orders");
                                            if(cursor.getCount()==0)
                                                db.execSQL("insert into User values(?,?,?,?)",new Object[] {
                                                        null,
                                                        account,
                                                        passRemembered,
                                                        1 });
                                            else{
                                                ContentValues value = new ContentValues();
                                                if(passRemembered==1)
                                                {
                                                    value.put("PasswordIsRemembered",0);
                                                    db.update("User",value,"PasswordIsRemembered=?", new String[]{"1"});
                                                    value.clear();
                                                }
                                                value.put("PasswordIsRemembered",passRemembered);
                                                value.put("Islogin",1);
                                                db.update("User",value,"Username=?", new String[]{account});
                                            }
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
                else
                    finish();
                break;
            case R.id.registerButton:
                Intent intent = new Intent(Login_page_activity.this,Register_page_activity.class);
                startActivity(intent);
                break;
        }
    }

    private void showToast(String str) {
        Toast.makeText(Login_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
        Log.d(Tag,"onRestart");
    }
}

