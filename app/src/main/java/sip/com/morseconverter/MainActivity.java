package sip.com.morseconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import androidx.core.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    EditText message;
    FloatingActionButton alpha;
    FloatingActionButton morse;
    EditText result;
    MediaPlayer mediaPlayer;
    FloatingActionButton imageButton;
    String morseCode=" ";
    ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (EditText) findViewById(R.id.edt_message);
        alpha = (FloatingActionButton) findViewById(R.id.btn_toAlpha);
        morse = (FloatingActionButton) findViewById(R.id.btn_toMarso);
        result = (EditText) findViewById(R.id.result);
        mediaPlayer = new MediaPlayer();
        imageButton = (FloatingActionButton) findViewById(R.id.btn_play);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        Window window = this.getWindow();

        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
        ViewCompat.setBackgroundTintList(message,colorStateList);
        ViewCompat.setBackgroundTintList(result,colorStateList);

        if (android.os.Build.VERSION.SDK_INT > 18) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (android.os.Build.VERSION.SDK_INT > 20) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (android.os.Build.VERSION.SDK_INT > 20) {
            window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.gray));
        }


        message.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    return true;
                }
              return false;
            }
        });


        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToConvert = message.getText().toString();
                if(!txtToConvert.isEmpty()|| !txtToConvert.equals("")) {

                    morseCode = txtToConvert.replace('/', ' ');
                    morseCode = morseCode.replace("//", " ");
                    if (!isMorseCode()) {
                        Toast.makeText(MainActivity.this, "Text already in Alpha format", Toast.LENGTH_SHORT).show();
                    } else {
                        String convertedText = MorseCode.morseToAlpha(txtToConvert);
                        result.setText(convertedText);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Please first enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        morse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToConvert = message.getText().toString();
                if(!txtToConvert.equals("")|| !txtToConvert.isEmpty()) {
                    morseCode = txtToConvert.replace('/',' ');
                    morseCode = morseCode.replace("//"," ");
                    if(isMorseCode()){
                        Toast.makeText(MainActivity.this,"message already in MorseCode format",Toast.LENGTH_SHORT).show();
                    }else {
                        morseCode = MorseCode.alphaToMorse(txtToConvert);
                        result.setText(morseCode.replace('|', ' '));
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        ClipData clip = ClipData.newPlainText("label", result.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "MorseCode copied to the clipboard also", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Please first enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMorseCode()){
                    try {
                        playSound();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"MorseCode is incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void playSound() throws InterruptedException, IOException {
        for (int i = 0; i < morseCode.length(); i++) {

            if (morseCode.charAt(i) == '.') {
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(this, R.raw.dash);
                mediaPlayer.start();
                Thread.sleep(200);
            } else if (morseCode.charAt(i) == '-') {
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(this, R.raw.beep);
                mediaPlayer.start();
                Thread.sleep(200);
            } else if (morseCode.charAt(i) == ' ') {
                Thread.sleep(500);
            }
            else if(morseCode.charAt(i) =='|'){
                Thread.sleep(1000);
            }
        }

    }

    public boolean isMorseCode() {
        for(int i = 0; i < morseCode.length(); i++) {
            if(morseCode.charAt(i) != '.' && morseCode.charAt(i) != '-' && morseCode.charAt(i) != ' '&& morseCode.charAt(i) != '|') {
                return false;
            }
        }
        return true;
    }
}

