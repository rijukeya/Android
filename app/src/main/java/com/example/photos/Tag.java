package com.example.photos;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag associated with a photo.
 * Only "person" and "location" are valid tag types.
 * 
 * @author Rijukeya
 * @version 1.0
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * Creates a new tag with the specified name and value.
     * Only "person" and "location" are allowed as valid tag names.
     *
     * @param name The name of the tag.
     * @param value The value of the tag.
     * @throws IllegalArgumentException If the tag name is not "person" or "location".
     */
    public Tag(String name, String value) {
        if (!isValidTagName(name)) {
            throw new IllegalArgumentException("Only 'person' and 'location' tags are allowed.");
        }
        this.name = name.toLowerCase(); // Normalize to lowercase for consistency
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!isValidTagName(name)) {
            throw new IllegalArgumentException("Only 'person' and 'location' tags are allowed.");
        }
        this.name = name.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if the tag name is valid.
     *
     * @param name The tag name to check.
     * @return True if the tag name is "person" or "location", false otherwise.
     */
    private static boolean isValidTagName(String name) {
        return "person".equalsIgnoreCase(name) || "location".equalsIgnoreCase(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) && Objects.equals(value, tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
