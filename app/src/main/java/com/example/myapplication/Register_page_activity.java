package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

public class Register_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Register_page_activity";
    private EditText inputAccount;
    private EditText inputPassword;
    private EditText reInputPassword;
    private EditText inputEmail;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_layout);
        inputAccount=findViewById(R.id.account_input_text);
        inputPassword=findViewById(R.id.password_input_text);
        reInputPassword=findViewById(R.id.re_password_input_text);
        inputEmail=findViewById(R.id.email_input_text);
        registerButton=findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        reInputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("User Register");
    }

    @Override
    public void onClick(View v) {
        String account = inputAccount.getText().toString();
        String passWord = inputPassword.getText().toString();
        String rePassWord = reInputPassword.getText().toString();
        String email = inputEmail.getText().toString();

        switch (v.getId())
        {
            case R.id.registerButton:
                if(account.equals("")||passWord.equals(""))
                {
                    showToast("Username or password can't be empty！");
                    return;
                }
                else if(!passWord.equals(rePassWord))
                {
                    showToast("Confirm password not the same!");
                    return;
                }
                else if(!isEmail(email))
                {
                    showToast("Email not available!");
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("username", account);
                formBuilder.add("password", passWord);
                formBuilder.add("email", email);
                Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/register").post(formBuilder.build()).build();
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
                                    if(error_code.equals("2"))
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

    private boolean isEmail(String flag_email)
    {
        boolean flag=false;
        for(int i=0;i<flag_email.length();i++)
        {
            if(!flag&&flag_email.charAt(i)=='@')
                flag=true;
            else if(flag&&flag_email.charAt(i)=='@')
            {
                flag=false;
                break;
            }
        }
        return flag;
    }

    private void showToast(String str) {
        Toast.makeText(Register_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
