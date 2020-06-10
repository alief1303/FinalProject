package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

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

public class Order_page_activity extends AppCompatActivity {
    SearchView mSearchView;
    private AndroidDatabase androidDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_page_activity);
        Fresco.initialize(Order_page_activity.this);
        androidx.appcompat.widget.Toolbar toolbar=(androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tickets");
        RequestFororderInform(0);
    }
    GridLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    WaterFallAdapter mAdapter;
    private void init(List<Adapater_common_type> list) {

        mRecyclerView = (RecyclerView) findViewById(R.id.order_recycler_view);
        mLayoutManager = new GridLayoutManager(Order_page_activity.this,1);
        mAdapter = new WaterFallAdapter(Order_page_activity.this, list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    public List<Adapater_common_type> RequestFororderInform(final int mode)  {
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
        formBuilder.add("User_name", user_name);
       // formBuilder.add("key_word", Login_page_activity.password);
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_orders").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());
                        Toast error_toast = Toast.makeText(Order_page_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                        RequestFororderInform(0);
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

                            Cursor cursor = db.rawQuery("select * from Orders", new String[]{});
                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int[] arr = new int[200];
                                if(cursor.getCount()!=0) {

                                    if(cursor.moveToFirst()) {
                                        do {
                                            arr[cursor.getInt(cursor.getColumnIndex("order_id"))]=1;
                                        }
                                        while(cursor.moveToNext());

                                    }
                                }
                                if (arr[jsonObject.getInt("order_id")]!=1) {
                                    Log.d("okhttp_error", String.valueOf(jsonObject.getInt("order_id")) + " " + cursor.getCount());
                                    db.execSQL("insert into Orders values(?,?,?,?,?,?,?)"
                                            , new Object[]{jsonObject.getInt("order_id"), jsonObject.getString("order_date"),
                                                    jsonObject.getString("seat_number"), jsonObject.getString("ticket_key"),
                                                    jsonObject.getString("order_user"),jsonObject.getInt("comments"),
                                                    jsonObject.getString("order_movie")


                                            });
                                }
                            }

                            Cursor cursor2;

                            final SQLiteDatabase db1 = androidDatabase.getWritableDatabase();

                            cursor = db1.rawQuery("select * from Orders", new String[]{});
                            if(cursor.moveToFirst()) {
                                do {
                                    Log.d("okhttp_error",cursor.getString(cursor.getColumnIndex("ticket_key")) );
                                    Date date=new Date();
                                    SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                                    String compare=sf.format(date);
                                    cursor2 = db1.rawQuery("select * from Movie where movie_id=? and whole_time > ? group by movie_name", new String[]{cursor.getString(cursor.getColumnIndex("order_movie")),compare});
                                    if(cursor2.getCount()>0)
                                    if(cursor2.moveToFirst()) {
                                        Adapter_order movie_order = new Adapter_order();
                                        Log.d("okhttp_error", cursor.getString(cursor.getColumnIndex("ticket_key")));
                                        movie_order.date = cursor.getString(cursor.getColumnIndex("order_date"));
                                        movie_order.seat = cursor.getString(cursor.getColumnIndex("seat_number"));
                                        movie_order.code = cursor.getString(cursor.getColumnIndex("ticket_key"));
                                        movie_order.hall= cursor2.getString(cursor2.getColumnIndex("projection_hall"));
                                        movie_order.movie = cursor2.getString(cursor2.getColumnIndex("movie_name"));
                                        movie_order.time = cursor2.getString(cursor2.getColumnIndex("start_time"))+" - "+cursor2.getString(cursor2.getColumnIndex("finish_time"));
                                        movie_order.img_url=cursor2.getString(cursor2.getColumnIndex("img_url"));
                                        movie_order.id=cursor.getString(cursor.getColumnIndex("order_movie"));
                                        movie_order.watched=0;
                                        if (mode == 0)
                                            list.add(movie_order);
                                        else {
                                            mAdapter.mData.add(movie_order);
                                            mAdapter.notifyItemChanged(mAdapter.mData.size());
                                            mAdapter.notifyItemRangeChanged(mAdapter.mData.size(), 1);
                                        }
                                    }

                                }
                                while(cursor.moveToNext());

                            }
                            if (mode == 0)
                                init(list);

                            Log.d("okhttp_error", "end of thread");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }

        });


        return list;

    }
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
