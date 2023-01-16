package com.mszgajewski.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mszgajewski.noteapp.databinding.ActivityAddBinding;
import com.mszgajewski.noteapp.databinding.ActivityUpdateBinding;

import java.util.UUID;

public class UpdateActivity extends AppCompatActivity {

    ActivityUpdateBinding binding;
    private String title, description,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");

        binding.updateTitleEditText.setText(title);
        binding.updateDescEditText.setText(description);

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = binding.updateTitleEditText.getText().toString();
                description = binding.updateDescEditText.getText().toString();
                updateNote();
            }
        });
    }

    private void updateNote(){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aktualizowanie");
        progressDialog.setMessage("notatki");
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        NotesModel notesModel = new NotesModel(id, title, description, firebaseAuth.getUid());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("notes")
                .document(id)
                .set(notesModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateActivity.this, "Zapisano", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
    }
}