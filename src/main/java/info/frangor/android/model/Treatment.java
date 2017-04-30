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

/**
 * Treatment model.
 */
public class Treatment {
    /**
     * Places.
     */
    public enum Place {
        HOME,
        VET,
        TRAINING,
        OTHERS
    }

    private int id;
    private String name;
    private int period;
    private int term;
    private Place place;
    private Pet.Specie specie = null;
    private static final String LOG_TAG = "laicare_mTreatment";

    /**
     * Treatment constructor.
     */
    public Treatment(int id, String name, int period, int term, Place place,
            Pet.Specie specie) {
        this.id = id;
        this.name = name;
        this.period = period;
        this.term = term;
        this.place = place;
        this.specie = specie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
         return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Pet.Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Pet.Specie specie) {
        this.specie = specie;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    /**
     * Save treatment on sqlite store.
     */
    public boolean save(SQLiteStore sqlStore) {
        boolean result;

        if (this.id < 0) {
            if (this.specie != null) {
                result = sqlStore.execQuery("INSERT INTO treatment "+
                    "(name, period, term, specie, place) "+
                    "VALUES ('"+ this.name +"',"+ this.period +","+
                    this.term +",'"+ this.specie.name() +"','"+
                    this.place.name() +"');");
            } else {
                result = sqlStore.execQuery("INSERT INTO treatment "+
                    "(name, period, term, place) "+
                    "VALUES ('"+ this.name +"',"+ this.period +","+
                    this.term +",'"+ this.place.name() +"');");
            }
        } else {
            if (this.specie != null) {
                result = sqlStore.execQuery("INSERT OR REPLACE INTO treatment "+
                        "(id, name, period, term, specie, place) "+
                        "VALUES ("+ this.id +",'"+ this.name +"',"+
                        this.period +","+ this.term +",'"+
                        this.specie.name() +"','"+ this.place.name()
                        +"');");
            } else {
                result = sqlStore.execQuery("INSERT OR REPLACE INTO treatment "+
                        "(id, name, period, term, place) "+
                        "VALUES ("+ this.id +",'"+ this.name +"',"+
                        this.period +","+ this.term +",'"+
                        this.place.name() +"');");
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
