package learn2crack.cotacts;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.util.LongSparseArray;

/**
 * Created by samzaleg on 9/15/2015.
 */
public class Contact {
    private long id;
    private Resources res;
    private String name;
    private Bitmap photo;
    private LongSparseArray<String> phones;

    Contact(long id, String name, Resources res) {
        this.id = id;
        this.name = name;
        this.res = res;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean rich) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (rich) {
            builder.append("id: ").append(Long.toString(id))
                    .append(", name: ").append("\u001b[1m").append(name).append("\u001b[0m");
        } else {
            builder.append(name);
        }

        if (phones != null) {
            builder.append("\n\tphones: ");
            for (int i = 0; i < phones.size(); i++) {
                int type = (int) phones.keyAt(i);
                builder.append(ContactsContract.CommonDataKinds.Phone.getTypeLabel(res, type, ""))
                        .append(": ")
                        .append(phones.valueAt(i));
                if (i + 1 < phones.size()) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }

    public void SetPhoto(Bitmap bitmap) {
        this.photo = bitmap;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void addPhone(int type, String number) {
        if (phones == null) {
            phones = new LongSparseArray<String>();
        }
        phones.put(type, number);
    }
}
