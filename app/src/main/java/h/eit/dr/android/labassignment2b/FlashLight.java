package h.eit.dr.android.labassignment2b;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.app.ActionBar.LayoutParams;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class FlashLight extends AppCompatActivity implements OnClickListener {
    int tvCol;
    TextView myTextView;
    Button ButtonW;
    Button ButtonB;
    Button ButtonR;
    Button ButtonY;
    Button ButtonG;
    LinearLayout one;
    Toolbar toolbar;
    int layout_counter=1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String VISI = "visibility";
    public static final String COLO = "color";
    public static final String RADIND = "radind";
    public static final String BAT = "batlevel";
    private AsyncTask alphaTask;
    private int visGet;
    private int visSelect;
    private int colGet;
    private int indexGet;
    private int colSelect;
    private int indexSelect;
    private static int levelGet;
    private static boolean checkBox1 = false;
    private static boolean checkBox2 = false;
    private static boolean checkBox3 = false;
    private static myasyTask myAsyncTask;
    private static boolean checkSos = false;
    private boolean sosStartCheck = false;
    private int batLevelSet;
    private mBatInfoReceiver myBatReciever;
    String tag = "savedData : ";
    View toDisplayInDialog;
    View toDisplayInDialog1;
    private boolean asyncMode = true;
    PowerManager pm;
    protected static final int BATTERY_LOW =0,DEVICE_ON = 1,DEVICE_OFF = 2,BATTERY_HIGH = 3;
    private Handler myHandler = new Handler(){
        PowerManager.WakeLock wl;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case BATTERY_LOW:
                    System.out.println("Dude Battery is too LOW");
                    asyncMode = false;
                    break;
                case DEVICE_ON:
                    wl= pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"myApp : My Tag");
                    wl.acquire();
                    System.out.println("In Device On state");
                    break;
                case DEVICE_OFF:
                    wl.release();
                    System.out.println("In Device Off state");
                    break;
                case BATTERY_HIGH:
                    System.out.println("Dude Battery is HIGH");
                    asyncMode = true;
                    break;
            }
        }
    };
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_flash_light);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        int visCheck = sharedPreferences.getInt(VISI,View.INVISIBLE);
        if(visCheck == View.INVISIBLE){
            openBox(this);
        }
        one = (LinearLayout) findViewById(R.id.button_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this.registerReceiver(this.mBatInfoReciever,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int colCheck = sharedPreferences.getInt(COLO,Color.CYAN);
        if(colCheck == Color.CYAN){
            openBox2();
        }
        //System.out.println("Battery Level on sharedPreference : "+levelGet);
        int levelCheck = sharedPreferences.getInt(BAT,10);
        if(levelCheck == 0){
            openbox3();
        }
        myTextView = findViewById(R.id.textView);
        myTextView.setBackgroundColor(colorSelector.WHITE.getColor());
        ButtonW = findViewById(R.id.button_w);
        ButtonB = findViewById(R.id.button_b);
        ButtonR = findViewById(R.id.button_r);
        ButtonY = findViewById(R.id.button_y);
        ButtonG = findViewById(R.id.button_g);
        myTextView.setOnClickListener(this);
        ButtonW.setOnClickListener(this);
        ButtonB.setOnClickListener(this);
        ButtonR.setOnClickListener(this);
        ButtonY.setOnClickListener(this);
        ButtonG.setOnClickListener(this);
        loadData();
        updateViews();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //batLevelChecker();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_select1:
                //one.setVisibility(View.GONE);
                //visSelect = View.GONE;
                openBox(this);
                Toast.makeText(this, "visibility option selected", Toast.LENGTH_SHORT);
                //saveData();
                return true;
            case R.id.menu_select2:
                //one.setVisibility(View.VISIBLE);
                //visSelect = View.VISIBLE;
                /*if(toDisplayInDialog != null){
                    ((ViewGroup) toDisplayInDialog.getParent()).removeView(toDisplayInDialog);
                }*/
                //toDisplayInDialog = getLayoutInflater().inflate(R.layout.radiogroup,null);
                openBox2();
                Toast.makeText(this, "Color Option selected", Toast.LENGTH_SHORT);
                //saveData();
                return true;
            case R.id.menu_select3:
                sosStartCheck = true;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                batLevelChecker();
                SOSActivity();
                //SOSActivityActioner();
                Toast.makeText(this,"SOS button pressed",Toast.LENGTH_SHORT);
                return true;
            case R.id.menu_select4:
                openbox3();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v){
        int id=v.getId();
        switch(id){
            case R.id.button_w:
                myTextView.setBackgroundColor(colorSelector.WHITE.getColor());
                if(myTextView.getCurrentTextColor() == colorSelector.WHITE.getColor()){
                    myTextView.setTextColor(colorSelector.BLACK.getColor());
                }
                //alphaTask.cancel(true);
                //one.setVisibility(View.GONE);
                //layout_counter++;
                break;
            case R.id.button_b:
                myTextView.setBackgroundColor(colorSelector.BLACK.getColor());
                myTextView.setTextColor(colorSelector.WHITE.getColor());
                //alphaTask.cancel(true);
                //one.setVisibility(View.GONE);
                //layout_counter++;
                break;
            case R.id.button_r:
                myTextView.setBackgroundColor(colorSelector.RED.getColor());
                if(myTextView.getCurrentTextColor() == colorSelector.WHITE.getColor()){
                    myTextView.setTextColor(colorSelector.BLACK.getColor());
                }
                //alphaTask.cancel(true);
                //one.setVisibility(View.GONE);
                //layout_counter++;
                break;
            case R.id.button_y:
                myTextView.setBackgroundColor(colorSelector.YELLOW.getColor());
                if(myTextView.getCurrentTextColor() == colorSelector.WHITE.getColor()){
                    myTextView.setTextColor(colorSelector.BLACK.getColor());
                }
                //alphaTask.cancel(true);
                //one.setVisibility(View.GONE);
                //layout_counter++;
                break;
            case R.id.button_g:
                myTextView.setBackgroundColor(colorSelector.GREEN.getColor());
                if(myTextView.getCurrentTextColor() == colorSelector.WHITE.getColor()){
                    myTextView.setTextColor(colorSelector.BLACK.getColor());
                }
                //alphaTask.cancel(true);
                //one.setVisibility(View.GONE);
                //layout_counter++;
                break;
            case R.id.textView:
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                try {
                    if (myAsyncTask.getStatus() == myasyTask.Status.RUNNING) {
                        myAsyncTask.cancel(true);
                        sosStartCheck = false;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        Toast.makeText(FlashLight.this, "SOS aborted", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    Log.d("In for first time : ",e.toString());
                }
                if(layout_counter%2 == 0) {
                    one.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    myTextView.setText("Touch the screen to show/hide buttons");
                    if(myTextView.getCurrentTextColor() == colorSelector.WHITE.getColor()){
                        myTextView.setTextColor(colorSelector.WHITE.getColor());
                    }
                }
                else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    one.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    myTextView.setText("");
                }
                layout_counter++;
                break;
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        myTextView = findViewById(R.id.textView);
        ColorDrawable cd = (ColorDrawable)myTextView.getBackground();
        int colorId = cd.getColor();
        one = (LinearLayout) findViewById(R.id.button_group);
        savedInstanceState.putString("txtVal",myTextView.getText().toString());
        savedInstanceState.putInt("colVal",colorId);
        savedInstanceState.putInt("visVal",one.getVisibility());
        savedInstanceState.putInt("contVal",layout_counter);
        savedInstanceState.putInt("txtColVal",myTextView.getCurrentTextColor());
        savedInstanceState.putInt("toolVal",toolbar.getVisibility());
        savedInstanceState.putInt("flagVal",getWindow().getAttributes().flags);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        myTextView = findViewById(R.id.textView);
        super.onRestoreInstanceState(savedInstanceState);
        myTextView.setText(savedInstanceState.getString("txtVal"));
        myTextView.setBackgroundColor(savedInstanceState.getInt("colVal"));
        myTextView.setTextColor(savedInstanceState.getInt("txtColVal"));
        one.setVisibility(savedInstanceState.getInt("visVal"));
        layout_counter = savedInstanceState.getInt("contVal");
        toolbar.setVisibility(savedInstanceState.getInt("toolVal"));
        getWindow().addFlags(savedInstanceState.getInt("flagVal"));
        if(checkBox1 == true){
            openBox(this);
        }
        if(checkBox2 == true){
            openBox2();
        }
        if(checkBox3 == true){
            openbox3();
        }
        if(checkSos == true){
            checkSos = false;
            sosStartCheck = true;
            batLevelChecker();
            //myAsyncTask.execute(". . . _ _ _ . . .");
            SOSActivity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (myAsyncTask.getStatus() == myasyTask.Status.RUNNING) {
                checkSos = true;
                myAsyncTask.cancel(true);
                sosStartCheck = false;
            }
        }
        catch(Exception e){
            Log.d("In for first time : ",e.toString());
        }
    }

    public void openBox(FlashLight view){
        checkBox1 = true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Button Visibility Decision");
        alertDialogBuilder.setPositiveButton("Hidden", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                checkBox1 = false;
                Toast.makeText(FlashLight.this,"You clicked Hidden button",Toast.LENGTH_LONG).show();
                //one.setVisibility(View.GONE);
                visSelect = View.GONE;
                saveData();
            }
        });

        alertDialogBuilder.setNegativeButton("Visible", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg2, int arg3) {
                checkBox1 = false;
                Toast.makeText(FlashLight.this,"You clicked Visible button",Toast.LENGTH_LONG).show();
                //one.setVisibility(View.VISIBLE);
                visSelect = View.VISIBLE;
                saveData();
            }
        });

        alertDialogBuilder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkBox1 = false;
                Toast.makeText(FlashLight.this,"You clicked Cancel button",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openBox2(){
        checkBox2 = true;
        toDisplayInDialog = getLayoutInflater().inflate(R.layout.radiogroup,null,false);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        try{
            indexGet = sharedPreferences.getInt(RADIND,0);
            RadioGroup myRadioGroup = (RadioGroup) toDisplayInDialog.findViewById(R.id.radioButtons);
            RadioButton myRadioButton = (RadioButton) myRadioGroup.getChildAt(indexGet);
            myRadioButton.setChecked(true);
        }
        catch(Exception e){
            Log.d("First time entry",e.toString());
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashLight.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Choose Startup Color").create();
        alertDialogBuilder.setView(toDisplayInDialog);
        alertDialogBuilder.setPositiveButton("Set Startup Color", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                chooseColor();
                checkBox2 = false;
                //one.setVisibility(View.GONE);
                //saveData();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkBox2 = false;
                Toast.makeText(FlashLight.this,"You clicked Cancel button",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        //AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.create().show();
    }

    public void openbox3(){
        checkBox3 = true;
        toDisplayInDialog1 = getLayoutInflater().inflate(R.layout.textbox,null,false);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        try{
            int level = sharedPreferences.getInt(BAT,10);
            EditText myEdit1 = (EditText) toDisplayInDialog1.findViewById(R.id.edit_Text);
            myEdit1.setText(String.valueOf(level));
        }
        catch (Exception e){
            Log.d("In for the first time:",e.toString());
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashLight.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Please enter the threshold value of Battery level").create();
        alertDialogBuilder.setView(toDisplayInDialog1);
        alertDialogBuilder.setPositiveButton("Set Threshold Level", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                checkBox3 = false;
                //System.out.println(batLevelSet + " : My threshold level set initially");
                EditText myEdit = (EditText) toDisplayInDialog1.findViewById(R.id.edit_Text);
                batLevelSet = Integer.parseInt(myEdit.getText().toString());
                saveDataBat();
                //System.out.println(batLevelSet + " : My threshold level set");
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkBox3 = false;
                Toast.makeText(FlashLight.this,"You clicked Cancel button",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        //AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.create().show();
    }

    public void SOSActivity(){
        //myTextView.setBackgroundColor(Color.WHITE);
        myAsyncTask =new myasyTask(this);
        myAsyncTask.execute(". . . _ _ _ . . .");

    }

    public void SOSActivityActioner(){
        int counter = 0;
        while(sosStartCheck){
            if(counter == 0){
                SOSActivity();
                counter = 1;
            }
            if(asyncMode == true){
                if(myAsyncTask.getStatus() == myasyTask.Status.FINISHED){
                    myAsyncTask.cancel(true);
                    SOSActivity();
                }
            }
            else{
                new Thread(){
                    public void run(){
                        try{
                            myHandler.sendEmptyMessage(DEVICE_ON);
                        }
                        catch(Exception e){
                            Log.d("WTF :",e.toString());
                        }
                    }
                }.start();
                if(myAsyncTask.getStatus()== myasyTask.Status.FINISHED){
                    myAsyncTask.cancel(true);
                    new Thread(){
                        public void run(){
                            try{
                                myHandler.sendEmptyMessage(DEVICE_OFF);
                            }
                            catch(Exception e){
                                Log.d("WTF :",e.toString());
                            }
                        }
                    }.start();
                    SOSActivity();
                }
            }
        }
    }

    public void chooseColor(){
        RadioGroup myRadioGroup = (RadioGroup) toDisplayInDialog.findViewById(R.id.radioButtons);
        int radioGroupId = myRadioGroup.getCheckedRadioButtonId();
        RadioButton myCheckedButton = (RadioButton) toDisplayInDialog.findViewById(radioGroupId);
        int index = myRadioGroup.indexOfChild(myCheckedButton);
        indexSelect = index;
        Toast.makeText(FlashLight.this,"You clicked Set Startup Color button",Toast.LENGTH_LONG).show();
        switch(index){
            case 0:
                //myTextView.setBackgroundColor(Color.WHITE);
                colSelect = colorSelector.WHITE.getColor();
                saveDataColor();
                break;
            case 1:
                //myTextView.setBackgroundColor(Color.BLACK);
                colSelect = colorSelector.BLACK.getColor();
                saveDataColor();
                break;
            case 2:
                //myTextView.setBackgroundColor(Color.RED);
                colSelect = colorSelector.RED.getColor();
                saveDataColor();
                break;
            case 3:
                //myTextView.setBackgroundColor(Color.YELLOW);
                colSelect = colorSelector.YELLOW.getColor();
                saveDataColor();
                break;
            case 4:
                //myTextView.setBackgroundColor(Color.GREEN);
                colSelect = colorSelector.GREEN.getColor();
                saveDataColor();
                break;
        }
    }
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt(VISI ,one.getVisibility());
        editor.putInt(VISI ,visSelect);
        editor.apply();
        Log.d(tag,"In save Data");
    }
    public void saveDataColor(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //ColorDrawable cd = (ColorDrawable)myTextView.getBackground();
        //int colorId = cd.getColor();
        //editor.putInt(COLO ,colorId);
        editor.putInt(RADIND,indexSelect);
        editor.putInt(COLO ,colSelect);
        editor.apply();
        Log.d(tag,"In save Data Color");
    }
    public void saveDataBat(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BAT,batLevelSet);
        editor.apply();
        Log.d(tag,"In save Data Battery Level");
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        visGet = sharedPreferences.getInt(VISI,View.VISIBLE);
        colGet = sharedPreferences.getInt(COLO,Color.WHITE);
        levelGet = sharedPreferences.getInt(BAT,10);
        //indexGet = sharedPreferences.getInt(RADIND,0);
        Log.d(tag,"In load Data");
    }

    public void updateViews(){
        one.setVisibility(visGet);
        myTextView.setBackgroundColor(colGet);
        Log.d(tag,"In update views");
    }


    public void batLevelChecker(){
        myBatReciever = new mBatInfoReceiver(this);
        this.registerReceiver(this.myBatReciever,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private static class myasyTask extends android.os.AsyncTask<String,Integer,Void>{

        private WeakReference<FlashLight> activityWeakReference;

        myasyTask(FlashLight activity){
            activityWeakReference = new WeakReference<FlashLight>(activity);
        }
        int tvCol;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            ColorDrawable cd = (ColorDrawable) myFlashLight.myTextView.getBackground();
            tvCol = cd.getColor();
        }

        @Override
        protected Void doInBackground(String... strings) {
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return null;
            }
            while(myFlashLight.sosStartCheck) {
                if(myFlashLight.asyncMode == false){
                    new Thread(){
                        FlashLight myFlashLight = activityWeakReference.get();
                        public void run(){
                            try{
                                myFlashLight.myHandler.sendEmptyMessage(DEVICE_ON);
                            }
                            catch(Exception e){
                                Log.d("WTF :",e.toString());
                            }
                        }
                    }.start();
                }
                for (int i = 0; i < strings[0].length(); i++) {
                    //publishProgress(strings[0].charAt(i));
                    //myFlashLight.batLevelChecker();
                    if (this.isCancelled()) {
                        System.out.println("I'm here");
                        return null;
                    }
                    if (strings[0].charAt(i) == '.') {
                        //changeColor(1);
                        publishProgress(1);
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (strings[0].charAt(i) == '_') {
                        //changeColor(2);
                        publishProgress(2);
                        try {
                            Thread.sleep(800);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //changeColor(3);
                        publishProgress(3);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(myFlashLight.asyncMode == false){
                    new Thread(){
                        FlashLight myFlashLight = activityWeakReference.get();
                        public void run(){
                            try{
                                myFlashLight.myHandler.sendEmptyMessage(DEVICE_OFF);
                            }
                            catch(Exception e){
                                Log.d("WTF :",e.toString());
                            }
                        }
                    }.start();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //System.out.println(values[0].length());
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            switch(values[0]){
                case 1:
                    System.out.println("in 1");
                    myFlashLight.myTextView.setBackgroundColor(Color.MAGENTA);
                    /*try{Thread.sleep(250);}
                    catch(Exception e){e.printStackTrace();}*/
                    break;
                case 2:
                    System.out.println("in 2");
                    myFlashLight.myTextView.setBackgroundColor(Color.MAGENTA);
                    /*try{Thread.sleep(400);}
                    catch(Exception e){e.printStackTrace();}*/
                    break;
                case 3:
                    System.out.println("in 3");
                    myFlashLight.myTextView.setBackgroundColor(Color.GRAY);
                    /*try{Thread.sleep(500);}
                    catch(Exception e){e.printStackTrace();}*/
                    break;
            }

            //super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            myFlashLight.myTextView.setBackgroundColor(tvCol);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            myFlashLight.myTextView.setBackgroundColor(tvCol);
        }

        public void changeColor(int val){
            //myTextView = findViewById(R.id.textView);
            //System.out.println(tvCol);
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            switch(val){
                case 1:
                    System.out.println("in 1");
                    myFlashLight.myTextView.setBackgroundColor(Color.MAGENTA);
                    try{Thread.sleep(250);}
                    catch(Exception e){e.printStackTrace();}
                    break;
                case 2:
                    System.out.println("in 2");
                    myFlashLight.myTextView.setBackgroundColor(Color.MAGENTA);
                    try{Thread.sleep(400);}
                    catch(Exception e){e.printStackTrace();}
                    break;
                case 3:
                    System.out.println("in 3");
                    myFlashLight.myTextView.setBackgroundColor(Color.GRAY);
                    try{Thread.sleep(500);}
                    catch(Exception e){e.printStackTrace();}
                    break;
            }
        }
    }
    /*private BroadcastReceiver mBatInfoReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            System.out.println("Current battery level is: ");
            System.out.println(level);
            System.out.println("%");
        }
    };*/
    private static class mBatInfoReceiver extends BroadcastReceiver {
        private WeakReference<FlashLight> activityWeakReference;
        private int level;
        mBatInfoReceiver(FlashLight activity){
            activityWeakReference = new WeakReference<FlashLight>(activity);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            FlashLight myFlashLight = activityWeakReference.get();
            if(myFlashLight == null || myFlashLight.isFinishing()){
                return;
            }
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            //System.out.println("Broadcast Info battery level ="+level+"%");
            myFlashLight.myTextView.setText(""+
                    "\n"+"Battery Level is "+String.valueOf(level)+"%");
            if(level < myFlashLight.levelGet){
                System.out.println("Battery Low");
                myFlashLight.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                new Thread(){
                    FlashLight myFlashLight = activityWeakReference.get();
                    public void run(){
                        try{
                            myFlashLight.myHandler.sendEmptyMessage(BATTERY_LOW);
                        }
                        catch(Exception e){
                            Log.d("WTF :",e.toString());
                        }
                    }
                }.start();
            }
            else{
                new Thread(){
                    FlashLight myFlashLight = activityWeakReference.get();
                    public void run(){
                        try{
                            myFlashLight.myHandler.sendEmptyMessage(BATTERY_HIGH);
                        }
                        catch(Exception e){
                            Log.d("WTF :",e.toString());
                        }
                    }
                }.start();
            }
        }
    }
    public enum colorSelector{
        RED(Color.RED),
        GREEN(Color.GREEN),
        YELLOW(Color.YELLOW),
        WHITE(Color.WHITE),
        BLACK(Color.BLACK);
        private int color;
        colorSelector(int c){
            color = c;
        }
        public int getColor(){
            return color;
        }
    }
}


