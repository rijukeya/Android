package com.example.photos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.photos.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private Album selectedAlbum;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the selected album from the arguments
        if (getArguments() != null) {
            selectedAlbum = (Album) getArguments().getSerializable("selectedAlbum");
        }

        if (selectedAlbum != null) {
            Toast.makeText(requireContext(), "Opened album: " + selectedAlbum.getName(), Toast.LENGTH_SHORT).show();
        }

        // Set up button listeners
        binding.buttonAddPhoto.setOnClickListener(v -> showAddPhotoDialog());
        binding.buttonRemovePhoto.setOnClickListener(v -> showRemovePhotoDialog());
        binding.buttonDisplayPhoto.setOnClickListener(v -> displayPhoto());

        // Navigate back to the Home fragment
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    private void showAddPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Photo");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter photo file path");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String filePath = input.getText().toString().trim();
            if (!filePath.isEmpty()) {
                Photo newPhoto = new Photo(filePath);
                selectedAlbum.addPhoto(newPhoto);
                Toast.makeText(requireContext(), "Photo added to album: " + filePath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Photo path cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showRemovePhotoDialog() {
        if (selectedAlbum.getPhotos().isEmpty()) {
            Toast.makeText(requireContext(), "No photos to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Remove Photo");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter photo file path to remove");
        builder.setView(input);

        builder.setPositiveButton("Remove", (dialog, which) -> {
            String filePath = input.getText().toString().trim();
            Photo photoToRemove = null;
            for (Photo photo : selectedAlbum.getPhotos()) {
                if (photo.getFilePath().equals(filePath)) {
                    photoToRemove = photo;
                    break;
                }
            }

            if (photoToRemove != null) {
                selectedAlbum.removePhoto(photoToRemove);
                Toast.makeText(requireContext(), "Photo removed: " + filePath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Photo not found in album", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void displayPhoto() {
        Toast.makeText(requireContext(), "Display photo feature not implemented yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}