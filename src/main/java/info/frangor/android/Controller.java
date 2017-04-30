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

package info.frangor.android;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import info.frangor.android.exceptions.NoAddedItem;
import info.frangor.android.exceptions.NoDeletedItem;
import info.frangor.android.model.CheckPoint;
import info.frangor.android.model.Pet;
import info.frangor.android.model.SQLiteStore;
import info.frangor.android.model.Appointment;
import info.frangor.android.model.Treatment;

/**
 * Controller Class.
 */
public class Controller extends Application {
    private static SQLiteStore db;
    private List<Pet> pets = new ArrayList<Pet>();
    private List<Treatment> treatments = new ArrayList<Treatment>();

    @Override
    public void onCreate() {
        super.onCreate();
        db = SQLiteStore.getInstance(this);
        pets = db.getPets();
        treatments = db.getTreatments();
    }

    /**
     * Get the pet from a position in the pet list.
     */
    public Pet getPet(int position) {
        return pets.get(position);
    }

    /**
     * Get number of pets.
     */
    public int getPetListSize() {
        return pets.size();
    }

    /**
     * Save a pet.
     */
    public void addPet(Pet pet) throws NoAddedItem {
        if (pet.save(db)) {
            pets = db.getPets();
        } else {
            throw new NoAddedItem("Could not add Pet with id "+
                    String.valueOf(pet.getId()));
        }
    }

    /**
     * Delete a pet.
     */
    public void delPet(int petId) throws NoDeletedItem {
        if (db.delPet(petId)) {
            pets = db.getPets();
        } else {
            throw new NoDeletedItem("Could not delete Pet with id "+
                    String.valueOf(petId));
        }
    }

    /**
     * Get the treatment from a position in the treatment list.
     */
    public Treatment getTreatment(int position) {
        return treatments.get(position);
    }

    /**
     * Get number of treatments.
     */
    public int getTreatmentListSize() {
        return treatments.size();
    }

    /**
     * Save a treatment.
     */
    public void addTreatment(Treatment treatment) throws NoAddedItem {
        if (treatment.save(db)) {
            treatments = db.getTreatments();
        } else {
            throw new NoAddedItem("Could not add Treatment with id "+
                    String.valueOf(treatment.getId()));
        }
    }

    /**
     * Delete a treatment.
     */
    public void delTreatment(int treatmentId) throws NoDeletedItem {
        if (db.delTreatment(treatmentId)) {
            treatments = db.getTreatments();
        } else {
            throw new NoDeletedItem("Could not delete Treatment with id "+
                    String.valueOf(treatmentId));
        }
    }

    /**
     * Get list of Treatments available for a Pet.
     */
    public List<Treatment> getTreatments(int petId) {
        return db.getTreatments(petId);
    }

    /**
     * Get list of CheckPoints.
     */
    public List<CheckPoint> getCheckPoints(int petId) {
        return db.getCheckPoints(petId);
    }

    /**
     * Save a CheckPoint.
     */
    public void addCheckPoint(CheckPoint checkPoint) throws NoAddedItem {
        if(!checkPoint.save(db)) {
            throw new NoAddedItem("Could not add CheckPoint with id "+
                    String.valueOf(checkPoint.getId()));
        }
    }

    /**
     * Delete a CheckPoint.
     */
    public void delCheckPoint(int checkPointId) throws NoDeletedItem {
        if (!db.delCheckPoint(checkPointId)) {
            throw new NoDeletedItem("Could not delete CheckPoint with id "+
                    String.valueOf(checkPointId));
        }
    }

    /**
     * Get list of Appointments.
     */
    public List<Appointment> getAppointments(int petId) {
        return db.getAppointments(petId);
    }

    /**
     * Save an Appointment.
     */
    public void addAppointment(Appointment appointment) throws NoAddedItem {
        if(!appointment.save(db)) {
            throw new NoAddedItem("Could not add Appointment with id "+
                    String.valueOf(appointment.getId()));
        }
    }

    /**
     * Delete an Appointment.
     */
    public void delAppointment(int appointmentId) throws NoDeletedItem {
        if (!db.delAppointment(appointmentId)) {
            throw new NoDeletedItem("Could not delete Appointment with id "+
                    String.valueOf(appointmentId));
        }
    }
}
