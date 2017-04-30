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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import info.frangor.android.Controller;
import info.frangor.android.R;
import info.frangor.android.model.Appointment;
import info.frangor.android.model.CheckPoint;

/**
 * Pet Tabs Activity.
 */
public class PetTabs extends AppCompatActivity
{
    private static final String LOG_TAG = "laicare_petTabs";
    private int petId;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CheckPointList checkPointList;
    private AppointmentList appointmentList;

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

        final Controller con = (Controller) getApplicationContext();
        setContentView(R.layout.pet_tabs);

        Intent intent = getIntent();
        petId = intent.getIntExtra("petId", -1);
        String petName = intent.getStringExtra("petName");

        getSupportActionBar().setTitle(petName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public int getPetId() {
        return petId;
    }

    private void setupViewPager(ViewPager viewPager) {
        checkPointList = CheckPointList.newInstance(petId);
        appointmentList = AppointmentList.newInstance(petId);

        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());
        adapter.addFragment(appointmentList, getString(
                    R.string.appointments_tab));
        adapter.addFragment(checkPointList, getString(
                    R.string.monitoring_tab));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    /**
     * Click on + button.
     */
    public void showAddCheckPointDialog(View view) {
        CheckPointForm addCheckPointdialog = CheckPointForm.newInstance(petId,
                -1);
        addCheckPointdialog.show(getSupportFragmentManager(),
                "checkpointform");
    }

    public void showAddAppointmentDialog(View view) {
        final Controller con = (Controller) getApplicationContext();
        if (con.getTreatments(petId).size() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    view.getContext());

            alertDialogBuilder.setTitle(getString(android.R.string
                        .dialog_alert_title))
                .setMessage(getString(R.string.no_treatments))
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                dialog.dismiss();
                            }
                        });
                alertDialogBuilder.show();
        } else {
            AppointmentForm addAppointmentDialog = AppointmentForm
                .newInstance(petId, -1);
            addAppointmentDialog.show(getSupportFragmentManager(),
                    "appointmentform");
        }
    }

    public void updateCheckPointList() {
        checkPointList.update();
    }

    public CheckPoint getCheckPoint(int position) {
        return checkPointList.getCheckPoint(position);
    }

    public void updateAppointmentList() {
        appointmentList.update();
    }

    public Appointment getAppointment(int position) {
        return appointmentList.getAppointment(position);
    }
}
