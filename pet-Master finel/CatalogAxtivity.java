package com.example.android.pets;
//import androidx.appcompat.app.AppCompatActivity;


import android.content.ContentUris;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.pets.CatalogActivity;
import com.example.android.pets.EditorActivity;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	  private static final int PET_LOADER = 0;
	  private PetDbHelper mDbHelper;
	  PetCursorAdapter mCursorAdapter;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_catalog);

			// Setup FAB to open EditorActivity
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							  Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
							  startActivity(intent);
						}
				  });
			// find the ListView jo populate kru pet data
			ListView petListView = (ListView)findViewById(R.id.list);
			//find & set empty view on ListView, so that it only show when the list has 0 item
			View emptyView = findViewById(R.id.empty_view);
			petListView.setEmptyView(emptyView);
			//eh 275 ch likhea mai
			//v apna adaper ha jo loader wala oh data lve list view to, & eh mCurscorAdapter t set krna
			mCursorAdapter = new PetCursorAdapter(this, null);
			//hun set krna bs [ehda fubction eh a v jo apa input krage eh odo update krdu list nu
			petListView.setAdapter(mCursorAdapter);
			//loader manager loader nu start krda ethe pr eh upper set a 
			petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
							  Intent intent = new Intent(CatalogActivity.this , EditorActivity.class);

							  Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

							  //setting the URI on the data field of the intent
							  intent.setData(currentPetUri);
							  startActivity(intent);
						}
				  });
			getLoaderManager().initLoader(PET_LOADER, null, this);
			//lec 272 comment krta 
			//mDbHelper = new PetDbHelper(this);
	  }
	  /*    eh lec 275 ch comment kita
	   @Override
	   protected void onStart() {
	   super.onStart();
	   displayDatabaseInfo();
	   }

	   * Temporary helper method to display information in the onscreen TextView about the state of
	   * the pets database.
	   */
//    eh lec 275 vich comment kita displayDatabaseInfo
//	  private void displayDatabaseInfo() {
//
//
//			// Create and/or open a database to read from it
//			//SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//			String[] projection = {
//				  PetEntry._ID,
//				  PetEntry.COLUMN_PET_NAME,
//				  PetEntry.COLUMN_PET_BREED,
//				  PetEntry.COLUMN_PET_GENDER,
//				  PetEntry.COLUMN_PET_WEIGHT
//			};
//			Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,
//			 projection,
//			 null,
//			 null,
//			 null);
//			ListView petListView = (ListView) findViewById(R.id.list);
//			PetCursorAdapter adapter = new PetCursorAdapter(this,cursor);
//			petListView.setAdapter(adapter);
	  /*Cursor cursor = db.query(
	   PetEntry.TABLE_NAME,
	   projection,
	   null,
	   null,
	   null,
	   null,
	   null
	   );*/

//			TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//			try {
//
//
//				  displayView.setText("The pets table contains " + cursor.getCount() + " pets\n");
//
//				  displayView.append(PetEntry._ID + " - eh t temp i a hna?" +
//									 PetEntry.COLUMN_PET_NAME + " - " +
//									 PetEntry.COLUMN_PET_BREED + " - " +
//									 PetEntry.COLUMN_PET_GENDER + " -etho taq i a age t n jva v. " +
//									 PetEntry.COLUMN_PET_WEIGHT + "\n");
//
//				  // Figure out the index of each column
//				  int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
//				  int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
//				  int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
//				  int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
//				  int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
//
//				  while(cursor.moveToNext())
//				  {
//						int currentID = cursor.getInt(idColumnIndex);
//						String currentName = cursor.getString(nameColumnIndex);
//						String currentBreed = cursor.getString(breedColumnIndex);
//						int currentGender = cursor.getInt(genderColumnIndex);
//						int currentWeight = cursor.getInt(weightColumnIndex);
//
//						displayView.append(("\n" + currentID + " - " +
//										   currentName + " - " +
//										   currentBreed + " - " +
//										   currentGender + " - " +
//										   currentWeight));
//
//				  }
//
//
//			} finally {
//				  // Always close the cursor when you're done reading from it. This releases all its
//				  // resources and makes it invalid.
//				  cursor.close();
//			}
//      }

	  private void insertPet() {

//			SQLiteDatabase db = mDbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(PetEntry.COLUMN_PET_NAME, "TOTO");
			values.put(PetEntry.COLUMN_PET_BREED, "Terrier\n");
			values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
			values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
			Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
//			long newRodId = db.insert(PetEntry.TABLE_NAME, null, values);
//			Log.v("CatologActivity", "New Row ID" + newRodId);

	  }


	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu options from the res/menu/menu_catalog.xml file.
			// This adds menu items to the app bar.
			getMenuInflater().inflate(R.menu.menu_catalog, menu);
			return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
			// User clicked on a menu option in the app bar overflow menu
			switch (item.getItemId()) {
						// Respond to a click on the "Insert dummy data" menu option
				  case R.id.action_insert_dummy_data:
						insertPet();
						//displayDatabaseInfo();
						return true;
						// Respond to a click on the "Delete all entries" menu option
				  case R.id.action_delete_all_entries:
						deleteAllPets();
						return true;
			}
			return super.onOptionsItemSelected(item);
	  }
	  /**
	   * Helper method to delete all pets in the database.
	   */
	  private void deleteAllPets() {
			int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
			//Log.v("Catalog Activity" , rowsDeleted + " rows deleted from pet database");
	  }
	  @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle args) {

			String[] projection = {
				  PetEntry._ID,
				  PetEntry.COLUMN_PET_NAME,
				  PetEntry.COLUMN_PET_BREED,
				  PetEntry.COLUMN_PET_GENDER,
				  PetEntry.COLUMN_PET_WEIGHT
			};
			return new CursorLoader(this,
									PetEntry.CONTENT_URI,
									projection,
									null,
									null,
									null);
	  }
	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mCursorAdapter.swapCursor(data);
	  }
	  @Override
	  public void onLoaderReset(Loader<Cursor> p1) {
			mCursorAdapter.swapCursor(null);
	  }

}
