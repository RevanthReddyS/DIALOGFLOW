package com.example.reddy.dialogflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.model.AIError;
import org.json.JSONArray;
import org.json.JSONObject;


public class DialogflowActivity extends AppCompatActivity implements AIListener {
    AIService aiService;
    TextView tv;
    Button btnSpeak;
    EditText txtSpeechInput;
    EditText outputText;
    String userQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogflow);

        btnSpeak = (Button) findViewById(R.id.button10);
        txtSpeechInput =  (EditText)findViewById(R.id.editText);
        outputText = (EditText) findViewById(R.id.editText2);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userQuery = txtSpeechInput.getText().toString();
                RetrieveFeedTask task = new RetrieveFeedTask();
                task.execute(userQuery);
            }
        });

        AIConfiguration config = new AIConfiguration("ENTER YOUR TOKEN HERE",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
       /* int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }*/
    }








    public String getQuery(String str) {
        String text = " ";
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer 'ENTER YOUR API KEY' ");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(str);
            jsonParam.put("query", queryArray);
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");
            OutputStreamWriter dstream = new OutputStreamWriter(connection.getOutputStream());
            dstream.write(jsonParam.toString());
            dstream.flush();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            text = sb.toString();
            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
            fulfillment = object.getJSONObject("fulfillment");
            speech = fulfillment.optString("speech");
            return speech;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }
        return null;
    }






    /*public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {


                }
                return;
            }
        }
    }




    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }


*/


    public void Listen(View v) {

        aiService.startListening();

    }




    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        txtSpeechInput.setText( result.getResolvedQuery() );
        outputText.setText(result.getAction());


    }


    @Override
    public void onError(AIError error) {
        tv.setText(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {


    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = getQuery(voids[0]);


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            outputText.setText(s);

        }

    }
}
