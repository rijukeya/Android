package com.example.photos;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album that contains a collection of photos.
 * Provides methods to manage photos within the album.
 *
 * @author Rijukeya
 * @version 1.0
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    public static ArrayList<Album> albums = new ArrayList<>();

    public static void serializeAlbums(Context context) throws IOException {
        File file = new File(context.getFilesDir(), "albumList.ser");
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(albums);
        }
    }

    public static void deserializeAlbums(Context context) throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir(), "albumList.ser");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                albums = (ArrayList<Album>) ois.readObject();
            }
        } else {
            albums = new ArrayList<>(); // Initialize empty list if no file exists
        }
    }

    /**
     * The name of the album.
     */
    private String name;

    /**
     * The list of photos in the album.
     */
    private List<Photo> photos;

    /**
     * Constructs a new album with the specified name.
     *
     * @param name The name of the album.
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Gets the name of the album.
     *
     * @return The name of the album.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the album.
     *
     * @param name The new name of the album.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of photos in the album.
     *
     * @return The list of photos.
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Sets the list of photos in the album.
     *
     * @param photos The new list of photos.
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     * Adds a photo to the album.
     * If the photo is not already in the album, it is added and its album is set.
     *
     * @param photo The photo to add.
     */
    public void addPhoto(Photo photo) {
        if (!photos.contains(photo)) {
            this.photos.add(photo);
            photo.setAlbum(this);
        }
    }

    /**
     * Removes a photo from the album.
     * If the photo is in the album, it is removed and its album is set to null.
     *
     * @param photo The photo to remove.
     */
    public void removePhoto(Photo photo) {
        this.photos.remove(photo);
        photo.setAlbum(null);
    }

    /**
     * Searches for photos in the album by a specific tag name and value.
     *
     * @param tagName The name of the tag to search for.
     * @param tagValue The value of the tag to search for.
     * @return A list of photos that match the specified tag name and value.
     */
    public List<Photo> searchPhotosByTag(String tagName, String tagValue) {
        List<Photo> matchingPhotos = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.getTags().containsKey(tagName) && photo.getTags().get(tagName).contains(tagValue)) {
                matchingPhotos.add(photo);
            }
        }
        return matchingPhotos;
    }

    /**
     * Copies a photo from this album to another album.
     *
     * @param photo The photo to copy.
     * @param targetAlbum The target album to copy the photo to.
     */
    public void copyPhotoToAlbum(Photo photo, Album targetAlbum) {
        targetAlbum.addPhoto(photo);
    }

    /**
     * Moves a photo from this album to another album.
     * The photo is removed from this album and added to the target album.
     *
     * @param photo The photo to move.
     * @param targetAlbum The target album to move the photo to.
     */
    public void movePhotoToAlbum(Photo photo, Album targetAlbum) {
        if (photos.contains(photo)) {
            targetAlbum.addPhoto(photo);
            removePhoto(photo);
        }
    }

    public int getNumPhotos() {
        return photos.size();
    }

    /**
     * Returns the string representation of the album.
     * This is typically the name of the album.
     *
     * @return The name of the album.
     */
    @Override
    public String toString() {
        return name;
    }
}
