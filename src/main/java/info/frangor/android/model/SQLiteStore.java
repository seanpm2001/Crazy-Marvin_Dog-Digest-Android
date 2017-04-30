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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

import info.frangor.android.BuildConfig;
import info.frangor.android.R;

/**
 * SQLite store class.
 * Inspired on DroidShows code:
 * https://github.com/ltGuillaume/DroidShows
 */
public class SQLiteStore extends SQLiteOpenHelper {
    public static final SimpleDateFormat dateformat =
        new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SQLiteStore instance = null;
    private SQLiteDatabase db;
    private static String DB_PATH = "";
    private static int DB_VERSION = 1;
    private static final String DB_NAME = "LaiCare.db";
    private static final String LOG_TAG = "laicare_mSQLiteStore";
    private Context context;

    /**
     * Get the SQLite instance.
     */
    public static SQLiteStore getInstance(Context context) {
        if (instance == null)
        {
            instance = new SQLiteStore(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor.
     */
    private SQLiteStore(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.context = context;
        try {
            openDataBase();
        } catch (SQLException e) {
            try {
                createDataBase();
                close();
                try {
                    openDataBase();
                } catch (SQLException e2) {
                    if (BuildConfig.DEBUG)
                        Log.e(LOG_TAG, e2.getMessage());
                }
            } catch (IOException e3) {
                if (BuildConfig.DEBUG)
                    Log.e(LOG_TAG, "Unable to create database");
            }
        }
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getWritableDatabase();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "Database doesn't exist yet.");
        }
        if (checkDB != null) {
            checkDB.close();
            return true;
        } else {
            return false;
        }
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                SQLiteDatabase.OPEN_READWRITE);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public boolean execQuery(String query) {
        try {
            db.execSQL(query);
        } catch (SQLException e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public Cursor Query(String query) {
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
        } catch (SQLiteException e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            return null;
        }
        return c;
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase dbase) {
        dbase.execSQL("CREATE TABLE IF NOT EXISTS pet ("
                +"id INTEGER CHECK(id>=0) PRIMARY KEY,"
                +"name TEXT NOT NULL,"
                +"birthday TEXT,"
                +"specie TEXT CHECK(specie IN('CAT','DOG')) NOT NULL,"
                +"UNIQUE(name, birthday));");

        dbase.execSQL("CREATE TABLE IF NOT EXISTS treatment ("
                +"id INTEGER CHECK(id>=0) PRIMARY KEY,"
                +"name TEXT NOT NULL,"
                +"period INTEGER NOT NULL,"
                +"term INTEGER NOT NULL,"
                +"place TEXT CHECK(place IN"
                    +"('HOME','VET','TRAINING','OTHERS')) NOT NULL,"
                +"specie TEXT CHECK(specie IN('CAT','DOG')),"
                +"UNIQUE(name));");

        dbase.execSQL("CREATE TABLE IF NOT EXISTS appointment ("
                +"id INTEGER CHECK(id>=0) PRIMARY KEY,"
                +"first_day TEXT NOT NULL,"
                +"attended TEXT "
                +"CHECK(attended IS NULL OR attended >= first_day),"
                +"pet_id INTEGER NOT NULL REFERENCES pet ON DELETE CASCADE,"
                +"treatment_id INTEGER NOT NULL REFERENCES treatment "
                    +"ON DELETE CASCADE,"
                +"UNIQUE(pet_id, treatment_id, first_day));");

        dbase.execSQL("CREATE TABLE IF NOT EXISTS checkpoint ("
                +"id INTEGER CHECK(id>=0) PRIMARY KEY,"
                +"grams INTEGER NOT NULL,"
                +"check_date TEXT NOT NULL,"
                +"pet_id INTEGER NOT NULL REFERENCES pet ON DELETE CASCADE,"
                +"UNIQUE(check_date, pet_id));");

        // Initial data for treatments.
        dbase.execSQL("INSERT INTO treatment (name, period, term, place) "
                +"VALUES ('"+ context.getResources()
                .getString(R.string.quadrivalent_vaccine)
                +"', 365, 0, 'VET');");

        dbase.execSQL("INSERT INTO treatment (name, period, term, place) "
                +"VALUES ('"+ context.getResources()
                .getString(R.string.rabies_vaccine)
                +"', 365, 0, 'VET');");

        dbase.execSQL("INSERT INTO treatment (name, period, term, place) "
                +"VALUES ('"+ context.getResources()
                .getString(R.string.deworming)
                +"', 90, 0, 'HOME');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase dbase, int oldVersion, int newVersion)
    {
        /* TODO */
    }

    /**
     * Get a pet list from sqlite database.
     * @return List of pets.
     */
    public List<Pet> getPets() {
        List<Pet> pets = new ArrayList<Pet>();
        Cursor cursor = Query("SELECT id, name, birthday, specie "
                +"FROM pet ORDER BY name, birthday;");
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                int idCol = cursor.getColumnIndex("id");
                int nameCol = cursor.getColumnIndex("name");
                int birthdayCol = cursor.getColumnIndex("birthday");
                int specieCol = cursor.getColumnIndex("specie");
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    do {
                        int id = cursor.getInt(idCol);
                        String name = cursor.getString(nameCol);
                        Date birthday = dateformat.parse(
                                cursor.getString(birthdayCol));
                        Pet.Specie specie = Pet.Specie.valueOf(
                                cursor.getString(specieCol));
                        pets.add(new Pet(id, name, birthday, specie));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SQLiteException | ParseException e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            cursor.close();
        }
        return pets;
    }

    /**
     * Get a treatment list from sqlite database.
     * @return List of treatments.
     */
    public List<Treatment> getTreatments() {
        List<Treatment> treatments = new ArrayList<Treatment>();
        Cursor cursor = Query("SELECT id, name, period, term, place, specie "
                +"FROM treatment ORDER BY name, specie;");
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                int idCol = cursor.getColumnIndex("id");
                int nameCol = cursor.getColumnIndex("name");
                int periodCol = cursor.getColumnIndex("period");
                int termCol = cursor.getColumnIndex("term");
                int placeCol = cursor.getColumnIndex("place");
                int specieCol = cursor.getColumnIndex("specie");
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    do {
                        int id = cursor.getInt(idCol);
                        String name = cursor.getString(nameCol);
                        int period = cursor.getInt(periodCol);
                        int term = cursor.getInt(termCol);
                        Treatment.Place place = Treatment.Place.valueOf(
                                cursor.getString(placeCol));
                        Pet.Specie specie = null;
                        if (cursor.getString(specieCol) != null) {
                            specie = Pet.Specie.valueOf(cursor.getString(
                                        specieCol));
                        }
                        treatments.add(new Treatment(id, name, period, term,
                                    place, specie));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            cursor.close();
        }
        return treatments;
    }

    /**
     * Get a treatment list available for a pet from sqlite database.
     * @return List of treatments.
     */
    public List<Treatment> getTreatments(int petId) {
        List<Treatment> treatments = new ArrayList<Treatment>();
        Cursor cursor = Query("SELECT id, name, period, term, place, specie "
                +"FROM treatment WHERE specie IS NULL OR "
                +"specie = (SELECT specie FROM pet WHERE id="+ petId +") "
                +"ORDER BY name;");
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                int idCol = cursor.getColumnIndex("id");
                int nameCol = cursor.getColumnIndex("name");
                int periodCol = cursor.getColumnIndex("period");
                int termCol = cursor.getColumnIndex("term");
                int placeCol = cursor.getColumnIndex("place");
                int specieCol = cursor.getColumnIndex("specie");
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    do {
                        int id = cursor.getInt(idCol);
                        String name = cursor.getString(nameCol);
                        int period = cursor.getInt(periodCol);
                        int term = cursor.getInt(termCol);
                        Treatment.Place place = Treatment.Place.valueOf(
                                cursor.getString(placeCol));
                        Pet.Specie specie = null;
                        if (cursor.getString(specieCol) != null) {
                            specie = Pet.Specie.valueOf(cursor.getString(
                                        specieCol));
                        }
                        treatments.add(new Treatment(id, name, period, term,
                                    place, specie));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            cursor.close();
        }
        return treatments;
    }

    /**
     * Get a checkpoint list of a pet from sqlite database.
     * @param petId Pet identify.
     * @return List of checkpoints
     */
    public List<CheckPoint> getCheckPoints(int petId) {
        List<CheckPoint> checkpoints = new ArrayList<CheckPoint>();
        Cursor cursor = Query("SELECT id, grams, check_date, pet_id "
                +"FROM checkpoint WHERE pet_id="+ petId +" "
                +"ORDER BY check_date ASC;");
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                int idCol = cursor.getColumnIndex("id");
                int gramsCol = cursor.getColumnIndex("grams");
                int dateCol = cursor.getColumnIndex("check_date");
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    do {
                        int id = cursor.getInt(idCol);
                        int grams = cursor.getInt(gramsCol);
                        Date date = dateformat.parse(
                            cursor.getString(dateCol));
                        checkpoints.add(new CheckPoint(id, grams, date,
                                    petId));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            cursor.close();
        }
        return checkpoints;
    }


    /**
     * Get a list of appointments of a pet from sqlite database.
     * @param petId Pet identify.
     * @return list of treatments.
     */
     public List<Appointment> getAppointments(int petId) {
        List<Appointment> appointments = new ArrayList<Appointment>();
        Cursor cursor = Query("SELECT appointment.id, first_day, attended, "
                +"pet_id, treatment_id, name, period, term, place, specie "
                +"FROM appointment JOIN treatment ON "
                +"treatment_id = treatment.id "
                +"WHERE pet_id = "+ petId +" ORDER BY name;");
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                int idCol = cursor.getColumnIndex("id");
                int fdayCol = cursor.getColumnIndex("first_day");
                int attendedCol = cursor.getColumnIndex("attended");
                int pidCol = cursor.getColumnIndex("pet_id");
                int tidCol = cursor.getColumnIndex("treatment_id");
                int tnameCol = cursor.getColumnIndex("name");
                int tperiodCol = cursor.getColumnIndex("period");
                int ttermCol = cursor.getColumnIndex("term");
                int tplaceCol = cursor.getColumnIndex("place");
                int tspecieCol = cursor.getColumnIndex("specie");
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    do {
                        int id = cursor.getInt(idCol);
                        Date firstDay = dateformat.parse(
                                cursor.getString(fdayCol));
                        Date attended = null;
                        if (!cursor.isNull(attendedCol)) {
                            attended = dateformat.parse(cursor
                                    .getString(attendedCol));
                        }
                        int treatmentId = cursor.getInt(tidCol);
                        String name = cursor.getString(tnameCol);
                        int period = cursor.getInt(tperiodCol);
                        int term = cursor.getInt(ttermCol);
                        Treatment.Place place = Treatment.Place.valueOf(
                                cursor.getString(tplaceCol));
                        Pet.Specie specie = null;
                        if (!cursor.isNull(tspecieCol)) {
                            specie = Pet.Specie.valueOf(cursor
                                    .getString(tspecieCol));
                        }
                        Treatment treatment = new Treatment(treatmentId,
                                name, period, term, place, specie);
                        appointments.add(new Appointment(id, firstDay,
                                    attended, treatment, petId));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SQLiteException | ParseException e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            cursor.close();
        }
        return appointments;
     }

    /**
     * Delete a pet.
     * @param petId Pet identify.
     * @return True if pet is deleted.
     */
    public boolean delPet(int petId) {
        return this.execQuery("DELETE FROM pet WHERE id="+ petId +";");
    }

    /**
     * Delete a treatment.
     * @param treatmentId Treatment identify.
     * @return True if treatment is deleted.
     */
    public boolean delTreatment(int treatmentId) {
        return this.execQuery("DELETE FROM treatment WHERE id="+
                treatmentId +";");
    }

    /**
     * Delete a checkpoint.
     * @param checkPointId CheckPoint identify.
     * @return True if checkpoint is deleted.
     */
    public boolean delCheckPoint(int checkPointId) {
        return this.execQuery("DELETE FROM checkpoint WHERE id="+
                checkPointId +";");
    }

    /**
     * Delete an appointment.
     * @param appointmentId Appointment identify.
     * @return True if appointment is deleted.
     */
    public boolean delAppointment(int appointmentId) {
        return this.execQuery("DELETE FROM appointment WHERE id="+
                appointmentId +";");
    }
}
