package com.example.photos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.photos.databinding.FragmentFirstBinding;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ArrayAdapter<Album> albumAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize album list if null
        if (Album.albums == null) {
            Album.albums = new ArrayList<>();
        }

        // Set up ListView and adapter
        albumAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, Album.albums);
        binding.albumListView.setAdapter(albumAdapter);

        // Set up ListView item click listener to open an album
        binding.albumListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Album selectedAlbum = Album.albums.get(position);
            Toast.makeText(requireContext(), "Opening album: " + selectedAlbum.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to album details (implement navigation logic here)
        });

        // Set up button listeners
        binding.createAlbumButton.setOnClickListener(v -> showCreateAlbumDialog());
        binding.deleteAlbumButton.setOnClickListener(v -> deleteAlbum());
        binding.renameAlbumButton.setOnClickListener(v -> showRenameAlbumDialog());
    }

    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create Album");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter album name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (!albumName.isEmpty()) {
                Album newAlbum = new Album(albumName);
                Album.albums.add(newAlbum);
                albumAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Album created: " + albumName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Album name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showRenameAlbumDialog() {
        if (Album.albums.isEmpty()) {
            Toast.makeText(requireContext(), "No albums to rename", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Rename Album");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter new album name");
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                Album albumToRename = Album.albums.get(Album.albums.size() - 1);
                albumToRename.setName(newName);
                albumAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Renamed album to: " + newName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "New name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteAlbum() {
        if (!Album.albums.isEmpty()) {
            Album removedAlbum = Album.albums.remove(Album.albums.size() - 1);
            albumAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Deleted album: " + removedAlbum.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No albums to delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}