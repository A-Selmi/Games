package com.abdullah.games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ListView listView;
    String TAG, s;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ProgressBar progressBar;
    TextView textView, textView1;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.Requests);
    }

    public void RequestsButton(View view) {
        Intent intent = new Intent(getBaseContext(), RequestsDetails.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Load();
    }

    private void Load() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.FailTextView);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.Requests);
        button.setVisibility(View.GONE);
        textView1 = findViewById(R.id.textViewmain);
        textView1.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        listView = (ListView) findViewById(R.id.GamesListView);
        arrayList = new ArrayList<String>();
        listView.setAdapter(null);

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            db.collection("Games")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("لا يوجد سجل ألعاب متاح");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    textView.setVisibility(View.GONE);
                                    arrayList.add(document.getData().get("name").toString());
                                }
                                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        TextView tv = (TextView) super.getView(position, convertView, parent);
                                        tv.setGravity(Gravity.CENTER);
                                        return tv;
                                    }
                                };
                                listView.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                button.setVisibility(View.VISIBLE);
                                textView1.setVisibility(View.VISIBLE);
                            } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("فشل في تحميل البيانات");
                                progressBar.setVisibility(View.GONE);
                                button.setVisibility(View.VISIBLE);
                                textView1.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }else {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("الرجاء التحقق من الإتصال بالإنترنت");
            button.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                s = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), GameDetails.class);
                intent.putExtra("GameName", s);
                startActivity(intent);
            }
        });

    }
}
