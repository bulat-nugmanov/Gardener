package com.example.Gardener.model;

import java.util.Set;

/**
 * Represents a schedule of schedule items
 */
public interface Schedule {

    String getName();

    void setName(String name);

    String toEncodedString();
}
