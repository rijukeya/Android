package com.example.photos;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.photos.databinding.FragmentPhotoDisplayBinding;

import java.io.File;
import java.util.HashSet;

public class PhotoDisplayFragment extends Fragment {

    private FragmentPhotoDisplayBinding binding;
    private Album album;
    private int currentPhotoIndex;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the album and current photo index from arguments
        if (getArguments() != null) {
            album = (Album) getArguments().getSerializable("album");
            currentPhotoIndex = getArguments().getInt("photoIndex", 0);
        }

        if (album != null && !album.getPhotos().isEmpty()) {
            displayPhoto();
        }

        binding.previousPhotoButton.setOnClickListener(v -> navigatePhoto(-1));
        binding.nextPhotoButton.setOnClickListener(v -> navigatePhoto(1));
        binding.modifyPersonTagButton.setOnClickListener(v -> modifyTag("person", binding.personTagEditText.getText().toString().trim()));
        binding.modifyLocationTagButton.setOnClickListener(v -> modifyTag("location", binding.locationTagEditText.getText().toString().trim()));
    }

    private void modifyTag(String tagName, String tagValue) {
        if (!tagValue.isEmpty()) {
            Photo currentPhoto = album.getPhotos().get(currentPhotoIndex);
            // Clear existing tag values for the tag name
            currentPhoto.getTags().put(tagName, new HashSet<>());
            currentPhoto.addTag(tagName, tagValue);
            displayPhoto();
            Toast.makeText(requireContext(), tagName + " tag modified", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), tagName + " tag value cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPhoto() {
        Photo currentPhoto = album.getPhotos().get(currentPhotoIndex);
        binding.photoImageView.setImageURI(Uri.fromFile(new File(currentPhoto.getFilePath())));

        // Display tags
        String personTag = currentPhoto.getTags().getOrDefault("person", new HashSet<>()).stream().findFirst().orElse("");
        String locationTag = currentPhoto.getTags().getOrDefault("location", new HashSet<>()).stream().findFirst().orElse("");

        binding.personTagTextView.setText("Person: " + personTag);
        binding.locationTagTextView.setText("Location: " + locationTag);
    }

    private void navigatePhoto(int direction) {
        currentPhotoIndex += direction;
        if (currentPhotoIndex < 0) {
            currentPhotoIndex = album.getPhotos().size() - 1;
        } else if (currentPhotoIndex >= album.getPhotos().size()) {
            currentPhotoIndex = 0;
        }
        displayPhoto();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}