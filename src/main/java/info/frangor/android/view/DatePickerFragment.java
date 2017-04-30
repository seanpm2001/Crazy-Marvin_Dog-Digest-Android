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

import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.os.Bundle;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * DatePickerFragment class.
 * Inspired on android.com guide:
 * http://developer.android.com/guide/topics/ui/controls/pickers.html
 */
public class DatePickerFragment extends AppCompatDialogFragment
                                implements DatePickerDialog.OnDateSetListener {

    private AppCompatEditText editText;

    public void setEditText(AppCompatEditText editText) {
        this.editText = editText;
    }

    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(
                getActivity().getApplicationContext());
        //DateFormat dateformat = new SimpleDateFormat();
        GregorianCalendar myDate = new GregorianCalendar(year, month, day);
        editText.setText(dateFormat.format(myDate.getTime()));
    }
}
