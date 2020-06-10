package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Password_change_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Password_change_page";
    private EditText oldPassword;
    private EditText newPassword;
    private EditText reNewPassword;
    private Button passwordChangeButton;
    private String login_username;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_page_layout);

        oldPassword=findViewById(R.id.old_password_input_text);
        newPassword=findViewById(R.id.new_password_input_text);
        reNewPassword=findViewById(R.id.re_new_input_text);
        oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        reNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordChangeButton=findViewById(R.id.passwordChangeButton);
        passwordChangeButton.setOnClickListener(this);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst())
            login_username=cursor.getString(cursor.getColumnIndex("Username"));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Change password");
    }

    @Override
    public void onClick(View v) {
        String oldPassWordText = oldPassword.getText().toString();
        String newPassWordText = newPassword.getText().toString();
        String reNewPasswordText = reNewPassword.getText().toString();

        switch (v.getId())
        {
            case R.id.passwordChangeButton:
                if(!reNewPasswordText.equals(newPassWordText))
                {
                    showToast("New password not match!");
                    break;
                }
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("username", login_username);
                formBuilder.add("oldPassword", oldPassWordText);
                formBuilder.add("newPassword", newPassWordText);
                Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/change_password").post(formBuilder.build()).build();
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
                                    String message = res_inform.getString("message");
                                    String error_code = res_inform.getString("error_code");
                                    showToast(message);
                                    if(error_code.equals("1"))
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
        Toast.makeText(Password_change_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
