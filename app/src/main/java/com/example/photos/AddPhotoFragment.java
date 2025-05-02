package com.example.photos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photos.databinding.FragmentAddPhotoBinding;

public class AddPhotoFragment extends Fragment {
    private FragmentAddPhotoBinding binding;
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddPhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSelectImage.setOnClickListener(v -> openGallery());
        binding.buttonBackToAlbum.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // Navigate back to SecondFragment
        });
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        pickPhotoIntent.setType("image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requireActivity().RESULT_OK && requestCode == REQUEST_IMAGE_PICK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Bundle result = new Bundle();
                result.putString("selectedImageUri", selectedImageUri.toString());
                requireActivity().getSupportFragmentManager().setFragmentResult("addPhotoResult", result);
                Toast.makeText(requireContext(), "Image selected: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                // Reopen the gallery
                openGallery();
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}