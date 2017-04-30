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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import info.frangor.android.Controller;
import info.frangor.android.R;
import info.frangor.android.view.utils.DividerItemDecoration;

/**
 * Treatment List Activity.
 */
public class TreatmentList extends AppCompatActivity
{
    private static final String LOG_TAG = "laicare_appointmentlist";
    private static RecyclerView recyclerView;
    private static AppCompatTextView emptyView;
    private static TreatmentItem treatmentAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.treatments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Controller con = (Controller) getApplicationContext();
        setContentView(R.layout.treatment_list);

        recyclerView = (RecyclerView) findViewById(R.id.treatment_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        treatmentAdapter = new TreatmentItem(con);
        recyclerView.setAdapter(treatmentAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    null));
        emptyView = (AppCompatTextView) findViewById(R.id
                .empty_treatment_view);

        updateTreatmentList();
    }

    /**
     * Click on + button.
     */
    public void showAddTreatmentDialog(View view) {
        TreatmentForm addTreatmentDialog = TreatmentForm.newInstance(-1);
        addTreatmentDialog.show(getSupportFragmentManager(),
                "treatmentform");
    }

    public void updateTreatmentList() {
        final Controller con = (Controller) getApplicationContext();
        treatmentAdapter.notifyDataSetChanged();
        if (con.getTreatmentListSize() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
