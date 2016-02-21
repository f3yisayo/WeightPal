package ninja.feyisayo.apps.weightpal;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.client.*;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


public class MainActivity extends AppCompatActivity {

    Firebase ref = new Firebase("https://weight-pal.firebaseio.com");

    AuthData authData;

    // Generated avatar For our account header;
    TextDrawable letterDrawable;
    ColorGenerator generator;

    SharedPreferences userInfo;

    @InjectView(R.id.tool_bar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        userInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        appDrawer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; adds items to the action bar if its present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.help:
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_indigo_600);

        PrimaryDrawerItem item2 = new PrimaryDrawerItem()
                .withName(R.string.about)
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_indigo_600);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem()
                .withName(R.string.sign_out)
                .withTextColorRes(R.color.md_grey_800)
                .withSelectedColorRes(R.color.md_grey_300)
                .withSelectedTextColorRes(R.color.md_indigo_600);


        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_bg)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesClickable(false)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(true)
                .withSliderBackgroundColorRes(R.color.md_grey_200)
                .withAccountHeader(accountHeader)
                .withHeaderPadding(true)
                .addDrawerItems(
                        item2, item1, item3,
                        new DividerDrawerItem()

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        return false;
                    }
                })

                .build();

        drawer.setSelection(item1, true);

    }

    @Override
    public void onBackPressed() {
        // disable going back to the Login Activity
        moveTaskToBack(true);
    }
}
