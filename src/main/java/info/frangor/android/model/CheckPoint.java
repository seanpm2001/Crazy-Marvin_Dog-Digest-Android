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

import java.util.Date;

/**
 * CheckPoint model.
 */
public class CheckPoint {
    private int id;
    private int grams;
    private Date date;
    private int petId;
    private static final String LOG_TAG = "laicare_mCheckPoint";

    /**
     * CheckPoint constructor.
     */
    public CheckPoint(int id, int grams, Date date, int petId) {
        this.id = id;
        this.grams = grams;
        this.date = date;
        this.petId = petId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrams() {
         return grams;
    }

    public void setGrams(int grams) {
        this.grams = grams;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public boolean save(SQLiteStore sqlStore) {
        boolean result;

        if (this.id < 0) {
            result = sqlStore.execQuery("INSERT INTO checkpoint "+
                    "(grams, check_date, pet_id) "+
                    "VALUES ("+ this.grams +",'"+
                    SQLiteStore.dateformat.format(this.date) +"',"+
                    this.petId +");");
        } else {
            result = sqlStore.execQuery("INSERT OR REPLACE INTO checkpoint "+
                    "(id, grams, check_date, pet_id) "+
                    "VALUES ("+ this.id +","+ this.grams +",'"+
                    SQLiteStore.dateformat.format(this.date) +"',"+
                    this.petId +");");
        }
        return result;
    }
}
