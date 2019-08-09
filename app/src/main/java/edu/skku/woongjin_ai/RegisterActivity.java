package edu.skku.woongjin_ai;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextID, editTextNM, editTextPW;
    String id, nm, pw;
    int coin;
    DatabaseReference mPostReference;
    Intent intent;
    int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button button = (Button) findViewById(R.id.button);
        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextNM = (EditText) findViewById(R.id.editTextNM);
        editTextPW = (EditText) findViewById(R.id.editTextPW);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editTextID.getText().toString();
                nm = editTextNM.getText().toString();
                pw = editTextPW.getText().toString();

                if(id.length() == 0 || nm.length() == 0 || pw.length() == 0) {
                    Toast.makeText(RegisterActivity.this, "Fill all blanks", Toast.LENGTH_SHORT).show();
                } else {
                    final ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(flag > -1) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String key = snapshot.getKey();
                                    if(id.equals(key)) {
                                        Toast.makeText(RegisterActivity.this, "ID is overlapped", Toast.LENGTH_SHORT).show();
                                        editTextID.setText("");
                                        editTextNM.setText("");
                                        editTextPW.setText("");
                                        flag = 1;
                                        break;
                                    }
                                    else flag = 0;
                                }
                                if(flag == 0) {
                                    flag = -1;
                                    postFirebaseDatabaseUserInfo();

                                    intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {        }
                    };
                    mPostReference.child("user_list").addValueEventListener(postListener);
                }
            }
        });
    }

    private void postFirebaseDatabaseUserInfo() {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        UserInfo post = new UserInfo(id, nm, pw, coin);
        postValues = post.toMap();
        childUpdates.put("/user_list/" + id + "/", postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
