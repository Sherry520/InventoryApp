package com.example.gyh.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gyh.inventoryapp.data.ProductContract.ProductEntry;

import java.io.FileNotFoundException;

/**
 * Created by gyh on 2016/11/1.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private int mQuantity;
    private int mChangeNumber;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private ImageView mImageView;
    private TextView mQuantityText;
    private EditText mVariationEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierTelEditText;
    private EditText mPriceEditText;
    private TextView mTimesText;

    private String mImageString;
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mImageView = (ImageView) findViewById(R.id.edit_product_picture_view);
        mQuantityText = (TextView) findViewById(R.id.edit_product_quantity);
        mVariationEditText = (EditText) findViewById(R.id.edit_quantity_variation);
        mSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mSupplierTelEditText = (EditText) findViewById(R.id.edit_supplier_tel);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mTimesText = (TextView) findViewById(R.id.edit_product_times);

        mNameEditText.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);
        mQuantityText.setOnTouchListener(mTouchListener);
        mVariationEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierTelEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

        findViewById(R.id.edit_product_picture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
        findViewById(R.id.edit_quantity_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQuantity = Integer.parseInt(mQuantityText.getText().toString());

                mChangeNumber = Integer.parseInt(mVariationEditText.getText().toString());

                mQuantity = mQuantity + mChangeNumber;
                mQuantityText.setText("" + mQuantity);
            }
        });
        findViewById(R.id.edit_quantity_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQuantity = Integer.parseInt(mQuantityText.getText().toString());

                mChangeNumber = Integer.parseInt(mVariationEditText.getText().toString());
                if ((mQuantity - mChangeNumber) >= 0) {
                    mQuantity = mQuantity - mChangeNumber;
                    mQuantityText.setText("" + mQuantity);
                }

            }
        });

        findViewById(R.id.edit_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telnumber = mSupplierTelEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telnumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    private void bitmapFactory(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 8;//设置生成的图片为原图的八分之一
        options.inJustDecodeBounds = true;//这将通知 BitmapFactory类只须返回该图像的范围,而无须尝试解码图像本身
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int heightRadio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRadio = (int) Math.ceil(options.outWidth / (float) width);
        if (heightRadio > 1 && widthRadio > 1) {
            if (heightRadio > widthRadio) {
                options.inSampleSize = heightRadio;
            } else {
                options.inSampleSize = widthRadio;
            }
        }
        //真正解码图片
        options.inJustDecodeBounds = false;
        Bitmap b;
        try {
            b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            mImageView.setImageBitmap(b);
            mImageString = uri.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        Log.d("MainActivity", 0 + "");
        if (requestCode == 0) {
            Uri uri = data.getData();
            bitmapFactory(uri);
            mImageString = uri.toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString();

        String pictureString = mImageString;

        String quantityString = mQuantityText.getText().toString().trim();

        String supplierString = mSupplierEditText.getText().toString().trim();

        String telString = mSupplierTelEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();

        String timesString = mTimesText.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(pictureString) ||
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(telString) || TextUtils.isEmpty(priceString) ||
                quantityString.equals(0)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, pictureString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_TEL, telString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_TIMES, timesString);
        if (mCurrentProductUri == null) {
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the insertion was successful
            if (rowsAffected == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PICTURE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_TEL,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_TIMES
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int pictureColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);
            int quantityColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int telColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_TEL);
            int priceColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int timesColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TIMES);

            String name = cursor.getString(nameColumn);
            String picture = cursor.getString(pictureColumn);
            int quantity = cursor.getInt(quantityColumn);
            String supplier = cursor.getString(supplierColumn);
            String tel = cursor.getString(telColumn);
            String price = cursor.getString(priceColumn);
            int times = cursor.getInt(timesColumn);

            mNameEditText.setText(name);
            bitmapFactory(Uri.parse(picture));
            mQuantityText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierTelEditText.setText(tel);
            mPriceEditText.setText(price);
            mTimesText.setText(Integer.toString(times));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mImageView.setImageURI(Uri.parse(""));
        mQuantityText.setText("");
        mSupplierEditText.setText("");
        mSupplierTelEditText.setText("");
        mPriceEditText.setText("");
        mTimesText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void deleteProduct() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
