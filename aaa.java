package com.application.jordan.synthesevocale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Locale;


import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */

    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    public TextView textResults;
    private TextToSpeech tts;
    private Button buttonListen;
    private EditText editText;
    private int nbMots = 5;

    public String action = "";
    public String description= "";
    public String jour= "";
    public String heureDebut= "";
    public String heureFin= "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResults = (TextView)findViewById(R.id.textResults);
        initVoiceRecognizer();

        tts = new TextToSpeech(this, this);
        buttonListen = (Button) findViewById(R.id.start_reg);

        buttonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startListening();
            }

        });
    }

    @Override
    public void onDestroy() {
// Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.FRANCE);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void initVoiceRecognizer() {
        speechRecognizer = getSpeechRecognizer();
        intent = new  Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, nbMots);
    }

    public void startListening() {
        while (tts.isSpeaking()){

        }
        if (speechRecognizer!=null) {
            speechRecognizer.cancel();
        }
        speechRecognizer.startListening(intent);
    }

    private SpeechRecognizer getSpeechRecognizer(){
        if (speechRecognizer == null) {
            speechRecognizer =
                    SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new VoiceListener());
        }
        return speechRecognizer;
    }


    private void speakOut(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    class VoiceListener implements RecognitionListener {
        String TAG = "TAG";



        public void onReadyForSpeech(Bundle params) {}
        public void onBeginningOfSpeech() {}
        public void onRmsChanged(float rmsdB) {}
        public void onBufferReceived(byte[] buffer) {}
        public void onEndOfSpeech() {
            Log.d(TAG,"onEndofSpeech");
        }
        public void onError(int error) {
            Log.v(TAG, "error "+ error);
        }

        public void onResults(Bundle results) {
            String str = new String();
            Log.v(TAG,"onResults"+ results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            //float [] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            str = data.get(0).toString();
            //for (int i = 0; i < data.size(); i++) {
            //    Log.v(TAG,"result "+ data.get(i));
            //    str += data.get(i) + " (confidence : " + confidence[i]+")\n";
            //}
            textResults.setText(str);
            analyseString(str);
        }

        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}

        public void analyseString(String str){
            switch (action)
            {
                case "":
                    if (str.toLowerCase().contains("ajout") || str.toLowerCase().contains("crée")){
                        action = "ajouter";
                        speakOut("Veuillez donner la description de votre événement");
                        textResults.setText(str);
                        startListening();}
                    if (str.toLowerCase().contains("supprim") || str.toLowerCase().contains("retir")){
                        action = "supprimer";
                        speakOut("Quelle évènement voulez vous supprimer ?");
                        textResults.setText(str);
                        startListening();}
                    if (str.toLowerCase().contains("modifi") || str.toLowerCase().contains("retir")){
                        action = "modifier";
                        speakOut("Quelle évènement voulez vous modifier ?");
                        textResults.setText(str);
                        startListening();}
                    if (str.toLowerCase().contains("consult")){
                        action = "modifier";
                        speakOut("Vous souhaitez accéder aux événements de quel jour ?");
                        textResults.setText(str);
                        startListening();}
                    break;
                case "ajouter":
                    switch (description)
                    {
                        case "":
                            description=str;
                            textResults.setText(str);
                            speakOut("A quel jour à lieu votre événement ?");
                            startListening();
                            break;
                        default:
                            switch (jour)
                            {
                                case "":
                                    jour = str;
                                    textResults.setText(str);
                                    speakOut("A quelle heure commence votre évènement ?");
                                    startListening();
                                    break;
                                default:
                                    switch (heureDebut)
                                    {
                                        case "":
                                            heureDebut = str;
                                            textResults.setText(str);
                                            speakOut("A quelle heure fini votre évènement ?");
                                            startListening();
                                            break;
                                        default:
                                            switch (heureFin)
                                            {
                                                case "":
                                                    heureFin = str;
                                                    textResults.setText(str);
                                                    speakOut("Vous venez d'" + action + " votre " + description + " au calendrier. Votre événement aura lieu " + jour+ " entre  " + heureDebut + " et " + heureFin);
                                                    reinitilaiserEvenement();
                                                    break;
                                                default:
                                                    reinitilaiserEvenement();

                                            }

                                    }

                            }


                    }
                    break;
                case "supprimer":
                    reinitilaiserEvenement();
                    break;
                case "modifier":
                    reinitilaiserEvenement();
                    break;
                case "consulter":
                    reinitilaiserEvenement();
                    break;
                default:
                    reinitilaiserEvenement();
                    System.out.println("Il faut davantage travailler.");
            }
        }
    }

    private void reinitilaiserEvenement() {
        action = "";
        description= "";
        jour= "";
        heureDebut= "";
        heureFin="";
    }


}