package com.fast_prog.dynate.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by sarathk on 4/22/17.
 */

public class UploadFiles implements Serializable {

    private Bitmap bm;
    private String base64Encoded;
    private String imageName;

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    public String getBase64Encoded() {
        return base64Encoded;
    }

    public void setBase64Encoded(String base64Encoded) {
        this.base64Encoded = base64Encoded;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
