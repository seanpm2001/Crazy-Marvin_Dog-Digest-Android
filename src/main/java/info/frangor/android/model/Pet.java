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
 * Pet model.
 */
public class Pet {
    /**
     * Types of species.
     */
    public enum Specie {
        DOG,
        CAT
    }

    private int id;
    private String name;
    private Date birthday;
    private Specie specie;
    private static final String LOG_TAG = "laicare_mPet";

    /**
     * Pet constructor.
     */
    public Pet(int id, String name, Date birthday, Specie specie) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Specie specie) {
        this.specie = specie;
    }

    /**
     * Save the pet on the sqlite store.
     */
    public boolean save(SQLiteStore sqlStore) {
        boolean result;

        if (this.id < 0) {
            result = sqlStore.execQuery("INSERT INTO pet "+
                    "(name, birthday, specie) "+
                    "VALUES ('"+ this.name +"','"+
                    SQLiteStore.dateformat.format(this.birthday) +"','"+
                    this.specie.name() +"');");
        } else {
            result = sqlStore.execQuery("INSERT OR REPLACE INTO pet "+
                    "(id, name, birthday, specie) "+
                    "VALUES ("+ this.id +",'"+ this.name +"','"+
                    SQLiteStore.dateformat.format(this.birthday) +"','"+
                    this.specie.name() +"');");
        }
        return result;
    }
}
