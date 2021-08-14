package com.abdullah.games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestsDetails extends AppCompatActivity {

    FirebaseFirestore db;
    ListView listView;
    String TAG;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ProgressBar progressBar;
    TextView textView, textView2;
    Button button1, button2, button3;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_details);

        listView = (ListView) findViewById(R.id.RequestsDetailsListView);
        button1 = (Button) findViewById(R.id.EditButton);
        button2 = (Button) findViewById(R.id.AddButton);
        button3 = (Button) findViewById(R.id.cancleButton2);
        textView2 = (TextView) findViewById(R.id.RequestsTextView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(button3.getVisibility() == View.VISIBLE) {
                        Intent intent = new Intent(getBaseContext(), RequestsStateDetails.class);
                        intent.putExtra("name", parent.getItemAtPosition(position).toString());
                        intent.putExtra("statename", "EDITR");
                        startActivity(intent);
                    }

                }
            });
        }

    private void Load(final int n) {
        progressBar = (ProgressBar) findViewById(R.id.RequestsStateprogressBar);
        textView = (TextView) findViewById(R.id.RequestsFailTextView);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        button1 = (Button) findViewById(R.id.EditButton);
        button2 = (Button) findViewById(R.id.AddButton);
        button3 = (Button) findViewById(R.id.cancleButton2);
        textView2 = (TextView) findViewById(R.id.RequestsTextView);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        listView = (ListView) findViewById(R.id.RequestsDetailsListView);
        arrayList = new ArrayList<String>();
        listView.setAdapter(null);

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            db.collection("Requests Details")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("لا توجد أي طلبات حاليا");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    textView.setVisibility(View.GONE);
                                    arrayList.add("أنا - "+document.getData().get("name").toString()+" - أرغب في لعب لعبة - "
                                            + document.getData().get("game").toString() + " -");
                                }
                                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        TextView tv = (TextView) super.getView(position, convertView, parent);
                                        tv.setGravity(Gravity.START|Gravity.CENTER);
                                        return tv;
                                    }
                                };
                                listView.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                if(n == 1) {
                                    button1.setVisibility(View.GONE);
                                    button2.setVisibility(View.GONE);
                                    button3.setVisibility(View.VISIBLE);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("إضغط على الخانة المراد تعديلها");
                                }else {
                                    button1.setVisibility(View.VISIBLE);
                                    button2.setVisibility(View.VISIBLE);
                                    button3.setVisibility(View.GONE);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("الطلبات");
                                }
                            } else {
//                                Log.w(TAG, "Error getting documents.", task.getException());
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("فشل في تحميل البيانات");
                                if(n == 1) {
                                    button1.setVisibility(View.GONE);
                                    button2.setVisibility(View.GONE);
                                    button3.setVisibility(View.VISIBLE);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("إضغط على الخانة المراد تعديلها");
                                }else {
                                    button1.setVisibility(View.VISIBLE);
                                    button2.setVisibility(View.VISIBLE);
                                    button3.setVisibility(View.GONE);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("الطلبات");
                                }
                            }
                        }
                    });
        }else {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("الرجاء التحقق من الإتصال بالإنترنت");
            if(n == 1) {
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                button3.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText("إضغط على الخانة المراد تعديلها");
            }else {
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.GONE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText("الطلبات");
            }
        }
    }

    public void EDITR(View view) {
        button1 = (Button) findViewById(R.id.EditButton);
        button2 = (Button) findViewById(R.id.AddButton);
        button3 = (Button) findViewById(R.id.cancleButton2);
        textView2 = (TextView) findViewById(R.id.RequestsTextView);
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            textView = (TextView) findViewById(R.id.RequestsFailTextView);
            textView.setVisibility(View.GONE);
            listView = (ListView) findViewById(R.id.RequestsDetailsListView);
            listView.setVisibility(View.VISIBLE);
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            button3.setVisibility(View.GONE);
            textView2.setText("إضغط على الخانة المراد تعديلها");
            textView2.setVisibility(View.GONE);
            Load(1);
        }else {
            textView = (TextView) findViewById(R.id.RequestsFailTextView);
            textView.setVisibility(View.VISIBLE);
            textView.setText("الرجاء التحقق من الإتصال بالإنترنت");
            listView = (ListView) findViewById(R.id.RequestsDetailsListView);
            listView.setVisibility(View.GONE);
        }
    }

    public void ADDR(View view) {
        Intent intent = new Intent(getBaseContext(), RequestsStateDetails.class);
        intent.putExtra("statename", "ADDR");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Load(0);
    }

    public void cancle(View view) {
        Load(0);
    }

}