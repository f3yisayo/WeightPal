package ninja.feyisayo.apps.weightpal;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.firebase.client.*;

import java.util.ArrayList;


public class CalorieActivity extends AppCompatActivity {


    Firebase foodRef = new Firebase("https://weight-pal.firebaseio.com/foods");

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.food_editText)
    AutoCompleteTextView foodSuggestions;

    @InjectView(R.id.calorie_container)
    ScrollView scrollLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

        ButterKnife.inject(this);
        provideToolbarEnhancement();
        fetchData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void provideToolbarEnhancement(){
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Health Data");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void fetchData(){

        final ArrayList<String> foodNames = new ArrayList<String>();


        Query carbohydratesQueryRef = foodRef.child("carbohydrates").orderByKey().startAt("A").endAt("Z");
        Query proteinsQueryRef = foodRef.child("protein").orderByKey().startAt("A").endAt("Z");
        Query vitaminsQueryRef = foodRef.child("vitamins").orderByKey().startAt("A").endAt("Z");


        // Carbohydrates query
        carbohydratesQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String food = ds.getKey();
                    foodNames.add(food);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Protein query
        proteinsQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String food = ds.getKey();
                    foodNames.add(food);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        vitaminsQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String food = ds.getKey();
                    foodNames.add(food);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CalorieActivity.this,
                        android.R.layout.simple_dropdown_item_1line, foodNames);


                foodSuggestions.setThreshold(1);
                foodSuggestions.setAdapter(adapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar.make(scrollLayout, "Can't connect to the internet", Snackbar.LENGTH_SHORT).show();
            }
        });


    }

}
