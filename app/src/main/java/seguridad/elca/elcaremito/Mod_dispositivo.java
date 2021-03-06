package seguridad.elca.elcaremito;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import seguridad.elca.elcaremito.android.IntentIntegrator;
import seguridad.elca.elcaremito.android.IntentResult;

public class Mod_dispositivo extends AppCompatActivity implements OnClickListener {

    DBController controller = new DBController(this);
    EditText nombre,descripcion,latitud,longitud,tiemp;
    TextView codigo;
    //private Button scanBtn;

    String idped,code,idusuar;
    private  Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_dispositivo);


        //scanBtn = (Button)findViewById(R.id.scan_button);
        codigo = (TextView) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        scanBtn=(Button) findViewById(R.id.scan_button);


        idped= getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");

        code=getIntent().getStringExtra("codigoscan");

        scanBtn.setOnClickListener(this);

       // System.out.println(code);
        carga_datos(code);

    }


    //////////////**********************CARGAR DATOS************/////////////////


    private void carga_datos(String code) {


        ArrayList<HashMap<String, String>> dipslist =  controller.getdispcod(code);


        for (HashMap<String, String> hashMap : dipslist) {
            //if (){}

           // System.out.println(hashMap.get("codigoscan"));
            // controller.dipsup(hashMap.get("codigoscan"));
            codigo.setText(hashMap.get("codigoscan"));
           // codigo.setText("nicolas");
            nombre.setText(hashMap.get("nombre"));
            descripcion.setText(hashMap.get("descripcion"));

        }

    }







    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            //formatTxt.setText("FORMAT: " + scanFormat);
            descripcion.setText(scanContent);
            //tiemp.setText(tiempo());
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
            Intent i = new Intent(Mod_dispositivo.this, Agregar_dispositivos.class);
            i.putExtra("idpedido", idped );
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
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




    ////////////////*********************CLICK EN EL BOTON*************////////////



    public void moddisp(View view) {


        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("codigo", codigo.getText().toString());
        queryValues.put("nombre", nombre.getText().toString());
        queryValues.put("descripcion", descripcion.getText().toString());

        //System.out.println(tiempo());

        controller.updips(queryValues);

        this.callHomeActivity(view);

    }




    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            System.out.println("aqui");
        }
        System.out.println("ok");

    }
}
