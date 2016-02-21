package ninja.feyisayo.apps.weightpal;

import com.firebase.client.Firebase;

public class WeightPalApp extends android.app.Application {


    @Override
    public void onCreate(){
        super.onCreate();
        // We initialize our firebase context here
        Firebase.setAndroidContext(this);
    }

}
