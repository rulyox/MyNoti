package zyon.notifier;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Activity_Menu_Info extends AppCompatActivity {

    // 리싸이클러뷰
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<List_Info> mArrayList;
    Adapter_Info mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerReceiver(finishActivity, new IntentFilter("FINISH_ACTIVITY"));

        setList();

    }

    // 메뉴
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(finishActivity);

    }

    private final BroadcastReceiver finishActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finishAffinity();
        }
    };

    void setList(){

        mRecyclerView = findViewById(R.id.list_info_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mArrayList = new ArrayList<>();
        mAdapter = new Adapter_Info(mArrayList, this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        List_Info item1 = new List_Info(getResources().getDrawable(R.drawable.info_app), getString(R.string.app_name), getString(R.string.app_ver));
        List_Info item2 = new List_Info(getResources().getDrawable(R.drawable.info_zyon), getString(R.string.info_developer), getString(R.string.info_zyon));
        List_Info item3 = new List_Info(getResources().getDrawable(R.drawable.info_mail), getString(R.string.info_contact), getString(R.string.info_email));
        mArrayList.add(item1);
        mArrayList.add(item2);
        mArrayList.add(item3);

        mAdapter.notifyDataSetChanged();

    }

    void clickList(int pos){

        if(pos == 2){

            Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zyon3075@naver.com"));

            // 이메일 앱 검사
            ComponentName emailApp = email.resolveActivity(getPackageManager());
            ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
            if (emailApp != null && !emailApp.equals(unsupportedAction)) {

                try {

                    Intent chooser = Intent.createChooser(email, getString(R.string.info_send));
                    startActivity(chooser);

                } catch (ActivityNotFoundException ignored) { }

            }
            else Toast.makeText (Activity_Menu_Info.this, getString(R.string.alert_noemail), Toast.LENGTH_LONG).show();

        }

    }

}