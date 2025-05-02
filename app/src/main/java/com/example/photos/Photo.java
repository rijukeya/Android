package com.example.photos;;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a photo in the application.
 * A photo has a file path, date taken, caption, and tags.
 * It also belongs to an album.
 * 
 * @author Rijukeya
 * @version 1.0
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filePath;
    private String caption;
    private Map<String, Set<String>> tags;
    private Album album;

    static String currentDir = System.getProperty("user.dir");
    static String storageDir = currentDir+"/src/photos/local";

    public void savePhotoToGallery(Bitmap bitmap, String fileName, Context context) {
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photoFile = new File(picturesDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();

            // Notify the gallery about the new photo
            MediaScannerConnection.scanFile(context, new String[]{photoFile.getAbsolutePath()}, null, null);
            Toast.makeText(context, "Photo saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save photo", Toast.LENGTH_SHORT).show();
        }
    }

    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new HashMap<>();
    }

    public Photo(String filePath, String caption) {
        this.filePath = filePath;
        this.caption = caption;
        this.tags = new HashMap<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Map<String, Set<String>> getTags() {
        return tags;
    }

    public void addTag(String tagName, String tagValue) {
        tags.putIfAbsent(tagName, new HashSet<>());
        tags.get(tagName).add(tagValue);
    }

    public void removeTag(String tagName, String tagValue) {
        if (tags.containsKey(tagName)) {
            tags.get(tagName).remove(tagValue);
            if (tags.get(tagName).isEmpty()) {
                tags.remove(tagName);
            }
        }
    }

    public String getName() {
        return new File(filePath).getName();
    }

    @Override
    public String toString() {
        return "Photo{" +
                "filePath='" + filePath + '\'' +
                ", caption='" + caption + '\'' +
                ", tags=" + tags +
                '}';
    }
}
