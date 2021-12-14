package com.example.android.pets.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.pets.data.PetContract.PetEntry;
import org.w3c.dom.Comment;
/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {
	  private PetDbHelper  mDbHelper;
	  private static final int PETS = 100;
	  private static final int PET_ID = 101;

	  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	  // static initializer
	  static {

			sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
			sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);


	  }
	  /** Tag for the log messages */
	  public static final String LOG_TAG = PetProvider.class.getSimpleName();

	  /**
	   * Initialize the provider and the database helper object.
	   */
	  @Override
	  public boolean onCreate() {
			mDbHelper = new PetDbHelper(getContext());
			return true;
	  }

	  /**
	   * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
	   */
	  @Override
	  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
						  String sortOrder) {

			SQLiteDatabase database = mDbHelper.getReadableDatabase();

			Cursor cursor;

			int match = sUriMatcher.match(uri);
			switch (match) {

				  case PETS:
						cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
												null, null, sortOrder);
						break;
				  case PET_ID:

						selection = PetEntry._ID + "=?";
						selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
						cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
												null, null, sortOrder);
						break;
				  default:
						throw new IllegalArgumentException("Cannot Query unknown uri " + uri);
			}
			//ethe eh ta ditta bcoz automatic load nhi ho reaha c insert
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
	  }

	  /**
	   * Insert new data into the provider with the given ContentValues.
	   */
	  @Override
	  public Uri insert(Uri uri, ContentValues contentValues) {
			final int match = sUriMatcher.match(uri);
			switch (match) {
				  case PETS:
						return insertPet(uri, contentValues);
				  default:
						throw new IllegalArgumentException("Insertion is not supported for " + uri);
			}
	  }

	  /**
	   * Insert a pet into the database with the given content values. Return the new content URI
	   * for that specific row in the database.
	   */
	  /*   eh 277 ch Comment kita c 
	   private Uri insertPet(Uri uri, ContentValues values) {
	   SQLiteDatabase database = mDbHelper.getWritableDatabase();
	   long id = database.insert(PetEntry.TABLE_NAME, null, values);
	   if (id == -1) {

	   Log.e(LOG_TAG, "failed to insert new row" + uri);
	   return null;
	   }
	   return ContentUris.withAppendedId(uri, id);
	   } */

	  private Uri insertPet(Uri uri, ContentValues values) {

			//check the name is not null
			String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
			if (name == null) {
				  throw new IllegalArgumentException("Pet requires a name");
			}

			//gender is valid or not
			Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
			if (gender == null || !PetEntry.isValidGender(gender)) {
				  throw new IllegalArgumentException("Requires valid gender");
			}

			// If the weight is provided, check that it's greater than or equal to 0 kg
			Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
			if (weight != null && weight < 0) {
				  throw new IllegalArgumentException("Requires valid weight");

			}
			// No need to check the breed, any value is valid (including null).


			SQLiteDatabase database = mDbHelper.getWritableDatabase();

			long id = database.insert(PetEntry.TABLE_NAME, null, values);
			// If the ID is -1, then the insertion failed. Log an error and return null.
			if (id == -1) {
				  Log.e(LOG_TAG, "failed to insert new row" + uri);
				  return null;
			}

			// Notify all listeners that the data has changed for the pet content URI
			getContext().getContentResolver().notifyChange(uri, null);
			// Once we know the ID of the new row in the table,
			// return the new URI with the ID appended to the end of it
			return ContentUris.withAppendedId(uri, id);

	  }

	  /**
	   * Updates the data at the given selection and selection arguments, with the new ContentValues.
	   */
	  @Override
	  public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

			final int match = sUriMatcher.match(uri);
			switch (match) {
				  case PETS:
						return update(uri, contentValues, selection, selectionArgs);

				  case PET_ID:
						// For the PET_ID code, extract out the ID from the URI,
						// so we know which row to update. Selection will be "_id=?" and selection
						// arguments will be a String array containing the actual ID.
						selection = PetEntry._ID + "=?";
						selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
						return updatePet(uri, contentValues, selection, selectionArgs);

				  default:
						throw new IllegalArgumentException("update is not Supported for this" + uri);
			}
	  }

	  /**
	   * Update pets in the database with the given content values. Apply the changes to the rows
	   * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
	   * Return the number of rows that were successfully updated.
	   */
	  private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
			// If the {@link PetEntry#COLUMN_PET_NAME} key is present,
			// check that the name value is not null.
			if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
				  String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
				  if (name == null) {
						throw new IllegalArgumentException("pet requires name");
				  }

			}
			// If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
			// check that the gender value is valid.
			if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
				  Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
				  if (gender == null || !PetEntry.isValidGender(gender)) {
						throw new IllegalArgumentException(" Valid gender required");
				  }
			}
			// If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
			// check that the weight value is valid.
			if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
				  Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
				  if (weight != null && weight < 0) {
						throw new IllegalArgumentException("Pet requires valid weight");
				  }
			}
			if (values.size() == 0) {
				  return 0;
			}

			// No need to check the breed, any value is valid (including null).
			SQLiteDatabase database = mDbHelper.getWritableDatabase();
			//eh 287 ch line likhi a a
            int rowUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
			//if 1 or more row uodated, Notify all listener that the data at the given URI has change
			if (rowUpdated != 0) {
				  getContext().getContentResolver().notifyChange(uri, null);
			}
			return rowUpdated;
			// eh lec 277 ch comment kita
			// Returns the number of database rows affected by the update statement
			//return database.update(PetEntry.TABLE_NAME,values,selection,selectionArgs);
	  }

	  /**
	   * Delete the data at the given selection and selection arguments.
	   */
	  @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {

			// Get writeable database
			SQLiteDatabase database = mDbHelper.getWritableDatabase();
			// Track the number of rows that were deleted
			int rowsDeleted;

			final int match = sUriMatcher.match(uri);
			switch (match) {

				  case PETS:
						// Delete all rows that match the selection and selection args
						rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
						break;

				  case PET_ID:
						// Delete a single row given by the ID in the URI
						selection = PetEntry._ID + "=?";
						selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
						rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
						break;
				  default:
						throw new IllegalArgumentException("Deletion is not supported for" + uri);

			}
			// If 1 or more rows were deleted, then notify all listeners that the data at the
			// given URI has changed
			if (rowsDeleted != 0) {
				  getContext().getContentResolver().notifyChange(uri, null);
			}
			return rowsDeleted;
	  }

	  /**
	   * Returns the MIME type of data for the content URI.
	   */
	  @Override
	  public String getType(Uri uri) {
			return null;
	  }
}



