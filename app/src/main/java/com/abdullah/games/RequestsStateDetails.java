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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestsStateDetails extends AppCompatActivity {

    EditText editText1, editText2;
    Button button1, button2, button3;
    FirebaseFirestore db;
    String TAG, s, s2, m, n;
    ProgressBar progressBar;
    String[] array;
    HashMap<String, Object> hashMap;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_state_details);

        progressBar = (ProgressBar) findViewById(R.id.RequestsStateprogressBar);
        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        s = intent.getStringExtra("statename");
        textView = (TextView) findViewById(R.id.RequestsDetailsTextView);
        if(s.equals("ADDR")) {
            textView.setText("إضافة");
        }else {
            textView.setText("تعديل");
        }
        editText1 = (EditText) findViewById(R.id.NameeditText);
        editText2 = (EditText) findViewById(R.id.GameNameeditText2);
        button1 = (Button) findViewById(R.id.DeleteButton);
        button2 = (Button) findViewById(R.id.saveButton);
        button3 = (Button) findViewById(R.id.CancelButton);
        if(s.equals("EDITR")) {
            s2 = intent.getStringExtra("name");
            array = s2.split("-");
            n = array[1].trim();
            editText1.setText(n);
            m = array[3].trim();
            editText2.setText(m);
            button1.setVisibility(View.VISIBLE);
        }else {
            button1.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText1.getText().toString().trim().isEmpty() || editText2.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RequestsStateDetails.this, "يرجى التأكد من تعبئة جميع الخانات", Toast.LENGTH_SHORT).show();
                }else {
                    db = FirebaseFirestore.getInstance();
                    ConnectivityManager manager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = manager.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        progressBar.setVisibility(View.VISIBLE);
                        if(editText1.getText().toString().trim().contains("-") || editText2.getText().toString().trim().contains("-")) {
                            Toast.makeText(RequestsStateDetails.this, "يرجى عدم إستخدام الرمز (-)", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else {
                            if (button1.getVisibility() == View.VISIBLE) {
                                if (!editText1.getText().toString().trim().equals(n) || !editText2.getText().toString().trim().equals(m)) {
                                    db.collection("Requests Details")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            if (document.getData().get("name").toString().equals(n) && document.getData().get("game").toString().equals(m)) {
                                                                db.collection("Requests Details")
                                                                        .document(document.getId())
                                                                        .update("name", editText1.getText().toString().trim(),
                                                                                "game", editText2.getText().toString().trim())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(RequestsStateDetails.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                                                                                finish();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(RequestsStateDetails.this, "حدث فشل في عملية التعديل", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(RequestsStateDetails.this, "حدث فشل في عملية التعديل", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(RequestsStateDetails.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", editText1.getText().toString().trim());
                                user.put("game", editText2.getText().toString().trim());

                                db.collection("Requests Details")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
//                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                Toast.makeText(RequestsStateDetails.this, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error adding document", e);
                                                Toast.makeText(RequestsStateDetails.this, "فشل في إضافة البيانات", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                progressBar.setVisibility(View.GONE);
                                finish();
                            }
                        }
                    }else {
                        Toast.makeText(RequestsStateDetails.this, "الرجاء التحقق من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        button1 = (Button) findViewById(R.id.DeleteButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("هل ترغب في حذف هذا الطلب ؟")
                .setIcon(R.mipmap.delete_icon)
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db = FirebaseFirestore.getInstance();
                        ConnectivityManager manager = (ConnectivityManager)
                                getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = manager.getActiveNetworkInfo();
                        if (info != null && info.isConnected()) {
                            progressBar.setVisibility(View.VISIBLE);
                            db.collection("Requests Details")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.getData().get("name").toString().equals(n) && document.getData().get("game").toString().equals(m)) {
                                                        db.collection("Requests Details").document(document.getId())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(RequestsStateDetails.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
//                                                                Log.w(TAG, "Error deleting document", e);
                                                                        Toast.makeText(RequestsStateDetails.this, "حدث فشل في عملية الحذف", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                });
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(RequestsStateDetails.this, "حدث فشل في عملية الحذف", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(RequestsStateDetails.this, "الرجاء التحقق من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        });

        return myQuittingDialogBox;
    }
}
