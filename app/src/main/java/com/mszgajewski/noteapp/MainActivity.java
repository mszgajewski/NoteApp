package com.mszgajewski.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;;
import com.mszgajewski.noteapp.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private  NotesAdapter notesAdapter;
    private  List<NotesModel> notesModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        notesModelList = new ArrayList<>();
        notesAdapter = new NotesAdapter(this);
        binding.notesRecycler.setAdapter(notesAdapter);
        binding.notesRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(text.length() > 0){
                    filter(text);
                } else {
                    notesAdapter.clear();
                    notesAdapter.filterList(notesModelList);
                }
            }
        });
    }

    private void filter(String text) {
        List<NotesModel> adapterList = notesAdapter.getList();
        List<NotesModel> notesModelList = new ArrayList<>();
        for (int i = 0; i < adapterList.size(); i++) {
            NotesModel notesModel = adapterList.get(i);
            if (notesModel.getTitle().toLowerCase().contains(text.toLowerCase())|| notesModel.getDescription().toLowerCase().contains(text)) {
                notesModelList.add(notesModel);
            }
        }
        notesAdapter.filterList(notesModelList);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DelayedProgressDialog progressDialog = new DelayedProgressDialog();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            progressDialog.show(getSupportFragmentManager(), "Sprawdzanie uzytkownika w toku");
            firebaseAuth.signInAnonymously()
                    .addOnSuccessListener(authResult -> progressDialog.cancel())
                    .addOnFailureListener(e -> {
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {

        FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notesAdapter.clear();
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                    for (int i=0; i<documentSnapshotList.size(); i++){
                        DocumentSnapshot documentSnapshot = documentSnapshotList.get(i);
                        NotesModel notesModel = documentSnapshot.toObject(NotesModel.class);
                        notesModelList.add(notesModel);
                        notesAdapter.add(notesModel);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}