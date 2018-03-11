package com.example.gyh.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gyh.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by gyh on 2016/11/1.
 */

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.product_name_text);
        final TextView quantityText = (TextView) view.findViewById(R.id.product_quantity_text);
        TextView priceText = (TextView) view.findViewById(R.id.product_price_text);
        final TextView timesText = (TextView) view.findViewById(R.id.sales_times_text);

        int _idColumn = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int timesColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TIMES);

        final int product_id = cursor.getInt(_idColumn);
        String productName = cursor.getString(nameColumn);
        final String productQuantity = cursor.getString(quantityColumn);
        String productPrice = cursor.getString(priceColumn);
        final String productTimes = cursor.getString(timesColumn);

        nameText.setText(productName);
        quantityText.setText(productQuantity);
        priceText.setText(productPrice);
        timesText.setText(productTimes);
        Button saleButton = (Button) view.findViewById(R.id.list_sale);

        final int[] mQuantity = {new Integer(productQuantity)};
        final int[] mTimes = {new Integer(productTimes)};

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int id = product_id;
                final Uri mUri = Uri.parse(ProductEntry.CONTENT_URI+"/#"+id);
                if((mQuantity[0] - 1 )>-1){
                    mQuantity[0] = mQuantity[0] - 1 ;
                    mTimes[0] = mTimes[0] + 1 ;
                    quantityText.setText(""+ mQuantity[0]);
                    timesText.setText(""+ mTimes[0]);

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantity[0]);
                    values.put(ProductEntry.COLUMN_PRODUCT_TIMES, mTimes[0]);

                    String selection = ProductEntry._ID+" = "+id;

                    context.getContentResolver().update(mUri,values,selection,null);

                }
                else{
                    Toast toast = Toast.makeText(context,"No Product Left", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }

}
