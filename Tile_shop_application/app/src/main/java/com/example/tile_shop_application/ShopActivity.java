package com.example.tile_shop_application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ShopActivity extends AppCompatActivity {
    private final static String LOG_TAG = ShopActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mItemsList;
    private ShoppingItemAdapter mAdapter;

    private int queryLimit = 100;
    private FrameLayout redCircle;
    private TextView countTextView;
    private int cartItemsCount = 0;
    private ProductFirebase mProductFirebase;

    private NotificationHandler mNotificationHandler;
    private AlarmManager mAlarmManager;
    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainShop), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(LOG_TAG, "Authentikálatlan felhasználó!");
            finish();
        } else {
            Log.d(LOG_TAG, "Authentikált felhasználó!");
        }
        mProductFirebase = new ProductFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Csempe app");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemsList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemsList);
        mRecyclerView.setAdapter(mAdapter);

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

//        mNotificationHandler = new NotificationHandler(this);
//        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        setAlarmManager();

        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();
    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 100;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 50;
                    queryData();
                    break;
            }
        }
    };

    private void queryData() {
        mItemsList.clear();

        mProductFirebase.getAllProductsOrdered(queryLimit, task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    mItemsList.add(document.toObject(Product.class));
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e(LOG_TAG, "Hiba a termékek lekérésekor: " + exception.getMessage());
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            Toast.makeText(this, "Kijelentkezés...", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.settings_button) {
            Log.d(LOG_TAG, "Settings clicked!");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.cart) {
            Log.d(LOG_TAG, "Cart clicked!");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        cartItemsCount += 1;

        if (cartItemsCount > 0) {
            countTextView.setText(String.valueOf(cartItemsCount));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItemsCount > 0) ? View.VISIBLE : View.GONE);
    }


    private void setAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);
    }

    private void setJobScheduler() {
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadLine = 5000;

        ComponentName componentName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, componentName)
                .setRequiredNetworkType(networkType)
                .setOverrideDeadline(hardDeadLine);

        mJobScheduler.schedule(builder.build());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}