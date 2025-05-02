package com.example.photos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.photos.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private Album selectedAlbum;
    private PhotoAdapter photoAdapter;

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

        // Initialize the photo adapter and set it to the GridView
        photoAdapter = new PhotoAdapter(requireContext(), selectedAlbum.getPhotos());
        binding.photoGridView.setAdapter(photoAdapter);

        // Set up GridView item click listener
        binding.photoGridView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Photo selectedPhoto = selectedAlbum.getPhotos().get(position);
            showPhotoOptionsDialog(selectedPhoto, position);
        });

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
                photoAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Photo added to album: " + filePath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Photo path cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showRemovePhotoDialog() {
        Toast.makeText(requireContext(), "Select a photo from the grid to remove", Toast.LENGTH_SHORT).show();
    }

    private void displayPhoto() {
        if (selectedAlbum != null && !selectedAlbum.getPhotos().isEmpty()) {
            Bundle args = new Bundle();
            args.putSerializable("album", selectedAlbum);
            args.putInt("photoIndex", 0); // Start with the first photo
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_PhotoDisplayFragment, args);
        } else {
            Toast.makeText(requireContext(), "No photos to display", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPhotoOptionsDialog(Photo photo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Photo Options");
        builder.setItems(new String[]{"Display Photo", "Delete Photo", "Move Photo"}, (dialog, which) -> {
            if (which == 0) {
                Toast.makeText(requireContext(), "Displaying photo: " + photo.getFilePath(), Toast.LENGTH_SHORT).show();
            } else if (which == 1) {
                selectedAlbum.removePhoto(photo);
                photoAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Photo deleted", Toast.LENGTH_SHORT).show();
            } else if (which == 2) {
                movePhotoToAnotherAlbum(photo);
            }
        });
        builder.show();
    }

    private void movePhotoToAnotherAlbum(Photo photo) {
        if (Album.albums == null || Album.albums.isEmpty()) {
            Toast.makeText(requireContext(), "No albums available to move the photo", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Target Album");

        String[] albumNames = Album.albums.stream().map(Album::getName).toArray(String[]::new);
        builder.setItems(albumNames, (dialog, which) -> {
            Album targetAlbum = Album.albums.get(which);
            if (targetAlbum != null && targetAlbum != selectedAlbum) {
                selectedAlbum.movePhotoToAlbum(photo, targetAlbum);
                photoAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Photo moved to album: " + targetAlbum.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Cannot move to the same album", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}