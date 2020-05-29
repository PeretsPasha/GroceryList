package com.example.grocery;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polyak.iconswitch.IconSwitch;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private GroceryAdapter mAdapter;
    private EditText mEditTextName;
    private TextView mTextViewAmount;
    private int mAmount = 0;
    private String name;
    private MediaPlayer mediaPlayer,littleDel;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         sharedPref = new SharedPref(this);
         if (sharedPref.loadNightModeState() == true){
             setTheme(R.style.AppThemeDark);
         } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IconSwitch iconSwitch = findViewById(R.id.icon_switch);

        if(sharedPref.loadNightModeState() == true) {
            iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
        } else {
            iconSwitch.setChecked(IconSwitch.Checked.LEFT);
        }

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                if (current == IconSwitch.Checked.RIGHT) {
                    name = mEditTextName.getText().toString();
                    sharedPref.saveVeriables(name, mAmount);
                    sharedPref.setNightModeState(true);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    name = mEditTextName.getText().toString();
                    sharedPref.saveVeriables(name, mAmount);
                    sharedPref.setNightModeState(false);
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.trash);
        littleDel = MediaPlayer.create(this, R.raw.littledel);
        mediaPlayer.setVolume(0.05f,0.05f);
        littleDel.setVolume(0.1f,0.1f);

        GroceryDBHelper dbHelper = new GroceryDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new GroceryAdapter(this, getAllItems());
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
                littleDel.start();
            }
        }).attachToRecyclerView(recyclerView);

        mEditTextName = findViewById(R.id.edittext_name);
        mTextViewAmount = findViewById(R.id.textview_amount);

        mAmount = sharedPref.loadAmount();
        name = sharedPref.loadEdit();
        mTextViewAmount.setText(String.valueOf(mAmount));
        mEditTextName.setText(name);

        Button buttonIncrease = findViewById(R.id.button_increase);
        Button buttonDecrease = findViewById(R.id.button_decrease);
        Button buttonAdd = findViewById(R.id.button_add);
        Button buttonClear = findViewById(R.id.button_clear);

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        buttonClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearList();
                mediaPlayer.start();
                return true;
            }
        });

        buttonAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View g) {

                return true;
            }
        });
    }

    private void clearList() {
        mDatabase.delete(GroceryContract.GroceryEntry.TABLE_NAME,
                GroceryContract.GroceryEntry._ID, null);
        mAdapter.swapCursor(getAllItems());
        mAmount=0;
        mTextViewAmount.setText(String.valueOf(mAmount));
    }

    private void increase() {
        mAmount++;
        mTextViewAmount.setText(String.valueOf(mAmount));
    }

    private void decrease() {
        if (mAmount > 0) {
            mAmount--;
            mTextViewAmount.setText(String.valueOf(mAmount));
        }
    }

    private void addItem() {

        if (mEditTextName.getText().toString().trim().length() == 0 || mAmount == 0) {
            return;
        }

        name = mEditTextName.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(GroceryContract.GroceryEntry.COLUMN_NAME, name);
        cv.put(GroceryContract.GroceryEntry.COLUMN_AMOUNT, mAmount);

        mDatabase.insert(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);
        mAdapter.swapCursor(getAllItems());

        mEditTextName.getText().clear();
        mAmount = 1;    // this is for easy writing
        mTextViewAmount.setText(String.valueOf(mAmount));
    }

    private void removeItem(long id) {
        mDatabase.delete(GroceryContract.GroceryEntry.TABLE_NAME,
                GroceryContract.GroceryEntry._ID + "=" + id, null);
        mAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                GroceryContract.GroceryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        name = mEditTextName.getText().toString();
        sharedPref.saveVeriables(name, mAmount);
    }
}
