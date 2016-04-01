package ninja.feyisayo.apps.weightpal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.firebase.client.*;

import java.util.ArrayList;


public class CalorieActivity extends AppCompatActivity {

    Firebase foodRef = new Firebase("https://weight-pal.firebaseio.com/foods");

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.calorie_digit)
    TextView calorie_text;

    @InjectView(R.id.food_editText)
    AutoCompleteTextView foodSuggestions;

    @InjectView(R.id.calorie_container)
    ScrollView scrollLayout;

    @InjectView(R.id.food_eaten_daily)
    TextView eaten_daily;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

        ButterKnife.inject(this);
        provideToolbarEnhancement();
        fetchData();
        eaten_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calorie, menu);
        return true;
    }

    public void provideToolbarEnhancement() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Health Data");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void showDialog(){

        final CharSequence options[] = new CharSequence[]{"Once","Twice", "Thrice", "Others"};


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How often?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eaten_daily.setText(getResources().getString(R.string.food_eaten_set, options[which]) );
            }
        });

        builder.show();
    }

    public void fetchData() {

        final ArrayList foodName = new ArrayList();


        final Query carbohydratesQueryRef = foodRef.child("carbohydrates").orderByKey().startAt("A").endAt("Z");
        final Query proteinsQueryRef = foodRef.child("protein").orderByKey().startAt("A").endAt("Z");
        final Query vitaminsQueryRef = foodRef.child("vitamins").orderByKey().startAt("A").endAt("Z");


        // Carbohydrates query
        carbohydratesQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String food = ds.getKey();

                    foodName.add(food);
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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String food = ds.getKey();
                    foodName.add(food);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        vitaminsQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String food = ds.getKey();
                    foodName.add(food);
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
                        android.R.layout.simple_dropdown_item_1line, foodName);

                foodSuggestions.setThreshold(1);
                foodSuggestions.setAdapter(adapter);

                foodSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Query for carbohydrate foods
                        carbohydratesQueryRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (foodSuggestions.getText().toString().equals(ds.getKey())) {
                                        calorie_text.setText(ds.getValue().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        // Protein Query
                        proteinsQueryRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (foodSuggestions.getText().toString().equals(ds.getKey())) {
                                        calorie_text.setText(ds.getValue().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        // Vitamin query
                        vitaminsQueryRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (foodSuggestions.getText().toString().equals(ds.getKey())) {
                                        calorie_text.setText(ds.getValue().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                });


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar.make(scrollLayout, "Can't connect to the internet", Snackbar.LENGTH_SHORT).show();
            }
        });


    }

}
