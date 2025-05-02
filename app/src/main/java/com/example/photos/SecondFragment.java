package com.example.photos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

        // Set up GridView item click listener to select a photo
        binding.photoGridView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Photo selectedPhoto = selectedAlbum.getPhotos().get(position);
            binding.buttonDisplayPhoto.setTag(selectedPhoto); // Store the selected photo in the button's tag
            binding.buttonMovePhoto.setTag(selectedPhoto); // Store the selected photo in the button's tag
            Toast.makeText(requireContext(), "Selected photo: " + selectedPhoto.getName(), Toast.LENGTH_SHORT).show();
        });

        // Set up button listeners
        binding.buttonAddPhoto.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_addPhotoFragment)
        );
        binding.buttonDisplayPhoto.setOnClickListener(v -> displaySelectedPhoto());
        binding.buttonMovePhoto.setOnClickListener(v -> moveSelectedPhoto());
        binding.buttonRemovePhoto.setOnClickListener(v -> removeSelectedPhoto());

        // Navigate back to the Home fragment
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        requireActivity().getSupportFragmentManager().setFragmentResultListener("addPhotoResult", this, (requestKey, result) -> {
            String selectedImageUri = result.getString("selectedImageUri");
            if (selectedImageUri != null && selectedAlbum != null) {
                Photo newPhoto = new Photo(selectedImageUri);
                selectedAlbum.addPhoto(newPhoto);
                photoAdapter.notifyDataSetChanged(); // Refresh the GridView
                Toast.makeText(requireContext(), "Photo added to album", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        pickPhotoIntent.setType("image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    String filePath = getRealPathFromURI(selectedImageUri);
                    if (filePath != null) {
                        Photo newPhoto = new Photo(filePath);
                        selectedAlbum.addPhoto(newPhoto);
                        photoAdapter.notifyDataSetChanged(); // Update the GridView immediately
                        Toast.makeText(requireContext(), "Photo added from gallery", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to retrieve photo path", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void moveSelectedPhoto() {
        Photo selectedPhoto = (Photo) binding.buttonMovePhoto.getTag();
        if (selectedPhoto != null) {
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
                    selectedAlbum.movePhotoToAlbum(selectedPhoto, targetAlbum);
                    photoAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Photo moved to album: " + targetAlbum.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Cannot move to the same album", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            Toast.makeText(requireContext(), "Please select a photo to move", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySelectedPhoto() {
        Photo selectedPhoto = (Photo) binding.buttonDisplayPhoto.getTag();
        if (selectedPhoto != null) {
            Bundle args = new Bundle();
            args.putSerializable("album", selectedAlbum);
            args.putInt("photoIndex", selectedAlbum.getPhotos().indexOf(selectedPhoto));
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_PhotoDisplayFragment, args);
        } else {
            Toast.makeText(requireContext(), "Please select a photo to display", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeSelectedPhoto() {
        Photo selectedPhoto = (Photo) binding.buttonDisplayPhoto.getTag();
        if (selectedPhoto != null) {
            selectedAlbum.removePhoto(selectedPhoto);
            photoAdapter.notifyDataSetChanged(); // Update the GridView immediately
            Toast.makeText(requireContext(), "Photo removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please select a photo to remove", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private void showAddPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Photo");
        builder.setItems(new String[]{"Choose from Gallery"}, (dialog, which) -> {
            if (which == 0) {
                // Choose from Gallery
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private String getRealPathFromURI(Uri uri) {
        String[] projection = {android.provider.MediaStore.Images.Media.DATA};
        try (Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String saveBitmapToFile(Bitmap bitmap, String fileName) {
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = new File(storageDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoFile.getAbsolutePath();
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