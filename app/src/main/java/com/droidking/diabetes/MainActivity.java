package com.droidking.diabetes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DEBUG_TAG = "HttpExample";
    ArrayList<Team> teams = new ArrayList<Team>();
    public static ListView listview;
    public static Button btnDownload;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL derived from form URL
    public static final String URL="https://docs.google.com/forms/d/13aS0Z652osolwweX3uxgW6352cUfczPZSs-dMOYfgfI/formResponse";
    //input element ids found from the live form page
    public static final String BEat_KEY="entry.1248647360";
    public static final String AEat_KEY="entry.206365149";
    public static final String BP_Systol_KEY = "entry.1229261796";
    public static final String BP_Dyastol_KEY = "entry.1323596523";

    private static Context context;
    private static EditText BEatEditText;
    private static EditText AEatEditText;
    private static EditText BP_SystolEditText;
    private static EditText BP_DyastolEditText;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three from link
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        context = this;
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void buttonClickHandler(View view) {
        new DownloadWebpageTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object);
            }
        }).execute("https://spreadsheets.google.com/tq?key=1yyTcjWA6RAUwkI7sKOevWXAJfpITs__Zb0TwilihDCw");

    }
    private void processJson(JSONObject object) {
        try {
            JSONArray rows = object.getJSONArray("rows");

            for (int r = 0; r < rows.length(); ++r) {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                int position = columns.getJSONObject(0).getInt("v");
                String name = columns.getJSONObject(1).getString("v");
                int wins = columns.getJSONObject(3).getInt("v");
                int draws = columns.getJSONObject(4).getInt("v");
                int losses = columns.getJSONObject(5).getInt("v");
                int points = columns.getJSONObject(19).getInt("v");
                Team team = new Team(position, name, wins, draws, losses, points);
                teams.add(team);
            }
            final TeamsAdapter adapter = new TeamsAdapter(this, R.layout.team, teams);
            listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
           {

               View rootView2 = inflater.inflate(R.layout.fragment_overview, container, false);
               LineChart chart = (LineChart) rootView2.findViewById(R.id.chart);
               Legend legend = chart.getLegend();
               XAxis xAxis = chart.getXAxis();
               xAxis.setDrawGridLines(false);
               xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
               xAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
               xAxis.setAvoidFirstLastClipping(true);
               YAxis leftAxis = chart.getAxisLeft();
               leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

               /*leftAxis.addLimitLine(ll1);
               leftAxis.addLimitLine(ll2);
               leftAxis.addLimitLine(ll3);
               leftAxis.addLimitLine(ll4);
               */

               leftAxis.setTextColor(getResources().getColor(R.color.glucosio_text_light));
               leftAxis.setStartAtZero(false);
               //leftAxis.setYOffset(20f);
               leftAxis.disableGridDashedLine();
               leftAxis.setDrawGridLines(false);

               // limit lines are drawn behind data (and not on top)
               leftAxis.setDrawLimitLinesBehindData(true);

               chart.getAxisRight().setEnabled(false);
               chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
               chart.setDescription("");
               chart.setGridBackgroundColor(Color.parseColor("#FFFFFF"));
               // setData();
               ArrayList<String> xVals = new ArrayList<String>();
               ArrayList<Integer> colors = new ArrayList<>();
               ArrayList<Entry> yVals = new ArrayList<Entry>();
               //Set Data Value /
               for(int i =0; i<15;i++) {
                   xVals.add(""+i);
                   float val = i;
                   yVals.add(new Entry(val, i));
               }

               LineDataSet set1 = new LineDataSet(yVals, "");
               // set the line to be drawn like this "- - - - - -"
               set1.setColor(getResources().getColor(R.color.glucosio_pink));
               set1.setCircleColors(colors);
               set1.setLineWidth(0f);
               set1.setCircleSize(2.8f);
               set1.setDrawCircleHole(false);
               set1.disableDashedLine();
               set1.setFillAlpha(255);
               set1.setDrawFilled(true);
               set1.setValueTextSize(0);
               set1.setValueTextColor(Color.parseColor("#FFFFFF"));
               set1.setFillColor(Color.parseColor("#FCE2EA"));
               colors.add(getResources().getColor(R.color.glucosio_reading_ok));
               if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
                   set1.setDrawFilled(false);
                   set1.setLineWidth(3f);
                   set1.setCircleSize(4.5f);
                   set1.setDrawCircleHole(true);

               }

               ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
               dataSets.add(set1); // add the datasets

               LineData data = new LineData(xVals, dataSets);

               chart.setData(data);
               chart.setPinchZoom(true);
               chart.setHardwareAccelerationEnabled(true);
               chart.animateY(1000, Easing.EasingOption.EaseOutCubic);

               legend.setEnabled(false);
               return rootView2;

           }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){

                View rootView2 = inflater.inflate(R.layout.fragment_list, container, false);

               listview = (ListView) rootView2.findViewById(R.id.listview);
               btnDownload = (Button) rootView2.findViewById(R.id.btnDownload);
               //ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
              // NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
               btnDownload.setEnabled(true);

               return rootView2;
            }else {

               View rootView1 = inflater.inflate(R.layout.fragment_submit, container, false);

               Button sendButton = (Button)rootView1.findViewById(R.id.bSubmit);
               BEatEditText = (EditText)rootView1.findViewById(R.id.etDate);
               AEatEditText = (EditText)rootView1.findViewById(R.id.etSugarLevel);
               BP_SystolEditText = (EditText) rootView1.findViewById(R.id.etBPSystol);
               BP_DyastolEditText = (EditText) rootView1.findViewById(R.id.etBPDiaystol);

               sendButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       //Make sure all the fields are filled with values
                       if (TextUtils.isEmpty(BEatEditText.getText().toString()) &&
                               TextUtils.isEmpty(AEatEditText.getText().toString()) &&
                               TextUtils.isEmpty(BP_SystolEditText.getText().toString()) &&
                               TextUtils.isEmpty(BP_DyastolEditText.getText().toString())) {
                           Toast.makeText(context, "Any fields are mandatory.", Toast.LENGTH_LONG).show();
                           return;
                       }
                       //Create an object for PostDataTask AsyncTask
                       PostDataTask postDataTask = new PostDataTask();

                       //execute asynctask
                       postDataTask.execute(URL, BEatEditText.getText().toString(),
                               AEatEditText.getText().toString(),
                               BP_SystolEditText.getText().toString(),
                               BP_DyastolEditText.getText().toString());
                   }
               });
               return rootView1;
           }
          /* else {

               View rootView1 = inflater.inflate(R.layout.fragment_graph, container, false);
               GraphView graph = (GraphView) rootView1.findViewById(R.id.graph);

               StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
               staticLabelsFormatter.setHorizontalLabels(new String[]{"Jan", "Feb", "Mar", "Apr", "May"});
               // staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
               graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

               LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                       new DataPoint(0, 1d),
                       new DataPoint(1, 5d),
                       new DataPoint(2, 3d),
                       new DataPoint(3, 2d),
                       new DataPoint(4, 10d),
                       new DataPoint(5, 9d),
                       new DataPoint(6, 19d)

               });
               series.setThickness(2);
               graph.addSeries(series);

               LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                       new DataPoint(0, 3d),
                       new DataPoint(1, 6d),
                       new DataPoint(2, 7d),
                       new DataPoint(3, 3d),
                       new DataPoint(4, 11d),
                       new DataPoint(5, 10d),
                       new DataPoint(6, 18d)

               });

               series2.setColor(Color.RED);
               series2.setThickness(2);
               graph.addSeries(series2);
               series.setTitle("After");
               series2.setTitle("Before");
               graph.getLegendRenderer().setVisible(true);
               graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

               return rootView1;
            }*/
        }
    }

    private static void setData() {
        for (int i = 0; i < 10; i++) {
            String date = "10";
          //  xVals.add(date + "");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "History";
                case 2:
                    return "Reports";
            }
            return null;
        }
    }

    private static class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String BEat = contactData[1];
            String AEat = contactData[2];
            String BPSystol = contactData[3];
            String BPDystol = contactData[4];
            String postBody="";
            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = BEat_KEY+"=" + URLEncoder.encode(BEat, "UTF-8") +
                        "&" + AEat_KEY + "=" + URLEncoder.encode(AEat,"UTF-8")+
                        "&" + BP_Systol_KEY + "=" + URLEncoder.encode(BPSystol,"UTF-8") +
                "&" + BP_Dyastol_KEY + "=" + URLEncoder.encode(BPDystol,"UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }
            try{
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            }catch (IOException exception){
                result=false;
            }
            return result;
        }
        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            Toast.makeText(context, result ? "Message successfully sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }
    }
}
