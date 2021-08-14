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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class GameDetails extends AppCompatActivity {

    FirebaseFirestore db;
    ListView listView;
    String TAG,s;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ProgressBar progressBar;
    TextView textView, textView1;
    Button button1, button2, button3;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        intent = getIntent();
        s = intent.getStringExtra("GameName");
        textView1 = (TextView) findViewById(R.id.GameNameTextView);
        textView1.setText(s);

        listView = (ListView) findViewById(R.id.GameDetailsListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(button3.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(getBaseContext(), StateDetails.class);
                    intent.putExtra("name", parent.getItemAtPosition(position).toString());
                    intent.putExtra("statename", "EDIT2000");
                    intent.putExtra("name2", Check(s));
                    startActivity(intent);
                }

            }
        });
    }

    private String Check(String s) {

        if(s.equals("إسم/حيوان")) {
            return "NameAndAnimal";
        }else if(s.equals("فواكة")) {
            return "Fruits";
        }else if(s.equals("شطرنج")) {
            return "chess";
        }else if(s.equals("زهر")) {
            return "Dice";
        }else if(s.equals("تركس")) {
            return "Trix";
        }else if(s.equals("أفلام")) {
            return "Movies";
        }else if(s.equals("طرنيب")) {
            return "Tarneeb";
        }else if(s.equals("مونوبولي")) {
            return "Monoboly";
        }else if(s.equals("أونو")){
            return "UNO";
        }

        return s;
    }

    private void Load(final String s, final int n) {
        progressBar = (ProgressBar) findViewById(R.id.RequestsStateprogressBar2);
        textView = (TextView) findViewById(R.id.RequestsFailTextView2);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        button1 = (Button) findViewById(R.id.Edit1Button);
        button2 = (Button) findViewById(R.id.Add1Button);
        button3 = (Button) findViewById(R.id.cancle1Button2);
        textView1 = (TextView) findViewById(R.id.GameNameTextView);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        textView1.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        listView = (ListView) findViewById(R.id.GameDetailsListView);
        arrayList = new ArrayList<String>();
        listView.setAdapter(null);

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            db.collection(Check(s) + " Details")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("لا توجد تفاصيل بعد عن هذه اللعبة");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    textView.setVisibility(View.GONE);
                                    arrayList.add("الإسم : - "+document.getData().get("name").toString()+" -  //  عدد مرات الفوز : - "
                                            + document.getData().get("win").toString() + " -");
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
                                    textView1.setVisibility(View.VISIBLE);
                                    textView1.setText("إضغط على الخانة المراد تعديلها");
                                }else {
                                    button1.setVisibility(View.VISIBLE);
                                    button2.setVisibility(View.VISIBLE);
                                    button3.setVisibility(View.GONE);
                                    textView1.setVisibility(View.VISIBLE);
                                    textView1.setText(s);
                                }
                            } else {
//                                Log.w(TAG, "Error getting documents.", task.getException());
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("فشل في تحميل البيانات");
                                if(n == 1) {
                                    button1.setVisibility(View.GONE);
                                    button2.setVisibility(View.GONE);
                                    button3.setVisibility(View.VISIBLE);
                                    textView1.setVisibility(View.VISIBLE);
                                    textView1.setText("إضغط على الخانة المراد تعديلها");
                                }else {
                                    button1.setVisibility(View.VISIBLE);
                                    button2.setVisibility(View.VISIBLE);
                                    button3.setVisibility(View.GONE);
                                    textView1.setVisibility(View.VISIBLE);
                                    textView1.setText(s);
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
                textView1.setVisibility(View.VISIBLE);
                textView1.setText("إضغط على الخانة المراد تعديلها");
            }else {
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.GONE);
                textView1.setVisibility(View.VISIBLE);
                textView1.setText(s);
            }
        }
    }

    public void EDIT(View view) {
        button1 = (Button) findViewById(R.id.Edit1Button);
        button2 = (Button) findViewById(R.id.Add1Button);
        button3 = (Button) findViewById(R.id.cancle1Button2);
        textView1 = (TextView) findViewById(R.id.GameNameTextView);
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            textView = (TextView) findViewById(R.id.RequestsFailTextView2);
            textView.setVisibility(View.GONE);
            listView = (ListView) findViewById(R.id.GameDetailsListView);
            listView.setVisibility(View.VISIBLE);
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            button3.setVisibility(View.GONE);
            textView1.setText("إضغط على الخانة المراد تعديلها");
            textView1.setVisibility(View.GONE);
            Load(s, 1);
        }else {
            textView = (TextView) findViewById(R.id.RequestsFailTextView2);
            textView.setVisibility(View.VISIBLE);
            textView.setText("الرجاء التحقق من الإتصال بالإنترنت");
            listView = (ListView) findViewById(R.id.GameDetailsListView);
            listView.setVisibility(View.GONE);
        }
    }

    public void ADD(View view) {
        Intent intent = new Intent(getBaseContext(), StateDetails.class);
        intent.putExtra("statename", "ADD2000");
        intent.putExtra("name2", Check(s));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        s = intent.getStringExtra("GameName");
        Load(s, 0);
    }

    public void canclebutton2(View view) {
        Load(s, 0);
    }
}