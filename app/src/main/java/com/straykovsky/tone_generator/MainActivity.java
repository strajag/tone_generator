package com.straykovsky.tone_generator;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener
{
    private final static int SEEK_BAR_MIN = 0;
    private final static int SEEK_BAR_MAX = 1000;
    private final static int FREQUENCY_MIN = 20;
    private final static int FREQUENCY_MAX = 20000;

    private boolean is_frequency_running;
    private boolean is_type_square;
    private int frequency = 440;

    private TextView text_view_frequency;
    private SeekBar seek_bar_frequency;
    private Button button_sine;
    private Button button_square;

    private int color_state_list;
    private int color;

    private ToneGenerator tone_generator;

    private AlertDialog.Builder builder;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        is_frequency_running = false;
        is_type_square = false;

        text_view_frequency = findViewById(R.id.text_view_frequency);
        text_view_frequency.setText(String.format(Locale.ENGLISH, "%dHz", frequency));
        text_view_frequency.setOnClickListener(this);

        button_sine = findViewById(R.id.button_sine);
        button_sine.setOnClickListener(this);

        button_square = findViewById(R.id.button_square);
        button_square.setOnClickListener(this);

        seek_bar_frequency = findViewById(R.id.seek_bar_frequency);
        seek_bar_frequency.setMin(SEEK_BAR_MIN);
        seek_bar_frequency.setMax(SEEK_BAR_MAX);
        seek_bar_frequency.setProgress((int)(Math.sqrt(((frequency - FREQUENCY_MIN) / (float)(FREQUENCY_MAX - FREQUENCY_MIN)) * (float)(SEEK_BAR_MAX * SEEK_BAR_MAX))));
        seek_bar_frequency.setOnSeekBarChangeListener(this);

        color_state_list = button_sine.getCurrentTextColor();
        color = Color.rgb(255 - Color.red(button_sine.getCurrentTextColor()),
                255 - Color.green(button_sine.getCurrentTextColor()),
                255 - Color.blue(button_sine.getCurrentTextColor()));

        tone_generator = new ToneGenerator();
    }

    @Override
    public void onClick(View view)
    {
        if(view == text_view_frequency)
        {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("frequency (20hz - 20000hz)");
            // Set up the input
            input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText(String.format(Locale.ENGLISH, "%d", frequency));
            int maxLength = 5;
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton("OK", this);
            builder.setNegativeButton("Cancel", this);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            input.requestFocus();
            input.selectAll();
        }
        if(view == button_sine)
        {
            if(!is_type_square && is_frequency_running)
            {
                /* stop */
                button_sine.setTextColor(color_state_list);
                is_frequency_running = false;
                tone_generator.stop();
            }
            else
            {
                /* start */
                button_sine.setTextColor(color);
                button_square.setTextColor(color_state_list);
                is_frequency_running = true;
                is_type_square = false;
                tone_generator.setFrequency(frequency);
                tone_generator.set_type_square(is_type_square);
                tone_generator.start();
            }
        }
        else if(view == button_square)
        {
            if(is_type_square && is_frequency_running)
            {
                /* stop */
                button_square.setTextColor(color_state_list);
                is_frequency_running = false;
                tone_generator.stop();
            }
            else
            {
                /* start */
                button_sine.setTextColor(color_state_list);
                button_square.setTextColor(color);
                is_frequency_running = true;
                is_type_square = true;
                tone_generator.setFrequency(frequency);
                tone_generator.set_type_square(is_type_square);
                tone_generator.start();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seek_bar, int progress, boolean is_change_from_user)
    {
        frequency = (int)((progress * progress) / (float)(SEEK_BAR_MAX * SEEK_BAR_MAX) * (FREQUENCY_MAX - FREQUENCY_MIN) + FREQUENCY_MIN);
        text_view_frequency.setText(String.format(Locale.ENGLISH, "%dHz", frequency));

        if(is_frequency_running)
        {
            tone_generator.setFrequency(frequency);
            tone_generator.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek_bar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seek_bar)
    {

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int button)
    {
        if(button == DialogInterface.BUTTON_POSITIVE)
        {
            int temp = Integer.parseInt(input.getText().toString());

            if(temp < 20)
            {
                temp = 20;
            }
            else if(temp > 20000)
            {
                temp = 20000;
            }

            frequency = temp;
            seek_bar_frequency.setOnSeekBarChangeListener(null);
            seek_bar_frequency.setProgress((int) (Math.sqrt(((frequency - FREQUENCY_MIN) / (float) (FREQUENCY_MAX - FREQUENCY_MIN)) * (float) (SEEK_BAR_MAX * SEEK_BAR_MAX))));
            text_view_frequency.setText(String.format(Locale.ENGLISH, "%dHz", frequency));
            seek_bar_frequency.setOnSeekBarChangeListener(this);
            if(is_frequency_running)
            {
                tone_generator.setFrequency(frequency);
                tone_generator.start();
            }
        }
        else if(button == DialogInterface.BUTTON_NEGATIVE)
        {
            dialogInterface.cancel();
        }
    }
}