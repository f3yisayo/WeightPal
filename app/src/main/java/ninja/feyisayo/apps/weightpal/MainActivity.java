package ninja.feyisayo.apps.weightpal;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.firebase.client.*;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Firebase ref = new Firebase("https://weight-pal.firebaseio.com");
    Firebase healthRef = new Firebase("https://weight-pal.firebaseio.com/health");

    // Generated avatar For our account header;
    TextDrawable letterDrawable;
    ColorGenerator generator;

    Drawer drawer;

    SharedPreferences userInfo;

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;
    @InjectView(R.id.add_food_data_btn)
    FloatingActionButton fab;
    @InjectView(R.id.rv)
    RecyclerView rv;
    @InjectView(R.id.mainActivity_header)
    RecyclerViewHeader recyclerViewHeader;

    @InjectView(R.id.welcome_message)
    TextView welcomeText;

    @InjectView(R.id.username)
    TextView userName;

    @InjectView(R.id.calories_consumed)
    TextView calories_taken;

    @InjectView(R.id.target_text)
    TextView target;

    @InjectView(R.id.current_date)
    TextView currentDate;

    AuthData authData;


    private List<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        authData = ref.getAuth();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                startActivity(intent);
            }
        });

        exercises = new ArrayList<Exercise>();

        appDrawer();
        initializeExerciseData();
        setupActivity();
        showDataCards();

    }

    // Fix crash if the user doesn't have data yet
    @Override
    public void onStart(){
        super.onStart();

        healthRef.child(authData.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showDataCards() {

        long date = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
        String dateString = simpleDateFormat.format(date);

        currentDate.setText(dateString);

        authData = ref.getAuth();

        userName.setText(getString(R.string.name_text, userInfo.getString("name", "name")));

        healthRef.child(authData.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("height").exists()) {
                    Intent intent = new Intent(getApplicationContext(), CalorieActivity.class);
                    startActivity(intent);
                }
                else if(dataSnapshot.exists()) {
                    UserHealth uh = dataSnapshot.getValue(UserHealth.class);
                    calories_taken.setText(String.valueOf(uh.getCalories_consumed()));

                    Long i = uh.getCurrent_weight() - uh.getFuture_weight();
                    target.setText(getString(R.string.target, i));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    public void setupActivity() {

        RVAdapter rvAdapter = new RVAdapter(exercises);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);

        llm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setAdapter(rvAdapter);

        recyclerViewHeader.attachTo(rv, true);
    }


    private void initializeExerciseData() {

        String ex1_name = getResources().getString(R.string.ex1_name);
        String ex1_desc = getResources().getString(R.string.ex1_desc);

        String ex3_name = getResources().getString(R.string.ex3_name);
        String ex3_desc = getResources().getString(R.string.ex3_desc);

        String ex4_name = getResources().getString(R.string.ex4_name);
        String ex4_desc = getResources().getString(R.string.ex4_desc);


        exercises.add(new Exercise(ex1_name, ex1_desc, R.drawable.a128));
        exercises.add(new Exercise(ex3_name, ex3_desc, R.drawable.e128));
        exercises.add(new Exercise(ex4_name, ex4_desc, R.drawable.b128));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; adds items to the action bar if its present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void appDrawer() {

        generator = ColorGenerator.MATERIAL;

        int color1 = generator.getRandomColor();

        // Get the first letter in the name of the user for the letter avatar
        String first_letter_in_name = userInfo.getString("name", "name").substring(0, 1);

        letterDrawable = TextDrawable.builder()
                .buildRect(first_letter_in_name, color1);

        IProfile profile = new ProfileDrawerItem()
                .withIcon(letterDrawable)
                .withName(userInfo.getString("name", "name"))
                .withEmail(userInfo.getString("email", "email"));


        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withName(R.string.bmi_calc)
                .withIcon(GoogleMaterial.Icon.gmd_local_hospital)
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedIconColorRes(R.color.md_indigo_300)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_grey_700);

        PrimaryDrawerItem item2 = new PrimaryDrawerItem()
                .withName(R.string.about)
                .withIcon(GoogleMaterial.Icon.gmd_favorite)
                .withSelectedIconColorRes(R.color.md_indigo_300)
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_grey_700);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem()
                .withName(R.string.sign_out)
                .withSelectedIconColorRes(R.color.md_indigo_300)
                .withIcon(GoogleMaterial.Icon.gmd_exit_to_app)
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_grey_700);


        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_bg)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesClickable(false)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(true)
                .withSliderBackgroundColorRes(R.color.md_grey_200)
                .withAccountHeader(accountHeader)
                .withHeaderPadding(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        item2, item1, item3,
                        new DividerDrawerItem())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 2:
                                Intent intent = new Intent(getApplicationContext(), BMIActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                ref.unauth();
                                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent2);
                                break;
                        }
                        return false;
                    }
                })

                .build();

        drawer.setSelection(item2, true);

    }

    @Override
    public void onBackPressed() {

        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            // disable going back to the Login Activity
            moveTaskToBack(true);
        }
    }
}
