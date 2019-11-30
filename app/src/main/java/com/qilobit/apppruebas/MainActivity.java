package com.qilobit.apppruebas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qilobit.apppruebas.Models.*;
import com.qilobit.apppruebas.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static String TAG = "=======MAIN APP========";
    private ListView list;
    private TextView txtName;
    private TextView txtPhone;
    private Button btnSave;
    private Button btnSort;
    private ImageButton btnLogout;
    private Context context;
    private CollectionReference peopleRef;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Toolbar mTopToolbar;
    private Intent activityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnSort = findViewById(R.id.btnSort);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);

        db = FirebaseFirestore.getInstance();

        context = getApplicationContext();
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                saveNewPeople();
            }
        });
        btnSort.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sortPeopleListByName();
            }
        });
        peopleRef = db.collection("People");

        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        showListLoader();
        getPeople();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                askForDeleteConfirmation(position);
            }
        });
        btnLogout.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });
    }
    private void getPeople(){
        db.collection(FirestoreReferences.PEOPLE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        arrayList.clear();
                        String listBody = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listBody = document.getString("name")+ " Tel: " + document.getString("phone");
                                arrayList.add(listBody);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void showListLoader(){
        arrayList.add("Loading...");
        adapter.notifyDataSetChanged();
    }
    private void saveNewPeople(){
        final String name = txtName.getText().toString();
        final String phone = txtPhone.getText().toString();
        if(name.isEmpty() || phone.isEmpty()){
            Toast.makeText(context, "Name and phone required", Toast.LENGTH_LONG).show();
        }else{
            btnSave.setEnabled(false);
            btnSave.setText("Wait...");
            People newPeople = new People(name, phone);
            Toast.makeText(context, "Good to save", Toast.LENGTH_LONG).show();
            peopleRef.add(newPeople)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Saved OK", Toast.LENGTH_LONG).show();
                            appendNewPersonToList(documentReference.getId());
                            btnSave.setEnabled(true);
                            btnSave.setText("Save");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            Toast.makeText(context, "Error writing documen", Toast.LENGTH_LONG).show();
                            btnSave.setEnabled(true);
                            btnSave.setText("Save");
                        }
                    });
        }

    }
    private void appendNewPersonToList(String newId){
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();
        String listBody = name+" => "+phone+" \n";

        arrayList.add(listBody);

        adapter.notifyDataSetChanged();
        cleanTextFields();
    }
    private void cleanTextFields(){
        txtName.setText("");
        txtPhone.setText("");
    }
    private void sortPeopleListByName(){
        Collections.sort(arrayList);
        adapter.notifyDataSetChanged();
    }
    private void removeItemFromList(int index){
        if(arrayList.get(index) != null){
            arrayList.remove(index);
            adapter.notifyDataSetChanged();
        }
    }
    private void askForDeleteConfirmation(final int positionToDelete){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder
                .setTitle("Delete confirmation")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Yes-code
                        removeItemFromList(positionToDelete);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        activityIntent = new Intent(context, LoginActivity.class);
        startActivity(activityIntent);
        finish();
    }
}
