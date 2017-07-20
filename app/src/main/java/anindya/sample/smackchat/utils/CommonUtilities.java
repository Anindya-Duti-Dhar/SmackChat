package anindya.sample.smackchat.utils;

import android.content.Context;
import android.widget.Toast;

public class CommonUtilities {

    // Common toast method
    public static void MakeToast(Context context, String Message){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, Message, duration);
        toast.show();
    }
}
