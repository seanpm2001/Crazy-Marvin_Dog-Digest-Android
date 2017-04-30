/*   This file is part of LaiCare.
 *
 *   LaiCare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LaiCare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
 */

package info.frangor.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import info.frangor.android.BuildConfig;
import info.frangor.android.Controller;
import info.frangor.android.R;
import info.frangor.android.view.utils.DividerItemDecoration;

/**
 * Pet List Activity.
 */
public class PetList extends AppCompatActivity
{
    private static final String LOG_TAG = "laicare_petlist";
    private static RecyclerView recyclerView;
    private static AppCompatTextView emptyView;
    private static PetItem petAdapter;

    /* Menus */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.manage_treatments:
                intent = new Intent(PetList.this, TreatmentList.class);
                startActivity(intent);
                return true;
            case R.id.preferences:
                intent = new Intent(PetList.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                intent = new Intent(PetList.this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.activity_mypets);

        /* Prevent multiple instances of activity:
         * http://stackoverflow.com/a/7748416 */
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    Intent.ACTION_MAIN.equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.w(LOG_TAG, "Main activity is not the root.");
                finish();
                return;
            }
        }

        final Controller con = (Controller) getApplicationContext();
        setContentView(R.layout.pet_list);

        recyclerView = (RecyclerView) findViewById(R.id.pet_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        petAdapter = new PetItem(con);
        recyclerView.setAdapter(petAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    null));
        emptyView = (AppCompatTextView) findViewById(R.id.empty_pet_view);

        updatePetList();
    }

    /**
     * Click on + button.
     */
    public void showAddPetDialog(View view) {
        PetForm addPetDialog = PetForm.newInstance(-1);
        addPetDialog.show(getSupportFragmentManager(), "petform");
    }

    public void updatePetList() {
        final Controller con = (Controller) getApplicationContext();
        petAdapter.notifyDataSetChanged();
        if (con.getPetListSize() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
