package seguridad.elca.elcaremito;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Remito extends ActionBarActivity {

    String idped,idusuar;

    private DrawingView drawView;


    EditText observaciones,aclaracion;


    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remito);
        idped = getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");

        cargadisp(idped);

        drawView = (DrawingView)findViewById(R.id.drawing);
        observaciones = (EditText)findViewById(R.id.observaciones);
        aclaracion = (EditText)findViewById(R.id.aclaracion);


    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Remito.this, Agregar_dispositivos.class);
            i.putExtra("idpedido", idped );
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargadisp(String idped)
    {


        //inicia lista
        ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(idped);

        if(dipslist.size()!=0){
            //Set the User Array list in ListView
            //ListAdapter adapter = new SimpleAdapter( Remito.this,dipslist, R.layout.view_remito, new String[] {"nombre"}, new int[] {R.id.nomdisp});

            ListAdapter adapter = new SimpleAdapter( Remito.this,dipslist, R.layout.view_remito, new String[] { "nombre"}, new int[] {R.id.nomdipo});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
            //Display Sync status of SQLite DB
            // Toast.makeText(getApplicationContext(), controller.getSyncStatus(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "No tiene dispositivos instaldos", Toast.LENGTH_LONG).show();}
        /*
        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
*/

    }


    public void savere(View view)
    {
        //String encodedString;
       // System.out.println("hola");
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Guardar Remito");
        saveDialog.setMessage("Desea guardar el remito?");
        saveDialog.setPositiveButton("Si", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){


                //save drawing
                drawView.setDrawingCacheEnabled(true);

                Bitmap image = drawView.getDrawingCache();
                image = redimensionarImagenMaximo(image,200,55);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50 , stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String

                String encodedString = Base64.encodeToString(byte_arr, 0);


                HashMap<String, String> queryValues = new HashMap<String, String>();

                queryValues.put("idpedido", idped);
                queryValues.put("observaciones",observaciones.getText().toString());
                queryValues.put("aclaracion",aclaracion.getText().toString());
                queryValues.put("firma",encodedString);
                queryValues.put("horafinal", tiempo());

                controller.upfoto(queryValues);

                controller.upload_aux(idped);

                //controller.getdisp(idped);


                drawView.destroyDrawingCache();




                Intent i = new Intent(Remito.this, Pedidos.class);
                i.putExtra("idpedido", idped );
                i.putExtra("idusuario",idusuar );
                startActivity(i);



            }
        });
        saveDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }


    public String tiempo()
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();
        //long dia = date.getTime();
        int year = date.getYear()-100;
        System.out.println (year);
        //System.out.println(""+date.getHours()+":"+date.getMinutes()+" "+date.getDay()+"/"+date.getMonth()+"/"+date.getYear());
        String time = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getDay() + "/" + date.getMonth() + "/" + year;
        return time;
    }



    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }






}
