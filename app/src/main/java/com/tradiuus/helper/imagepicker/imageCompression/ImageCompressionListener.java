package com.tradiuus.helper.imagepicker.imageCompression;

public interface ImageCompressionListener {
    void onStart();

    void onCompressed(String filePath);
}
