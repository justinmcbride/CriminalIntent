package com.dare599z.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Justin on 11/19/2014.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.dare599z.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date", DIALOG_TIME = "time", DIALOG_WHAT = "whattochange";
    private static final int REQUEST_DATE = 0, REQUEST_TIME = 1, REQUEST_WHATTOCHANGE = 2, REQUEST_PHOTO = 3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton, mDeleteButton;
    private ImageButton mPhotoButton;
    private CheckBox mSolvedCheckBox;
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    private void updateDate() {
        mDateButton.setText(mCrime.getFormattedDate());
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE | requestCode == REQUEST_TIME) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }

        else if (requestCode == REQUEST_WHATTOCHANGE) {
            int whatToChange = (int)data.getIntExtra(DateTimePickerFragment.EXTRA_TYPE, -1);
            if (whatToChange == -1) {
                Toast.makeText(getActivity().getApplicationContext(), "Error: Date or time not selected", Toast.LENGTH_SHORT).show();
                return;
            }
            if (whatToChange == DateTimePickerFragment.EXTRA_DATE) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            } else if (whatToChange == DateTimePickerFragment.EXTRA_TIME) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }

        }

        else if (requestCode == REQUEST_PHOTO) {
            if (mCurrentPhotoPath == null) {
                Toast.makeText(getActivity(),"path not saved", Toast.LENGTH_SHORT).show();
                return;
            }
            String path = mCurrentPhotoPath.substring(6);
            Photo p = new Photo(path);

            if (mCrime.getPhoto() != null) {
                boolean deleted = mCrime.deletePhoto();
                if (deleted) {
                    Toast.makeText(getActivity(), "old file deleted", Toast.LENGTH_SHORT).show();
                    mImageView.setImageResource(android.R.color.darker_gray);
                }
                else Toast.makeText(getActivity(), "old file NOT deleted", Toast.LENGTH_SHORT).show();
            }

            mCrime.setPhoto(p);
            showPhoto();
        }
    }


    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void showPhoto() {
        Photo p = mCrime.getPhoto();
        if (p != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            // Set height and width in options, does not return an image and no resource taken
            BitmapFactory.decodeFile(p.getFilename(), options);

            int pow = 0;
            while (options.outHeight >> pow > 4096 || options.outWidth >> pow > 4096)
                pow += 1;
            options.inSampleSize = 1 << pow;
            options.inJustDecodeBounds = false;
            Bitmap image = BitmapFactory.decodeFile(p.getFilename(), options);
            mImageView.setImageBitmap(image);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        setHasOptionsMenu(true);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        if (savedInstanceState != null && (savedInstanceState.getSerializable("mCurrentPhoto") != null)) {
            mCurrentPhotoPath = (String)savedInstanceState.getSerializable("mCurrentPhoto");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("mCurrentPhoto", mCurrentPhotoPath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        if (NavUtils.getParentActivityName(getActivity()) != null) getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DateTimePickerFragment dialog = new DateTimePickerFragment();
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_WHATTOCHANGE);
                dialog.show(fm, DIALOG_WHAT);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mDeleteButton = (Button)v.findViewById(R.id.deleteCrimeButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForDelete();
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                    }
                }
            }
        });


        mImageView = (ImageView)v.findViewById(R.id.crime_ImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p==null) return;

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(p.getFilename())), "image/jpg");
                startActivity(intent);
            }
        });
        registerForContextMenu(mImageView);
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_photo_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime_photo:
                mCrime.deletePhoto();
                mImageView.setImageResource(android.R.color.darker_gray);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void checkForDelete() {
        Dialog deleteDialog = new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to delete this crime?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CrimeLab.get(getActivity()).deleteCrime(mCrime);
                        NavUtils.navigateUpFromSameTask(getActivity());

                    }
                })
                .setNegativeButton("No", null)
                .create();
        deleteDialog.show();
    }
}
