package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by HP-USER on 3/21/2018.
 */

public class MainTickets extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets_main);
        Intent intent = getIntent();

        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .4), (int) (height * .6));
        TextView closePopup = (TextView) findViewById(R.id.closePop);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        final RadioButton adult=(RadioButton)findViewById(R.id.radio1);
        final RadioButton student=(RadioButton)findViewById(R.id.radio2);
        final RadioButton child=(RadioButton)findViewById(R.id.radio3);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    RadioButton radio ;
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radio = (RadioButton) findViewById(selectedId);

                    Intent intent = new Intent(MainTickets.this, ChooseTrip.class);
                    intent.putExtra("category", radio.getText());
                    startActivityForResult(intent, 0);

                }


            }
        });
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}