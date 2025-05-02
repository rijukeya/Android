package com.example.photos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.photos.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        createStockAlbum();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    private void createStockAlbum() {
        // Create the "stock" album
        Album stockAlbum = new Album("stock");

        // Add photos to the album
        ArrayList<Photo> stockPhotos = new ArrayList<>();
        stockPhotos.add(createPhotoFromDrawable(R.drawable.messi, "mess.jpg"));
        stockPhotos.add(createPhotoFromDrawable(R.drawable.messi2, "messi2.jpg"));
        stockPhotos.add(createPhotoFromDrawable(R.drawable.neymar1, "neyamr1.jpg"));
        stockPhotos.add(createPhotoFromDrawable(R.drawable.neymar2, "neyamr2.jpg"));

        // Add photos to the album
        for (Photo photo : stockPhotos) {
            stockAlbum.addPhoto(photo);
        }

        // Add the album to the global album list
        if (Album.albums == null) {
            Album.albums = new ArrayList<>();
        }
        Album.albums.add(stockAlbum);
    }

    private Photo createPhotoFromDrawable(int drawableId, String fileName) {
        // Convert drawable resource to Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);

        // Save the photo to the gallery (optional)
        Photo photo = new Photo(fileName);
        photo.setCaption(fileName);

        return photo;
    }
    private static final int REQUEST_IMAGE_GET = 1;

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            // Use the URI to load the photo
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}