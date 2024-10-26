package com.example.pcstorage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcstorage.Modal.DocumentModel;
import com.example.pcstorage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<DocumentModel> documentList;
    private Context context;

    public DocumentAdapter(List<DocumentModel> documentList, Context context) {
        this.documentList = documentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentModel document = documentList.get(position);
        holder.documentName.setText(document.getName());

        // Handle Download Button
        holder.downloadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(document.getUrl()));
            context.startActivity(intent);
        });

        // Handle Delete Button
        holder.deleteButton.setOnClickListener(v -> {
            deleteDocument(document.getName(), position);
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView documentName;
        public ImageButton downloadButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentName = itemView.findViewById(R.id.documentName);
            downloadButton = itemView.findViewById(R.id.downloadButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Method to delete a document
    private void deleteDocument(String name, int position) {
        // Assuming userId is required for the path; adjust if necessary
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference documentRef = FirebaseStorage.getInstance().getReference("Data/" + userId + "/documents/" + name);

        documentRef.delete().addOnSuccessListener(aVoid -> {
            // Remove from list and notify adapter
            documentList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to delete document", Toast.LENGTH_SHORT).show();
        });
    }

}