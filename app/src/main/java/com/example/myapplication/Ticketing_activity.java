package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Ticketing_activity extends AppCompatActivity {
    public static List<Adapater_common_type> seat_info_list = new ArrayList<Adapater_common_type>();
    public static List<Integer> seat_nums = new ArrayList<Integer>();
    List<Adapater_common_type> seat_list = new ArrayList<Adapater_common_type>();
    public static String price;
    public static TextView priceText;
    public String id;
    public String user_name;
    private Button button;
    FragmentRecyclerAdapter mAdapter;
    FragmentRecyclerAdapter mAdapter2;
    String serial_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketing_activity);
        Intent intent = getIntent();
        final String movie_name = intent.getStringExtra("movie_name");
        price = intent.getStringExtra("movie_price");
        id=intent.getStringExtra("movie_id");

        TextView name=findViewById(R.id.ticketing_movie_name);
        name.setText(movie_name);
        TextView type=findViewById(R.id.ticketing_movie_type);
        type.setText(intent.getStringExtra("movie_type")+" "+intent.getStringExtra("type"));
        Log.d("test2", movie_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        AndroidDatabase androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst())
        {
            user_name = cursor.getString(cursor.getColumnIndex("Username"));
        }

        priceText = (TextView) findViewById(R.id.price);

        set_serial_number();
        Set_seat(serial_number);
        RecyclerView mRecyclerView;

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_for_ticketing);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);

        mAdapter = new FragmentRecyclerAdapter(Ticketing_activity.this, seat_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        RecyclerView mRecyclerView2;
        mRecyclerView2 = (RecyclerView) findViewById(R.id.seat_info_recycler_view);
        StaggeredGridLayoutManager mLayoutManager2 = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        mAdapter2 = new FragmentRecyclerAdapter(Ticketing_activity.this, seat_info_list);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        mRecyclerView2.setAdapter(mAdapter2);
        mAdapter.getAdapter(mAdapter2);

        button=(Button)findViewById(R.id.buy_tickets_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seat_nums.size()!=0)
                Request_seat();
                view.setEnabled(false);
            }
        });
    }

    public void Set_seat(String serial_number)
    {
        for (int i = 0; i < 40; i++) {
            boolean selected = false;
            if (serial_number.charAt(i) == '1')
                selected = true;
            Adapter_seat seat = new Adapter_seat(i, selected, false);
            seat_list.add(seat);
        }
    }
    public void set_serial_number()
    {
        AndroidDatabase androidDatabase = new AndroidDatabase(Ticketing_activity.this, "Shield.db", null, 1);
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Movie WHERE movie_id=?", new String[]{id});
        if(cursor.moveToFirst())
            serial_number=cursor.getString(cursor.getColumnIndex("serial_number"));
    }

    public void  Request_seat() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        int i = 0;
        String nums = "";
        for (Integer num : seat_nums) {
            if (i == 0) {
                nums += num;
                i = 1;
            } else
                nums += "." + num;
        }
        formBuilder.add("seat_nums", nums);
        formBuilder.add("id", id);
        formBuilder.add("user_name", user_name);
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/select_seat").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());
                        Toast error_toast = Toast.makeText(Ticketing_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                        button.setEnabled(true);
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        button.setEnabled(true);
                        try {
                            final String res = response.body().string();
                            JSONObject res_inform = null;
                            res_inform = new JSONObject(res);
                            String result = res_inform.getString("result");
                            String serial_number = res_inform.getString("serial_number");
                            String error_code= res_inform.getString("errorcode");

                            if(error_code.equals("0")) {
                                AndroidDatabase androidDatabase = new AndroidDatabase(Ticketing_activity.this, "Shield.db", null, 1);
                                final SQLiteDatabase db = androidDatabase.getWritableDatabase();


                                ContentValues values = new ContentValues();
                                values.put("serial_number", serial_number);
                                db.update("Movie", values, "movie_id=?",
                                        new String[]{id});
                                seat_list.clear();
                                seat_nums.clear();
                                seat_info_list.clear();
                                Set_seat(serial_number);
                                mAdapter.notifyDataSetChanged();
                                mAdapter2.notifyDataSetChanged();

                                Toast error_toast = Toast.makeText(Ticketing_activity.this, result, Toast.LENGTH_LONG);
                                error_toast.setGravity(Gravity.CENTER, 0, 0);
                                error_toast.show();
                            }
                            else {
                                String nums2 = "";
                                int i2 = 0;
                                for (Integer num : seat_nums) {
                                    if (i2 == 0) {
                                        nums2 += num;
                                        i2 = 1;
                                    } else
                                        nums2 += "." + num;
                                }

                                Intent intent = new Intent(Ticketing_activity.this, Dialog_activity.class);
                                intent.putExtra("seat_nums", nums2);
                                intent.putExtra("id", id);
                                intent.putExtra("price", res_inform.getString("total_price"));
                                intent.putExtra("balance", res_inform.getString("balance"));
                                intent.putExtra("vip_level", res_inform.getString("vip_level"));
                                intent.putExtra("preferential_account", res_inform.getString("preferential_account"));
                                intent.putExtra("nums", String.valueOf(seat_nums.size()));

                                Ticketing_activity.this.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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
                seat_info_list.clear();
                seat_nums.clear();
                seat_list.clear();
                mAdapter.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        seat_info_list.clear();
        seat_nums.clear();
        seat_list.clear();
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        seat_info_list.clear();
        seat_nums.clear();
        seat_list.clear();
        set_serial_number();
        Set_seat(serial_number);
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        seat_info_list.clear();
        seat_nums.clear();
        seat_list.clear();
        set_serial_number();
        Set_seat(serial_number);
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
        Log.d("tset","onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        set_serial_number();
    }
}
