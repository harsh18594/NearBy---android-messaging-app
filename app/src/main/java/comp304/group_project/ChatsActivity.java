package comp304.group_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

public class ChatsActivity extends android.support.v4.app.Fragment {
    Context ctx;
    View rootView;
    private final Handler handler = new Handler();

   // List<Object> deduped;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.activity_chats, container, false);

        return rootView;
    }

    @Override
    public  void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = this.getContext();

        new temp1().execute("http://dataofdata.com/chat_list.php");
        doTheAutoRefresh();
    }

    class temp1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urlStr) {
            // do stuff on non-UI thread
            StringBuffer htmlCode = new StringBuffer();
            try{
                HttpURLConnection conn = null;
                URL url = new URL(urlStr[0]);
                //    context = urlStr[1];
                conn = (HttpURLConnection) url.openConnection();

                //retrieve the shared data

                SharedPreferences pref = ctx.getSharedPreferences("shared-data", Context.MODE_PRIVATE);
                String user_name = pref.getString("username","shivam");

                //retrieval over

                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setRequestMethod("POST");
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"iso-8859-1"));
                conn.connect();

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    htmlCode.append(inputLine);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
            return htmlCode.toString();
        }

        @Override
        protected void onPostExecute(String htmlCode){
            // do stuff on UI thread with the html

            ListView lv = (ListView) rootView.findViewById(R.id.chats_list);
            List<String> your_array_list = new ArrayList<String>();




            String str[];
            try {
                JSONArray jArray = new JSONArray(htmlCode.toString());

                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    if(!your_array_list.contains(jObject.getString("Name")))
                        your_array_list.add(jObject.getString("Name"));
                }

                // deduped = your_array_list.stream().distinct().collect(Collectors.toList());

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,your_array_list );

                lv.setAdapter(arrayAdapter);

                ////
                //// listview on click listener

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent;
                        intent = new Intent(ctx,ChatActivity.class);
                        SharedPreferences pref;
                        pref = ctx.getSharedPreferences("shared-data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = pref.edit();
                        ed.putString("friendname", (String)parent.getItemAtPosition(position));
                        ed.commit();
                        startActivity(intent);

                    }

                });

            } catch (JSONException e) {
                e.printStackTrace();


            }

            //   String[] nearby_list = csvParser(str);


            //            lv.setAdapter(arrayAdapter);
        }

    }

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //recreate();
                new temp1().execute("http://dataofdata.com/chat_list.php");
                doTheAutoRefresh();
            }
        }, 2000);
    }
}
    //**************************************************************************
/*        new AsyncTask<String, Void, String>(){

            // Context context;
            @Override
            protected String doInBackground(String... urlStr){
                // do stuff on non-UI thread
                StringBuffer htmlCode = new StringBuffer();
                try{
                    HttpURLConnection conn = null;
                    URL url = new URL(urlStr[0]);
                    //    context = urlStr[1];
                    conn = (HttpURLConnection) url.openConnection();

                    //retrieve the shared data

                    SharedPreferences pref = ctx.getSharedPreferences("shared-data", Context.MODE_PRIVATE);
                    String user_name = pref.getString("username","shivam");

                    //retrieval over

                    conn.setConnectTimeout(6000);
                    conn.setReadTimeout(6000);
                    conn.setRequestMethod("POST");
                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"iso-8859-1"));
                    conn.connect();

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        htmlCode.append(inputLine);
                    }
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return htmlCode.toString();
            }

            @Override
            protected void onPostExecute(String htmlCode){
                // do stuff on UI thread with the html

                ListView lv = (ListView) rootView.findViewById(R.id.chats_list);
                List<String> your_array_list = new ArrayList<String>();




                String str[];
                try {
                    JSONArray jArray = new JSONArray(htmlCode.toString());

                    for(int i=0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        if(!your_array_list.contains(jObject.getString("Name")))
                        your_array_list.add(jObject.getString("Name"));
                    }

                   // deduped = your_array_list.stream().distinct().collect(Collectors.toList());

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,your_array_list );

                    lv.setAdapter(arrayAdapter);

                    ////
                    //// listview on click listener

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent;
                            intent = new Intent(ctx,ChatActivity.class);
                            SharedPreferences pref;
                            pref = ctx.getSharedPreferences("shared-data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = pref.edit();
                            ed.putString("friendname", (String)parent.getItemAtPosition(position));
                            ed.commit();
                            startActivity(intent);

                        }

                    });









                    ////
                    ////

                } catch (JSONException e) {
                    e.printStackTrace();


                }

                //   String[] nearby_list = csvParser(str);


                //            lv.setAdapter(arrayAdapter);
            }


        }.execute("http://dataofdata.com/chat_list.php");
        */

