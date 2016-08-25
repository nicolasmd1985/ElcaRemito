package seguridad.elca.elcaremito;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import seguridad.elca.elcaremito.android.IntentIntegrator;
import seguridad.elca.elcaremito.android.IntentResult;

public class Scaner_dispositivo extends AppCompatActivity implements OnClickListener {

    DBController controller = new DBController(this);


    EditText codigo, nombre, descripcion, latitud, longitud, tiemp;
    private Button scanBtn;

    String idped, idusuar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_dispositivo);

        idusuar = getIntent().getStringExtra("idusuario");


        scanBtn = (Button) findViewById(R.id.scan_button);
        // formatTxt = (TextView)findViewById(R.id.scan_format);
        // contentTxt = (TextView)findViewById(R.id.scan_content);


        codigo = (EditText) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);
        tiemp = (EditText) findViewById(R.id.tiempo);

        scanBtn.setOnClickListener(this);
        idped = getIntent().getStringExtra("idpedido");
        //System.out.println
        tiempo();

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                (LocationListener) mlocListener);

        }




    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }

    }

/////////////************************OBTIENE INFO DEL SCANER*****************////////////////


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            //formatTxt.setText("FORMAT: " + scanFormat);
            codigo.setText(scanContent);
            tiemp.setText(tiempo());
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /////////////////****************ESTO ES PARA DEVOLVERSE*****************/////////////////////

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Scaner_dispositivo.this, Agregar_dispositivos.class);
            i.putExtra("idpedido", idped );
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }




//////////////*********Aqui empieza la Clase Localizacion**************/

    /* Class My Location Listener */
    public class MyLocationListener implements LocationListener {
        Scaner_dispositivo mainActivity;

        public Scaner_dispositivo getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(Scaner_dispositivo mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este mŽtodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la detecci—n de un cambio de ubicacion
           // loc.getLatitude();
          //  loc.getLongitude();

            String lat= ""+loc.getLatitude();
            String lon= ""+loc.getLongitude();
            System.out.println(loc.getLatitude());
            System.out.println(loc.getLongitude());
            latitud.setText(lat);
            longitud.setText(lon);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este mŽtodo se ejecuta cuando el GPS es desactivado
           // messageTextView.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este mŽtodo se ejecuta cuando el GPS es activado
            //messageTextView.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este mŽtodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizaci—n (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temp˜ralmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* End of Class MyLocationListener */




    ////////////////*********************CLICK EN EL BOTON*************////////////



    public void adddip(View view) {


        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("codigo", codigo.getText().toString());
        queryValues.put("nombre", nombre.getText().toString());
        queryValues.put("descripcion", descripcion.getText().toString());
        queryValues.put("latitud", latitud.getText().toString());
        queryValues.put("longitud", longitud.getText().toString());
        queryValues.put("tiempo", tiempo());
        queryValues.put("fkidpedido", idped);
        //System.out.println(tiempo());

        controller.inserdips(queryValues);

        this.callHomeActivity(view);

    }



    ///////////////////////*******************OBTENER TIEMPO**************/////////////////

    public String tiempo()
    {
        //final Calendar cal = Calendar.getInstance();
        //cal.setTimeInMillis(System.currentTimeMillis());
        //cal.setTimeZone("GMT-8");
        Date date = new Date();
               //long dia = date.getTime();
        CharSequence s  = DateFormat.format("d/M/yyyy H:m", date.getTime());

        System.out.println (s);
        String time = s.toString();
        //System.out.println(""+date.getHours()+":"+date.getMinutes()+" "+date.getDay()+"/"+date.getMonth()+"/"+date.getYear());
        //String time = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getDay() + "/" + date.getMonth() + "/" + year;
        return time ;
    }



    /**
     * Navigate to Home Screen
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                Agregar_dispositivos.class);
        objIntent.putExtra("idpedido", idped );
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }

    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void canceldisp(View view) {
        this.callHomeActivity(view);
    }




}
