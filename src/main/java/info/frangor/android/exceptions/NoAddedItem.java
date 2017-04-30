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

package info.frangor.android.exceptions;

/**
 * NoAddedItem exception.
 */
public class NoAddedItem extends Exception {

    public NoAddedItem() {
        super();
    }

    public NoAddedItem(String message) {
        super(message);
    }

    public NoAddedItem(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAddedItem(Throwable cause) {
         super(cause);
    }
}
