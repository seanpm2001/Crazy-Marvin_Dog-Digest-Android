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

package info.frangor.android.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import info.frangor.android.Controller;
import info.frangor.android.model.Appointment;
import info.frangor.android.model.Pet;
import info.frangor.android.view.PetTabs;
import info.frangor.android.R;

/**
 * Appointments service Class.
 */
public class AppointmentsNotify extends IntentService {
    private static Controller con;
    private static List<Appointment> appointments =
        new ArrayList<Appointment>();

    public AppointmentsNotify() {
        super(AppointmentsNotify.class.getSimpleName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if ( intent == null ) {
            return;
        }
        Pet pet;
        Appointment appointment;
        final Controller con = (Controller) getApplicationContext();
        SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(con);
        if ( !prefs.getBoolean("pref_key_notifications", true) ) {
            return;
        }
        for ( int i=0; i < con.getPetListSize(); i++ ) {
            pet = con.getPet(i);
            Intent nIntent = new Intent(con, PetTabs.class);
            nIntent.putExtra("petId", pet.getId());
            nIntent.putExtra("petName", pet.getName());
            nIntent.setAction(String.valueOf(pet.getId()));
            PendingIntent pIntent = PendingIntent.getActivity(con,
                    0, nIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            appointments = con.getAppointments(pet.getId());
            for ( int a=0; a < appointments.size(); a++ ) {
                appointment = appointments.get(a);
                if ( !appointment.isCovered() ) {
                    NotificationCompat.Builder builder = new NotificationCompat
                        .Builder(con);
                    builder.setSmallIcon(R.drawable.icon_mono);
                    builder.setContentTitle(pet.getName());
                    builder.setContentText(appointment.getTreatment()
                            .getName());
                    builder.setContentIntent(pIntent);
                    final NotificationManager notificationManager =
                        (NotificationManager) getSystemService(
                                NOTIFICATION_SERVICE);
                    notificationManager.notify(String.valueOf(pet.getId()),
                            appointment.getId(),
                            builder.build());
                }
            }
        }
    }
}
