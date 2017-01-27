package jsb.com.notetaker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import jsb.com.notetaker.AdaptersAndDataFiles.Note;
import jsb.com.notetaker.AdaptersAndDataFiles.NoteDataController;
import jsb.com.notetaker.Fragments.NoteEditFragment;
import jsb.com.notetaker.Fragments.NoteViewFragment;
import jsb.com.notetaker.R;

public class NoteDetailActivity extends AppCompatActivity {

	public static boolean isSaveDialogueShowing;
	public static boolean isChoiceDialogueShowing;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private NoteEditFragment noteEditFragment;
	private static ActionBar actionBar;

	private String newNoteTitle;
	private String newNoteBody;
	private Note.Category newNoteCategory;
	private String newNoteDate;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		setFragment();
	}
    public static ActionBar getActionBarNoteDetail(){
       return actionBar;
    }

	private void setFragment() {
		Intent intent = getIntent();
		String action = intent.getExtras().getString(MainActivity.INTENT_MOTIVE);

		switch (action) {
			case MainActivity.VIEW_NOTE_MOTIVE:
				NoteViewFragment noteViewFragment = new NoteViewFragment();
				setTitle("View Note");
				//show up button
				actionBar.setDisplayHomeAsUpEnabled(true);
				fragmentTransaction.add(R.id.detail_container, noteViewFragment, "NOTE_VIEW_FRAGMENT");
				break;

			case MainActivity.EDIT_NOTE_MOTIVE:
				noteEditFragment = new NoteEditFragment();
				setTitle("Edit Note");
				//hide up button
				actionBar.setDisplayHomeAsUpEnabled(false);
				fragmentTransaction.add(R.id.detail_container, noteEditFragment, "NOTE_EDIT_FRAGMENT");
				break;

			case MainActivity.ADD_NOTE_MOTIVE:
				noteEditFragment = new NoteEditFragment();
				setTitle("Add New Note");
				//hide up button
				actionBar.setDisplayHomeAsUpEnabled(false);
				fragmentTransaction.add(R.id.detail_container, noteEditFragment, "NOTE_EDIT_FRAGMENT");
				break;
		}
		fragmentTransaction.commit();
	}

	private boolean checkChanges() {
		//check changes between initial note data and current note data
		return !(newNoteTitle.equals(NoteDataController.initialNoteTitle) &&
				newNoteBody.equals(NoteDataController.initialNoteBody) &&
				newNoteCategory.equals(NoteDataController.initialCategory)&&
				newNoteDate.equals(NoteDataController.initialDate));
	}

	@Override
	public void onBackPressed() {
		noteEditFragment = (NoteEditFragment) fragmentManager.findFragmentByTag("NOTE_EDIT_FRAGMENT");

		if (noteEditFragment != null) {
			newNoteTitle = noteEditFragment.getnewNoteTitle();
			newNoteBody = noteEditFragment.getnewNoteBody();
			newNoteCategory = noteEditFragment.getnewNoteCategory();
			newNoteDate = noteEditFragment.getnewNoteDate();

			if (checkChanges()) {
				Log.d(MainActivity.APP_ID_KEY, "Changes were made");
				noteEditFragment.launchSaveConfirmationDialogue();
				noteEditFragment.saveConfirmDialogue.show();
			} else {
				Log.d(MainActivity.APP_ID_KEY, "No changes made");
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
	}

	//TODO CREATE A METHOD TO GO BACK TO MAIN ACTIVITY ON UP BUTTON

}
