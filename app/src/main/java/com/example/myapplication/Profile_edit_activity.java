package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

public class Profile_edit_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Profile_edit_activity";
    public static final int CHOOSE_PHOTO=2;

    private SimpleDraweeView userAvatar;
    private Button editUserAvatar;
    private EditText newUsername;
    private CheckBox checkMale;
    private CheckBox checkFemale;
    private EditText newAge;
    private EditText newEmail;
    private EditText newPhonenumber;
    private Button profileConfirmButton;
    private String loginUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_layout);
        Intent intent=getIntent();
        userAvatar=findViewById(R.id.edit_userAvatar);
        editUserAvatar=findViewById(R.id.edit_userAvatar_button);
        editUserAvatar.setOnClickListener(this);
        newUsername=findViewById(R.id.profile_new_username);
        checkMale=findViewById(R.id.profile_gender_male);
        checkFemale=findViewById(R.id.profile_gender_female);
        newAge=findViewById(R.id.profile_new_age);
        newEmail=findViewById(R.id.profile_new_email);
        newPhonenumber=findViewById(R.id.profile_new_phonenumber);
        profileConfirmButton=findViewById(R.id.profileConfirmButton);

        if(!intent.getStringExtra("user_avatar_uri").isEmpty())
            userAvatar.setImageURI(Uri.parse(intent.getStringExtra("user_avatar_uri")));
        String gender_html;
        gender_html = "<img src='" + R.drawable.male + "'>";
        checkMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkFemale.setChecked(false);
                }
            }
        });
        checkMale.setText(Html.fromHtml(gender_html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);
                Drawable drawable = getResources().getDrawable(id, null);
                drawable.setBounds(0, 0, 60 , 60);
                return drawable;
            }
        }, null));
        gender_html = "<img src='" + R.drawable.female + "'>";
        checkFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkMale.setChecked(false);
                }
            }
        });
        checkFemale.setText(Html.fromHtml(gender_html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);
                Drawable drawable = getResources().getDrawable(id, null);
                drawable.setBounds(0, 0, 60 , 60);
                return drawable;
            }
        }, null));
        loginUsername=intent.getStringExtra("username");
        newUsername.setText(loginUsername);
        if(!intent.getStringExtra("gender").equals("null"))
        {
            if(intent.getStringExtra("gender").equals("male"))
                checkMale.setChecked(true);
            else
                checkFemale.setChecked(true);
        }
        if(!intent.getStringExtra("age").equals("null"))
            newAge.setText(intent.getStringExtra("age"));
        if(!intent.getStringExtra("email").equals("null"))
            newEmail.setText(intent.getStringExtra("email"));
        if(!intent.getStringExtra("phonenumber").equals("null"))
            newPhonenumber.setText((intent.getStringExtra("phonenumber")));
        profileConfirmButton.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit profile");
    }

    private String username;
    private String gender;
    private String age;
    private String email;
    private String phonenumber;

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profileConfirmButton:
                username=newUsername.getText().toString();
                gender="";
                if(checkMale.isChecked())
                    gender="male";
                else if(checkFemale.isChecked())
                    gender="female";
                age=newAge.getText().toString();
                email=newEmail.getText().toString();
                phonenumber=newPhonenumber.getText().toString();
                if(username.equals("")||gender.equals("")||age.equals("")||email.equals("")||phonenumber.equals(""))
                {
                    showToast("Information can't be empty!");
                    return;
                }
                if(!isEmail(email))
                {
                    showToast("Email not available!");
                    return;
                }
                if(!checkAge(age))
                {
                    showToast("Age can only be in range 1-100!");
                    return;
                }
                if(!checkPhonenumber(phonenumber))
                {
                    showToast("Phonenumber not available!");
                    return;
                }

                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("loginUsername", loginUsername);
                formBuilder.add("username", username);
                formBuilder.add("gender", gender);
                formBuilder.add("age", age);
                formBuilder.add("email", email);
                formBuilder.add("phonenumber", phonenumber);
                Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/edit_profile").post(formBuilder.build()).build();
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
                                    {
                                        Intent intent=new Intent();
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.edit_userAvatar_button:
                openAlbum();
                break;
            default:
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

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(data!=null)
                {
                    Log.d(Tag,data.getData().getPath());
                    userAvatar.setImageURI(data.getData());
                }
                else
                    showToast("Please choose a photo!");
            default:
                break;
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

    private boolean checkAge(String flag_age)
    {
        try {
            if(Integer.valueOf(flag_age).intValue()>0&&Integer.valueOf(flag_age).intValue()<=100)
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkPhonenumber(String flag_phonenumber)
    {
        for(int i=0;i<flag_phonenumber.length();i++)
        {
            if(flag_phonenumber.charAt(i)-'0'>9||flag_phonenumber.charAt(i)-'0'<0)
                return false;
        }
        return true;
    }

    private void showToast(String str) {
        Toast.makeText(Profile_edit_activity.this,str,Toast.LENGTH_SHORT).show();
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
