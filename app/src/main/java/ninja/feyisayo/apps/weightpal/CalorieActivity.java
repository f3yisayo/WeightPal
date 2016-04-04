package ninja.feyisayo.apps.weightpal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import java.util.HashMap;
import java.util.Map;


public class CalorieActivity extends AppCompatActivity {

    Firebase foodRef = new Firebase("https://weight-pal.firebaseio.com/foods");
    Firebase saveHealthRef = new Firebase("https://weight-pal.firebaseio.com/health");
    AuthData authData;

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.futureweight_editText)
    EditText future_weight;

    @InjectView(R.id.calorie_digit)
    TextView calorie_text;

    @InjectView(R.id.food_editText)
    AutoCompleteTextView foodSuggestions;

    @InjectView(R.id.calorie_container)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.food_eaten_daily)
    TextView eaten_daily;

    @InjectView(R.id.weight_editText)
    EditText user_weight;

    @InjectView(R.id.health_checkbox)
    CheckBox checkBox;

    @InjectView(R.id.drinks_edittext)
    AutoCompleteTextView drinkSuggestions;

    @InjectView(R.id.user_height)
    EditText userHeight;


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

            case R.id.data_done:
                saveOtherData();
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

    public boolean checkForValidData(EditText[] fields, AutoCompleteTextView[] fields2) {
        for (EditText currentField : fields) {
            if (currentField.getText().toString().length() <= 0)
                return false;
        }

        if(Integer.valueOf(calorie_text.getText().toString()) < 1)
            return false;

        for (AutoCompleteTextView currentField : fields2) {
            if (currentField.getText().toString().length() <= 0)
                return false;
        }

        return true;
    }

    public void saveOtherData() {

        boolean fieldsAreValid = checkForValidData(new EditText[]{future_weight, user_weight, userHeight},
                new AutoCompleteTextView[]{foodSuggestions, drinkSuggestions});
        // We need to make sure the user filled in the inputs
        if (!fieldsAreValid) {
            Snackbar.make(coordinatorLayout, "Please fill all inputs", Snackbar.LENGTH_LONG).show();
            // We break out of the method if the user's inputs aren't valid
            return;
        }

        // Push the data
        final Map<String, Object> userHealthData = new HashMap<String, Object>();
        saveHealthRef = saveHealthRef.child(authData.getUid());

        userHealthData.put("current_weight", Long.parseLong(user_weight.getText().toString()));
        userHealthData.put("future_weight", Long.parseLong(future_weight.getText().toString()));

        userHealthData.put("daily_eaten", eaten_daily.getText().toString());
        userHealthData.put("health_reminder", checkBox.isChecked());
        userHealthData.put("food_eaten", foodSuggestions.getText().toString());
        userHealthData.put("height", Long.parseLong(userHeight.getText().toString()));
        userHealthData.put("calories_consumed", Long.parseLong(calorie_text.getText().toString()));

        saveHealthRef.updateChildren(userHealthData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null)
                    Snackbar.make(coordinatorLayout, "Data could not be saved", Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(coordinatorLayout, "Data saved successfully :)", Snackbar.LENGTH_LONG).show();
            }

        });
    }

    public void provideToolbarEnhancement() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Health Data");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void showDialog() {

        final CharSequence options[] = new CharSequence[]{"Once", "Twice", "Thrice", "Others"};


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How often?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eaten_daily.setText(getResources().getString(R.string.food_eaten_set, options[which]));
            }
        });

        builder.show();
    }

    public void fetchData() {

        final ArrayList foodName = new ArrayList();
        final ArrayList drinkNames = new ArrayList();

        authData = new Firebase("https://weight-pal.firebaseio.com/").getAuth();


        final Query carbohydratesQueryRef = foodRef.child("carbohydrates").orderByKey().startAt("A").endAt("Z");
        final Query proteinsQueryRef = foodRef.child("protein").orderByKey().startAt("A").endAt("Z");
        final Query vitaminsQueryRef = foodRef.child("vitamins").orderByKey().startAt("A").endAt("Z");

        final Query drinksQuery = foodRef.child("Carbonated Drinks").orderByKey().startAt("A").endAt("Z");

        drinksQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String drink = ds.getKey();
                    drinkNames.add(drink);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


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

                ArrayAdapter<String> foodAdapter = new ArrayAdapter<String>(CalorieActivity.this,
                        android.R.layout.simple_dropdown_item_1line, foodName);

                foodSuggestions.setThreshold(1);
                foodSuggestions.setAdapter(foodAdapter);

                ArrayAdapter<String> drinkAdapter = new ArrayAdapter<String>(CalorieActivity.this, android.R.layout.simple_dropdown_item_1line, drinkNames);
                drinkSuggestions.setThreshold(0);
                drinkSuggestions.setAdapter(drinkAdapter);

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

                drinkSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        drinksQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    Long i = Long.parseLong(ds.getValue().toString());
                                    Long j = Long.parseLong(calorie_text.getText().toString());

                                    // If the user already entered the amount of food calories taken;
                                    // We add it to that of the drinks
                                    if (drinkSuggestions.getText().toString().equals(ds.getKey())
                                            && Long.parseLong(calorie_text.getText().toString()) != 0) {

                                        calorie_text.setText(String.valueOf(i + j));

                                    }
                                    // If the current key is the same as what we have typed
                                    if (foodSuggestions.getText().toString().matches("")
                                            && drinkSuggestions.getText().toString().equals(ds.getKey())) {
                                        calorie_text.setText(String.valueOf(i));
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
                Snackbar.make(coordinatorLayout, "Can't connect to the internet", Snackbar.LENGTH_SHORT).show();
            }
        });


    }

}
