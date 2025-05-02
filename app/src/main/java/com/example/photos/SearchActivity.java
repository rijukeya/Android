package com.example.photos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ListView autocompleteList;
    private GridView searchResults;
    private ArrayAdapter<String> autocompleteAdapter;
    private PhotoAdapter photoAdapter;
    private List<Photo> allPhotos;
    private List<Photo> matchingPhotos;
    private List<String> autocompleteSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_input);
        autocompleteList = findViewById(R.id.autocomplete_list);
        searchResults = findViewById(R.id.search_results);

        findViewById(R.id.back_to_home_button).setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, Home.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity
        });

        allPhotos = getAllPhotos();
        matchingPhotos = new ArrayList<>();
        autocompleteSuggestions = new ArrayList<>();

        autocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autocompleteSuggestions);
        autocompleteList.setAdapter(autocompleteAdapter);

        photoAdapter = new PhotoAdapter(this, matchingPhotos);
        searchResults.setAdapter(photoAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAutocompleteSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autocompleteList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedSuggestion = autocompleteSuggestions.get(position);
            searchInput.setText(selectedSuggestion);
            autocompleteList.setVisibility(View.GONE);
        });

    }

    private List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<>();
        for (Album album : Album.albums) {
            photos.addAll(album.getPhotos());
        }
        return photos;
    }

    private void updateAutocompleteSuggestions(String query) {
        autocompleteSuggestions.clear();
        if (!query.isEmpty()) {
            for (Photo photo : allPhotos) {
                for (String tagName : photo.getTags().keySet()) {
                    for (String tagValue : photo.getTags().get(tagName)) {
                        if (tagValue.toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT))) {
                            String suggestion = tagName + "=" + tagValue;
                            if (!autocompleteSuggestions.contains(suggestion)) {
                                autocompleteSuggestions.add(suggestion);
                            }
                        }
                    }
                }
            }
        }
        autocompleteAdapter.notifyDataSetChanged();
        autocompleteList.setVisibility(autocompleteSuggestions.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty() || !query.contains("=")) {
            Toast.makeText(this, "Invalid query. Use format: tag=value", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = query.split("=", 2);
        String tagName = parts[0].toLowerCase(Locale.ROOT);
        String tagValue = parts[1].toLowerCase(Locale.ROOT);

        matchingPhotos.clear();
        for (Photo photo : allPhotos) {
            if (photo.getTags().containsKey(tagName)) {
                for (String value : photo.getTags().get(tagName)) {
                    if (value.toLowerCase(Locale.ROOT).startsWith(tagValue)) {
                        matchingPhotos.add(photo);
                        break;
                    }
                }
            }
        }

        photoAdapter.notifyDataSetChanged();
        if (matchingPhotos.isEmpty()) {
            Toast.makeText(this, "No matching photos found", Toast.LENGTH_SHORT).show();
        }
    }
}