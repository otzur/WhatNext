package learn2crack.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by otzur on 9/19/2015.
 */
public class WnContact  implements Serializable {
    private String name;
    private String phoneNumber;
    private Bitmap photo;
    private static final long serialVersionUID = -7060210544600464481L;

    public WnContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        //this.photoId = photoId;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
