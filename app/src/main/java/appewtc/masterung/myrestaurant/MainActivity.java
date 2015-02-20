package appewtc.masterung.myrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    private UserTABLE objUserTABLE;
    private OrderTABLE objOrderTABLE;
    private EditText edtUser, edtPassword;
    private String strUserChoose, strPasswordChoose, strPasswordTrue, strName;
    private FoodTABLE objFoodTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        bindWidget();

        objUserTABLE = new UserTABLE(this);
        objOrderTABLE = new OrderTABLE(this);
        objFoodTABLE = new FoodTABLE(this);

        //Tester
        //testAddValue();

        //delete All Data
        deleteAllData();

        //synJsonToSQLte
        synJSonToSQLite();

    }   // onCreate

    public void clickLogin(View view) {

        strUserChoose = edtUser.getText().toString().trim();
        strPasswordChoose = edtPassword.getText().toString().trim();

        if (strUserChoose.equals("") || strPasswordChoose.equals("") ) {

            //Alert Error
            MyAlertDialog objMyAlert = new MyAlertDialog();
            objMyAlert.errorDialog(MainActivity.this, "Have Space", "Please Fill in Every Blank");

        } else {

            checkUser();

        } // if

    }   // clickLogin

    private void checkUser() {

        try {

            String strData[] = objUserTABLE.searchUser(strUserChoose);
            strPasswordTrue = strData[2];
            strName = strData[3];

            Log.d("Restaurant", "Wellcom " + strName);

            checkPassword();

        } catch (Exception e) {
            MyAlertDialog objMyAlert = new MyAlertDialog();
            objMyAlert.errorDialog(MainActivity.this, "No This User", "NO " + strUserChoose + " in my Database");
        }

    }   // checkUser

    private void checkPassword() {

        if (strPasswordChoose.equals(strPasswordTrue)) {

            //Intent to Order Activity
            wellcomeUser();

        } else {

            MyAlertDialog objMyAlert = new MyAlertDialog();
            objMyAlert.errorDialog(MainActivity.this, "Password False", "Please Try agains Password False");

        }

    }   // checkPassword

    private void wellcomeUser() {

        final AlertDialog.Builder objAlert = new AlertDialog.Builder(this);
        objAlert.setIcon(R.drawable.friend);
        objAlert.setTitle("Wellcome to My Restaurant");
        objAlert.setMessage("Wellcome " + strName + "\n" + "To My Restaurant");
        objAlert.setCancelable(false);
        objAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent objIntent = new Intent(MainActivity.this, OrderActivity.class);
                objIntent.putExtra("Name", strName);
                startActivity(objIntent);
                finish();
            }
        });
        objAlert.show();

    }   // wellcomeUser

    private void bindWidget() {
        edtUser = (EditText) findViewById(R.id.editText);
        edtPassword = (EditText) findViewById(R.id.editText2);
    }   // bindWidget

    private void deleteAllData() {

        SQLiteDatabase objSQLite = openOrCreateDatabase("Restaurant.db", MODE_PRIVATE, null);
        objSQLite.delete("userTABLE", null, null);


    }   // deleteAllData

    private void synJSonToSQLite() {

        //setUp Policy
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);
        }   // if

        InputStream objInputStream = null;
        String strJSON = "";

        //Create objInputStream
        try {

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/rest/get_data_user_rest.php");
            HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
            HttpEntity objHttpEntity = objHttpResponse.getEntity();
            objInputStream = objHttpEntity.getContent();

        } catch (Exception e) {
            Log.d("Restaurant", "Error from InputStream ==> " + e.toString());
        }


        //Change InputStream to String
        try {

            BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
            StringBuilder objStringBuilder = new StringBuilder();
            String strLine = null;

            while ((strLine = objBufferedReader.readLine()) != null ) {
                objStringBuilder.append(strLine);
            }   // while

            objInputStream.close();
            strJSON = objStringBuilder.toString();

        } catch (Exception e) {
            Log.d("Restaurant", "Error Create String ==> " + e.toString());
        }


        //Up Value to SQLite
        try {

            final JSONArray objJSONArray = new JSONArray(strJSON);
            for (int i = 0; i < objJSONArray.length(); i++) {

                JSONObject objJSONObject = objJSONArray.getJSONObject(i);
                String strUser = objJSONObject.getString("User");
                String strPassword = objJSONObject.getString("Password");
                String strOfficer = objJSONObject.getString("Officer");

                long insertVale = objUserTABLE.addValueToUser(strUser, strPassword, strOfficer);

            }   // for

        } catch (Exception e) {
            Log.d("Restaurant", "Error Up Value ==> " + e.toString());
        }


    }   // synJsonToSQLite

    private void testAddValue() {

        objUserTABLE.addValueToUser("User", "Password", "Officer");
        objOrderTABLE.addValueOrder("Officer", "Date", "Food", 4);
        objFoodTABLE.addValueToFood("Food", "Price");

    }   // testAddValue


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}   // Main Class
