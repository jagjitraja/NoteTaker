package jsb.com.notetaker.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import jsb.com.notetaker.Activities.MainActivity;
import jsb.com.notetaker.Activities.NoteDetailActivity;
import jsb.com.notetaker.AdaptersAndDataFiles.Note;
import jsb.com.notetaker.AdaptersAndDataFiles.NoteDataController;
import jsb.com.notetaker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditFragment extends Fragment {

	private ImageButton imageButton;
	private EditText editTitle;
	private EditText editBody;
	private AlertDialog chooseCategoryDialogue;
	public AlertDialog saveConfirmDialogue;
	private Note.Category chosenCategory;
	private Button saveButton;

	/*private UpdateNoteData updateNoteData;

	//Interface used to transfer note data from fragment to notedetailactivity on destroy
	//Interface used to transfer note data from fragment to notedetailactivity on destroy
	public interface UpdateNoteData{
		public void passNoteData(String newTitle, String newBody, Note.Category newCategory);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		updateNoteData = (UpdateNoteData)context;
	}*/

	public NoteEditFragment() {
		// Required empty public constructor
	}

	private void getUIElements(View fragmentLayout){
		imageButton = (ImageButton) fragmentLayout.findViewById(R.id.edit_note_item_icon);
		editBody = (EditText) fragmentLayout.findViewById(R.id.edit_note_body_view);
		editTitle = (EditText)fragmentLayout.findViewById(R.id.edit_note_title_view);
		saveButton = (Button) fragmentLayout.findViewById(R.id.save_button);
	}


	private void fillNoteData(Intent intent, Bundle savedInstanceState){
		final String title = intent.getExtras().getString(MainActivity.NOTE_TITLE_KEY);
		String body = intent.getExtras().getString(MainActivity.NOTE_BODY_KEY);
		Note.Category category;

		if(savedInstanceState == null) {
			category = (Note.Category) intent.getExtras().getSerializable(MainActivity.NOTE_CATEGORY_KEY);
			chosenCategory = category;
		}
		else{
			category = (Note.Category) savedInstanceState.getSerializable(MainActivity.NOTE_CATEGORY_KEY);
			chosenCategory = category;
			//handle orientation change when dialogue is showing
			if(savedInstanceState.getBoolean(MainActivity.SAVE_DIALOGUE_IS_SHOWING)){
				launchSaveConfirmationDialogue();
				saveConfirmDialogue.show();
				NoteDetailActivity.isSaveDialogueShowing = true;
			}

			if(savedInstanceState.getBoolean(MainActivity.CHOICE_DIALOGUE_IS_SHOWING)){
				launchChooseCategoryDialogueBuilder();
				chooseCategoryDialogue.show();
				NoteDetailActivity.isChoiceDialogueShowing = true;
			}
		}
		imageButton.setImageResource(Note.getCategoryImageFromCategory(category));
		editTitle.setText(title);
		editBody.setText(body);
		holdInitialNoteData(title,body,category, intent.getIntExtra(MainActivity.NOTE_ID_KEY,0));
	}

	private void holdInitialNoteData(String title, String body, Note.Category category, int id){
		NoteDataController.initialCategory = category;
		NoteDataController.initialNoteBody = body;
		NoteDataController.initialNoteTitle = title;
        NoteDataController.chosenNoteID = id;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final View fragmentLayout = inflater.inflate(R.layout.fragment_note_edit,container,false);
		getUIElements(fragmentLayout);
		Intent intent = getActivity().getIntent();
		fillNoteData(intent,savedInstanceState);

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Hide keyboard when dialogue is launched
				hideKeyboard(fragmentLayout);
				launchSaveConfirmationDialogue();
				saveConfirmDialogue.show();
				NoteDetailActivity.isSaveDialogueShowing=true;
			}
		});
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            //Hide Keyboard when dialogue is launched
	            hideKeyboard(fragmentLayout);
                launchChooseCategoryDialogueBuilder();
                chooseCategoryDialogue.show();
	            NoteDetailActivity.isChoiceDialogueShowing = true;
            }
        });

		return fragmentLayout;
	}

	//Hide Keyboard method
	private void hideKeyboard(View fragmentLayout){
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(fragmentLayout.getWindowToken(),0);
	}

	private void launchChooseCategoryDialogueBuilder(){
		AlertDialog.Builder chooseCategoryDialogueBuilder = new AlertDialog.Builder(getContext());
        final String[] categories = {"PRIVATE","MEALS","WORK","FINANCIAL","STUDIES"};

        chooseCategoryDialogueBuilder.setTitle(getResources().getString(R.string.choose_category_dialogue_title));
        chooseCategoryDialogueBuilder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenCategory = Note.getCategoryFromString(categories[which]);
                imageButton.setImageResource(Note.getCategoryImageFromCategory(chosenCategory));
                dialog.dismiss();
	            NoteDetailActivity.isChoiceDialogueShowing = false;
            }
        });
        chooseCategoryDialogueBuilder.setNegativeButton(getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            NoteDetailActivity.isChoiceDialogueShowing = false;
            }
        });

        chooseCategoryDialogue = chooseCategoryDialogueBuilder.create();
	}

	public void launchSaveConfirmationDialogue(){
		final AlertDialog.Builder saveConfirmDialogueBuilder = new AlertDialog.Builder(getContext());

		saveConfirmDialogueBuilder.setTitle(R.string.save_confirm_dialogue_title);
		saveConfirmDialogueBuilder.setMessage(R.string.save_confirm_message);

		saveConfirmDialogueBuilder.setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NoteDetailActivity.isSaveDialogueShowing = false;

                Intent intent = new Intent(getActivity(),MainActivity.class);

                intent.putExtra(MainActivity.NOTE_TITLE_KEY,editTitle.getText().toString());
                intent.putExtra(MainActivity.NOTE_BODY_KEY,editBody.getText().toString());
                intent.putExtra(MainActivity.NOTE_CATEGORY_KEY,chosenCategory);
				intent.putExtra(MainActivity.CHANGES_MADE,true);
				intent.putExtra(MainActivity.NOTE_ID_KEY,NoteDataController.chosenNoteID);
				//clears back stack so that main activity does not relaunch on back pressed
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

				dialog.dismiss();
				getActivity().finish();
                startActivity(intent);
			}
		});
		saveConfirmDialogueBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NoteDetailActivity.isSaveDialogueShowing = false;
				dialog.cancel();
				getActivity().finish();
			}
		});

		saveConfirmDialogue = saveConfirmDialogueBuilder.create();
	}


    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putSerializable(MainActivity.NOTE_CATEGORY_KEY,chosenCategory);

	    if(NoteDetailActivity.isChoiceDialogueShowing){
		    Log.d(MainActivity.APP_ID_KEY,"SAVING CHOICE DIALOGUE INSTANCE");
		    chooseCategoryDialogue.dismiss();
		    saveInstanceState.putBoolean(MainActivity.CHOICE_DIALOGUE_IS_SHOWING,NoteDetailActivity.isChoiceDialogueShowing);
		    NoteDetailActivity.isChoiceDialogueShowing = false;
	    }

	    if(NoteDetailActivity.isSaveDialogueShowing){
		    Log.d(MainActivity.APP_ID_KEY,"SAVING SAVE DIALOGUE INSTANCE");
		    saveConfirmDialogue.dismiss();
		    saveInstanceState.putBoolean(MainActivity.SAVE_DIALOGUE_IS_SHOWING,NoteDetailActivity.isSaveDialogueShowing);
		    NoteDetailActivity.isSaveDialogueShowing = false;
	    }
    }

	public String getnewNoteTitle(){
		return editTitle.getText().toString();
	}

	public String getnewNoteBody(){
		return editBody.getText().toString();
	}

	public Note.Category getnewNoteCategory(){
		return chosenCategory;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(MainActivity.APP_ID_KEY,"DESTROYED FRAGMENT=========================");
	}
}