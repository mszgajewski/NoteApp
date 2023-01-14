package com.mszgajewski.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mszgajewski.noteapp.databinding.ActivityAddBinding;

import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    ActivityAddBinding binding;
    private String title="", description="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = binding.addTitleEditText.getText().toString();
                description = binding.addDescEditText.getText().toString();
                saveNote();
            }
        });
    }

    private void saveNote() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Zapisywanie");
        progressDialog.setMessage("notatki");
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String noteId = UUID.randomUUID().toString();
        NotesModel notesModel = new NotesModel(noteId, title, description, firebaseAuth.getUid());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("notes")
                .document(noteId)
                .set(notesModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddActivity.this, "Zapisano", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
    }
}