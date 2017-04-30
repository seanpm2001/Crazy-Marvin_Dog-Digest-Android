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

package info.frangor.android.model;

import info.frangor.android.model.utils.DateUtil;

import java.util.Date;

/**
 * Appointment model.
 */
public class Appointment implements Comparable<Appointment> {
    private int id;
    private Date firstDay;
    private Date attended;
    private Treatment treatment;
    private int petId;

    private static final String LOG_TAG = "laicare_mTreatment";

    /**
     * Treatment constructor.
     */
    public Appointment(int id, Date firstDay, Date attended,
            Treatment treatment, int petId) {
        this.id = id;
        this.firstDay = firstDay;
        this.attended = attended;
        this.treatment = treatment;
        this.petId = petId;
    }

    public Appointment(int id, Date firstDay, Treatment treatment, int petId) {
        this.id = id;
        this.firstDay = firstDay;
        this.attended = null;
        this.treatment = treatment;
        this.petId = petId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFirstDay() {
         return firstDay;
    }

    public void setFirstDay(Date firstDay) {
        this.firstDay = firstDay;
    }

    public Date getAttended() {
        return attended;
    }

    public void setAttended(Date attended) {
        this.attended = attended;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public Date nextDate() {
        if (attended == null) {
            return firstDay;
        } else {
            Date today = new Date();
            Date next = DateUtil.addDays(attended, treatment.getPeriod());
            return next;
        }
    }

    /**
     * Return true if appointment not need to be attended yet.
     */
    public boolean isCovered() {
        Date today = new Date();
        Date last = DateUtil.addDays(firstDay, treatment.getTerm());
        if (nextDate().after(today)) {
            return true;
        } else if (treatment.getTerm() != 0 && last.before(nextDate())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Appointment appointment) {
        if (this.nextDate().before(appointment.nextDate())) {
            return -1;
        } else if (this.nextDate().after(appointment.nextDate())) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean save(SQLiteStore sqlStore) {
        boolean result;

        if (this.id < 0) {
            if (this.attended == null) {
                result = sqlStore.execQuery("INSERT INTO appointment "+
                        "(first_day, pet_id, treatment_id) "+
                        "VALUES ('"+ SQLiteStore.dateformat.format(this
                                .firstDay) +"',"+ this.petId +","+ this
                        .treatment.getId() +");");
            } else {
                result = sqlStore.execQuery("INSERT INTO appointment "+
                        "(first_day, pet_id, attended, treatment_id) "+
                        "VALUES ('"+ SQLiteStore.dateformat.format(this
                                .firstDay) +"',"+ this.petId +",'"+
                        SQLiteStore.dateformat.format(this.attended)
                        +"',"+ this.treatment.getId() +");");
            }
        } else {
            if (this.attended == null) {
                result = sqlStore.execQuery("INSERT OR REPLACE INTO "+
                        "appointment (id, first_day, pet_id, treatment_id) "+
                        "VALUES ("+ this.id +",'"+
                        SQLiteStore.dateformat.format(this.firstDay)
                        +"',"+ this.petId +","+ this.treatment.getId()
                        +");");
            } else {
                result = sqlStore.execQuery("INSERT OR REPLACE INTO "+
                        "appointment (id, first_day, pet_id, attended, "+
                        "treatment_id) VALUES ("+ this.id +",'"+
                        SQLiteStore.dateformat.format(this.firstDay) +"',"+
                        this.petId +",'"+ SQLiteStore.dateformat
                        .format(this.attended) +"',"+
                        this.treatment.getId() +");");
            }
        }
        return result;
    }
}
