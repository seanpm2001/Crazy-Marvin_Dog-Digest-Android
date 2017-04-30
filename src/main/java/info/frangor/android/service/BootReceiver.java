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

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *  Launch Service sheduler.
 *  From: http://www.vogella.com/tutorials/AndroidServices/article.html
 */
public class BootReceiver extends BroadcastReceiver {
    private static final long REPEAT_TIME = 1000 * 60 * 60;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            AlarmManager service = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, StartAppointments.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 30);
            service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), REPEAT_TIME, pending);
        }
    }
}
