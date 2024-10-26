package com.example.pcstorage.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcstorage.Adapter.DocumentAdapter;
import com.example.pcstorage.Modal.DocumentModel;
import com.example.pcstorage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Document_fragment extends Fragment {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<DocumentModel> documentList;
    private FirebaseAuth auth;
    private TextView empty_view;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.document_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDocuments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        documentList = new ArrayList<>();
        adapter = new DocumentAdapter(documentList, getContext());
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        empty_view = view.findViewById(R.id.empty_view);

        fetchDocuments();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDocuments() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login to view your documents", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Data/" + userId + "/documents/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            documentList.clear();  // Clear the list before adding new items
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileName = item.getName();
                    String downloadUrl = uri.toString();
                    documentList.add(new DocumentModel(fileName, downloadUrl));
                    adapter.notifyDataSetChanged();
                    updateUI();
                });
            }

            // Update the UI in case no documents were found
            if (listResult.getItems().isEmpty()) {
                updateUI();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load documents", Toast.LENGTH_SHORT).show();
            updateUI();
        });
    }

    public void updateUI() {
        if (documentList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
    }
}
