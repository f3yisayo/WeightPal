package ninja.feyisayo.apps.weightpal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.firebase.client.*;

public class BMIActivity extends AppCompatActivity {

    @InjectView(R.id.toolBar)
    Toolbar toolbar;
    @InjectView(R.id.userHeight)
    TextView height;

    Firebase ref = new Firebase("https://weight-pal.firebaseio.com");
    AuthData authData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bmi_layout);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Body Mass Index");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authData = ref.getAuth();

        setData();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        ref.child("health").child(authData.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("height").exists()){
                    Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void setData(){
        ref.child("health").child(authData.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("height").exists()){
                    Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                    startActivity(intent);

                    return;
                }
                UserHealth userHealth = dataSnapshot.getValue(UserHealth.class);

                Double bmi = (double) (userHealth.getCurrent_weight() / userHealth.getHeight() * userHealth.getHeight());
                height.setText(String.format("%.2f", bmi));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


}
