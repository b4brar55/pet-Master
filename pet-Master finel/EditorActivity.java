/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.pets.data.PetContract.PetEntry;
import org.w3c.dom.Comment;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	  // eh apa catalog de vich v bnaya c 
	  //identified for the pet loader
	  private static final int EXISTING_PET_LOADER = 0;

	  /** EditText field to enter the pet's name */
	  private EditText mNameEditText;

	  /** EditText field to enter the pet's breed */
	  private EditText mBreedEditText;
	  /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
	  private boolean mPetHasChanged = false ;
	  private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				  // jdo apa kise view te eh onTouch set krage ta
				  // view & MotionEvent perameters ha onTouch de
				  // view existing textFiled de changes nu record krda & view de sare information rakhda
				  // motion 1event to 2event t move krda & kithe change & move hoea usdi information rakhda
				  mPetHasChanged = true;
				  //why return false[lec289]bcoz
				  //eh touch event handle kru
				  //apa eh onTouch sare Fields te chaheda ha
				  //j eh true hoea ta oh sirf phle View(field) te h check kru
				  //false nal sarea field te he check kru
				  // eh onTouch apa 4 te set kita j return true hunda ta sirf
				  //1st he check hunda
				  return false;
			}

	  };

	  /** EditText field to enter the pet's weight */
	  private EditText mWeightEditText;

	  /** EditText field to enter the pet's gender */
	  private Spinner mGenderSpinner;
	  /**
	   * Content URI for the existing pet (null if it's a new pet)
	   */
	  private Uri mCurrentPetUri;
	  /**
	   * Gender of the pet. The possible values are:
	   * 0 for unknown gender, 1 for male, 2 for female.
	   ***/
	  private int mGender = 0;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_editor);

			Intent intent = getIntent();
			mCurrentPetUri = intent.getData();

			//If the intent DOES NOT contain a pet content URI , then we know that we
			//creating a new pet.
			if (mCurrentPetUri == null) {
				  setTitle(R.string.editor_activity_title_new_pet);
				  //lec 294
				  // Invalidate the options menu, so the "Delete" menu option can be hidden.
				  // (It doesn't make sense to delete a pet that hasn't been created yet.)
				  invalidateOptionsMenu();
			} else {
				  setTitle(getString(R.string.editor_activity_title_edit_pet));

				  // Initialize a loader to read the pet data from the database
				  // and display the current values in the editor
				  getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
			}
			// Find all relevant views that we will need to read user input from
			mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
			mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
			mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
			mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
			// lec 289 onTouch set krta uppar wali fields te
			mNameEditText.setOnTouchListener(mTouchListener);
			mBreedEditText.setOnTouchListener(mTouchListener);
			mWeightEditText.setOnTouchListener(mTouchListener);
			mGenderSpinner.setOnTouchListener(mTouchListener);
			setupSpinner();

	  }

	  /**
	   * Setup the dropdown spinner that allows the user to select the gender of the pet.
	   */
	  private void setupSpinner() {
			// Create adapter for spinner. The list options are from the String array it will use
			// the spinner will use the default layout
			ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
																				R.array.array_gender_options, android.R.layout.simple_spinner_item);

			// Specify dropdown layout style - simple list view with 1 item per line
			genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

			// Apply the adapter to the spinner
			mGenderSpinner.setAdapter(genderSpinnerAdapter);

			// Set the integer mSelected to the constant values
			mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							  String selection = (String) parent.getItemAtPosition(position);
							  if (!TextUtils.isEmpty(selection)) {
									if (selection.equals(getString(R.string.gender_male))) {
										  mGender = PetEntry.GENDER_MALE; // Male
									} else if (selection.equals(getString(R.string.gender_female))) {
										  mGender = PetEntry.GENDER_FEMALE; // Female
									} else {
										  mGender = PetEntry.GENDER_UNKNOWN; // Unknown
									}
							  }
						}

						// Because AdapterView is an abstract class, onNothingSelected must be defined
						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							  mGender = 0; // Unknown
						}
				  });
	  }

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu options from the res/menu/menu_editor.xml file.
			// This adds menu items to the app bar.
			getMenuInflater().inflate(R.menu.menu_editor, menu);
			return true;
	  }
	  //change name in insertPet to savePet ,lec 285
	  //Get user input from editor and save pet into database
	  private void savePet() {
			String nameString = mNameEditText.getText().toString().trim();
			String breedString = mBreedEditText.getText().toString().trim();
			String weightString = mWeightEditText.getText().toString().trim();
			//lec 287
			//eh wieght apa comment kita bcoz apa direct check kr rhe a v 
			// ki inputField empty ha ja nhi ja empty a t rerurn hoje
			//weight di apa ek default value set kita a
			//int weight = Integer.parseInt(weightString);
			if (mCurrentPetUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString)
				&& TextUtils.isEmpty(weightString) && mGender == PetEntry.GENDER_UNKNOWN) {
				  return;
			}
//		  eh instances a
//		  PetDbHelper mDbHelper = new PetDbHelper(this);
//		  SQLiteDatabase db = mDbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(PetEntry.COLUMN_PET_NAME , nameString);
			values.put(PetEntry.COLUMN_PET_BREED , breedString);
			values.put(PetEntry.COLUMN_PET_GENDER, mGender);
			//values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
			// If the weight is not provided by the user, don't try to parse the string into an
			// integer value. Use 0 by default.
			int weight = 0;

			if (!TextUtils.isEmpty(weightString)) {
				  weight = Integer.parseInt(weightString);
			}

			values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
			if (mCurrentPetUri == null) {
				  //lec 285 code
				  //this is NEW pet, so insert a new pet into the provider,
				  //returing the content URI for the new pet
				  Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

				  if (newUri == null) {
						Toast.makeText(this, "Pet failed to Save ", Toast.LENGTH_SHORT).show();
				  } else {
						Toast.makeText(this, "Pet saved, Success" , Toast.LENGTH_SHORT).show();
				  }
			} else {

				  // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
				  // and pass in the new ContentValues. Pass in null for the selection and selection args
				  // because mCurrentPetUri will already identify the correct row in the database that
				  // we want to modify.

				  int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

				  // Show a toast message depending on whether or not the update was successful.
				  if (rowsAffected == 0) {
						// If no rows were affected, then there was an error with the update.
						Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
				  } else {
						// Otherwise, the update was successful and we can display a toast.
						Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
				  }
			}

//		  TOAST MSG
//		  long newRodId = db.insert(PetEntry.TABLE_NAME ,null , values);
//		  if(newRodId==-1)
//		  {
//				Toast.makeText(this ," Error in saving pet" ,Toast.LENGTH_SHORT).show();
//		  }else{
//				Toast.makeText(this,"Pet saved with Row id" + newRodId , Toast.LENGTH_SHORT).show();
//		  }
	  }
// lec 294
	  @Override
	  public boolean onPrepareOptionsMenu(Menu menu) {
			super.onPrepareOptionsMenu(menu);
			//if this is new pet,hide the "Delete" menu item
			if (mCurrentPetUri == null) {
				  MenuItem menuItem = menu.findItem(R.id.action_delete);
				  menuItem.setVisible((false));
			}
			return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
			// User clicked on a menu option in the app bar overflow menu
			switch (item.getItemId()) {
						// Respond to a click on the "Save" menu option
				  case R.id.action_save:
						// Method to save Data from EditText(UI)
						//save pet in Database
						savePet();
						// Method to go back to CatalogActivity(UI)
						finish();
						return true;
						// Respond to a click on the "Delete" menu option
				  case R.id.action_delete:
						// Delete function coded in lec 295,296,297
						showDeleteConfirmationDialog();
						return true;
						// Respond to a click on the "Up" arrow button in the app bar
				  case android.R.id.home:

// je pet changed ha ta[mPetHasChanged ta false ha]
						//lec 289
						// ta ethe false ayu uppar wali comment di condition
						//but thalle wale code nal (! to true hoju)
						//NavUtils back aju
						//pr je koi change hoea(listener te) ta mPetHasChanged di value true hoju
						//(!nal value false hoju) te fir if condition run nhi honi
						//dialog open hoju
						if (!mPetHasChanged) {
							  // Navigate back to parent activity (CatalogActivity)
							  NavUtils.navigateUpFromSameTask(this);
							  return true;
						}
						// Otherwise if there are unsaved changes, setup a dialog to warn the user.
						// Create a click listener to handle the user confirming that
						// changes should be discarded.
						DialogInterface.OnClickListener discardButtonClickListener =
							  new DialogInterface.OnClickListener() {
							  @Override
							  public void onClick(DialogInterface dialog, int i) {
									// User clicked "Discard" button, navigate to parent activity.
									NavUtils.navigateUpFromSameTask(EditorActivity.this);

							  }
						};


						showUnsavedChangesDialog(discardButtonClickListener);

			}
			return super.onOptionsItemSelected(item);
	  }
	  //lec 292
	  @Override
	  public void onBackPressed() {
			// If the pet hasn't changed, continue with handling back button press
			if (!mPetHasChanged) {
				  super.onBackPressed();
				  return;
			}

			// Otherwise if there are unsaved changes, setup a dialog to warn the user.
			// Create a click listener to handle the user confirming that changes should be discarded.
			DialogInterface.OnClickListener discardButtonClickListener =
				  new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialog, int i) {

                        // User clicked "Discard" button, close the current activity.
                        finish();
				  }
			};
			// Show dialog that there are unsaved changes
			showUnsavedChangesDialog(discardButtonClickListener);


	  }
	  //lec 290-291-292 -293 important 
	  private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
			// Create an AlertDialog.Builder and set the message, and click listeners
			// for the positive and negative buttons on the dialog.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.unsaved_changed_dialog_msg);
			builder.setPositiveButton(R.string.Discard, discardButtonClickListener);
			builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							  // User clicked the "Keep editing" button, so dismiss the dialog
							  // and continue editing the pet.
							  if (dialog != null) {
									dialog.dismiss();
							  }
						}
				  });

			// Create and show the AlertDialog
			AlertDialog alertDialog = builder.create();
			alertDialog.show();


	  }
	  @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Since the editor shows all pet attributes, define a projection that contains
			// all columns from the pet table
			String[] projection = {
				  PetEntry._ID,
				  PetEntry.COLUMN_PET_NAME,
				  PetEntry.COLUMN_PET_BREED,
				  PetEntry.COLUMN_PET_GENDER,
				  PetEntry.COLUMN_PET_WEIGHT
			};
			// This loader will execute the ContentProvider's query method on a background thread
			return new CursorLoader(this, //Parent activity content
									// Query the content URI for the current pet			
									mCurrentPetUri,
									// Columns to include in the resulting Cursor
									projection,          
									null,
									null,
									null
									);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			// Bail early if the cursor is null or there is less than 1 row in the cursor
			if (cursor == null || cursor.getCount() < 1) {
				  return;
			}

			// Proceed with moving to the first row of the cursor and reading data from it
			// (This should be the only row in the cursor)
			if (cursor.moveToNext()) {
				  // Find the columns of pet attributes that we're interested in
				  int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
				  int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
				  int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
				  int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

				  // Extract out the value from the Cursor for the given column index
				  String name = cursor.getString(nameColumnIndex);
				  String breed = cursor.getString(breedColumnIndex);
				  int gender = cursor.getInt(genderColumnIndex);
				  int weight = cursor.getInt(weightColumnIndex);

				  // Update the views on the screen with the values from the database

				  mNameEditText.setText(name);
				  mBreedEditText.setText(breed);
				  mWeightEditText.setText(Integer.toString(weight));

				  // Gender is a dropdown spinner, so map the constant value from the database
				  // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
				  // Then call setSelection() so that option is displayed on screen as the current selection.
				  switch (gender) {
						case PetEntry.GENDER_MALE:
							  mGenderSpinner.setSelection(1);
							  break;
						case PetEntry.GENDER_FEMALE:
							  mGenderSpinner.setSelection(2);
							  break;
						case PetEntry.GENDER_UNKNOWN:
							  mGenderSpinner.setSelection(0);
							  break;
				  }
			}
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) {
			// If the loader is invalidated, clear out all the data from the input fields.

			mNameEditText.setText("");
			mBreedEditText.setText("");
			mWeightEditText.setText("");
			mGenderSpinner.setSelection(0);

	  }
	  /**
	   * Prompt the user to confirm that they want to delete this pet.
	   */
	  private void showDeleteConfirmationDialog() {
			// Create an AlertDialog.Builder and set the message, and click listeners
			// for the positive and negative buttons on the dialog.
			AlertDialog.Builder builder= new AlertDialog.Builder(this);
			builder.setMessage(R.string.delete_dialog_msg);
			builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							  // User clicked the "Delete" button, so delete the pet.
							  deletePet();
						}
				  });
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							  // User clicked the "Cancel" button, so dismiss the dialog
							  // and continue editing the pet.
							  if (dialog != null) {
									dialog.dismiss();
							  }
						}
				  });
			// Create and show the AlertDialog
			AlertDialog alertDialog = builder.create();
			alertDialog.show();

	  }

	  /**
	   * Perform the deletion of the pet in the database.
	   */
	  private void deletePet() {
			// Only perform the delete if this is an existing pet
			if (mCurrentPetUri != null) {
				  // Call the ContentResolver to delete the pet at the given content URI.
				  // Pass in null for the selection and selection args because the mCurrentPetUri
				  // content URI already identifies the pet that we want.
				  int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

				  // Show a toast message depending on whether or not the delete was successful.
				  if (rowsDeleted == 0) {
						// If no rows were deleted, then there was an error with the delete
						Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
									   Toast.LENGTH_SHORT).show();
				  } else {
						// Otherwise, the delete was successful and we can display a toast.
						Toast.makeText(this, getString(R.string.editor_delete_pet_successfull),
									   Toast.LENGTH_SHORT).show();
				  }
			}
			finish();

	  }
}
