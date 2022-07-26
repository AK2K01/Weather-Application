package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText cityname;
    TextView resulttext;
    public void getweather(View view)
    {
        try {
            String city = cityname.getText().toString();
            String encodedcityname = URLEncoder.encode(city, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedcityname + "&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityname.getWindowToken(), 0);
        }catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather!",Toast.LENGTH_SHORT).show();
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try{
                URL url=new URL(urls[0]);
                HttpsURLConnection urlConnection=(HttpsURLConnection)url.openConnection();
                InputStream input=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(input);
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }catch(MalformedURLException e){
                System.out.println("Not able to open the link!");
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather!",Toast.LENGTH_SHORT).show();
                return "Failed!";
            }catch(Exception e){
                e.printStackTrace();
            }
            return "Failed!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherinfo=jsonObject.getString("weather");
                JSONArray jsonArray=new JSONArray(weatherinfo);
                String message="";
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    String main=jsonObject2.getString("main");
                    String description=jsonObject2.getString("description");
                    if(!main.equals("") && !description.equals(""))
                    {
                        message=main+":"+description+"\r\n";
                    }
                }
                if(!message.equals(""))
                {
                    resulttext.setText(message);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Could not find weather!",Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resulttext=(TextView)findViewById(R.id.resulttext);
        cityname=(EditText)findViewById(R.id.cityname);
    }
}