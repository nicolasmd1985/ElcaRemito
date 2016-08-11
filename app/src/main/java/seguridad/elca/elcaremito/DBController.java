package seguridad.elca.elcaremito;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by nicolas on 27/07/2016.
 */
public class DBController extends SQLiteOpenHelper {


     private static final String NOMBRE_BASE_DATOS = "remito.db";

    private static final int VERSION_ACTUAL = 1;

    private final Context contexto;




    public DBController(Context contexto) {
        super(contexto,NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query;
        ///////////////BASE DE USUARIOS//////////////////
        query = "CREATE TABLE usuarios ( idusuario INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, usuario TEXT, pass TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE AUX PEDIDOS//////////////////
        query = "CREATE TABLE aux_pedido ( idauxpedido INTEGER PRIMARY KEY, fkidusuario INTEGER REFERENCES idusuario, cliente TEXT, descripcion TEXT, idnumsoporte TEXT, calle TEXT, numero TEXT, ciudad TEXT, provincia TEXT, fechacr TEXT, fechack TEXT, finalizado TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE AUX PEDIDOS//////////////////
        query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT,  fkidauxpedido INTEGER REFERENCES idauxpedido)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE DEL REMITO//////////////////
        query = "CREATE TABLE remito ( id_remito INTEGER PRIMARY KEY, fkidauxpedido INTEGER REFERENCES idauxpedido, observaciones TEXT, firma BLOB, horafinalizado TEXT)";
        sqLiteDatabase.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS usuarios";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS aux_pedido";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS dispositivos";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS remito";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);


    }


///////////////////*****************VALORES DE USUARIO***************/////////////ok

    /**
     * Inserts User into SQLite DB   * @param queryValues
     */
    public void insertUsers(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("nombre", queryValues.get("nombre"));
        values.put("apellido", queryValues.get("apellido"));
        values.put("usuario", queryValues.get("usuario"));
        values.put("pass", queryValues.get("pass"));
        values.put("idusuario", queryValues.get("tecnico"));

        //values.put("udpateStatus", "no");
        database.insert("usuarios", null, values);
        database.close();
    }

////////////////////*************OBTENER LISTA DE USUARIOS***********///////////ok


    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getUsers() {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT  nombre,apellido,usuario,pass FROM usuarios ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("nombre", cursor.getString(0));
                map.put("apellido", cursor.getString(1));
                map.put("usuario", cursor.getString(2));
                map.put("pass", cursor.getString(3));
               // map.put("tecnico", cursor.getString(4));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ///////////////////////QUERY PARA ACCESO////////////////////////////////////ok
    public ArrayList<HashMap<String, String>> login() {
        ArrayList<HashMap<String, String>> logind;
        logind = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  idusuario,usuario,pass FROM usuarios";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("idusuario", cursor.getString(0));
                map.put("usuario", cursor.getString(1));
                map.put("pass", cursor.getString(2));

                logind.add(map);

            }while (cursor.moveToNext());
        }
        database.close();
        return logind;

    }




    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void inser_auxped(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("fkidusuario", queryValues.get("idtecnico"));
        values.put("idnumsoporte", queryValues.get("idnumsoporte"));
        values.put("descripcion", queryValues.get("descripcion"));
        values.put("cliente", queryValues.get("cliente"));
        values.put("calle", queryValues.get("calle"));
        values.put("numero", queryValues.get("numero"));
        values.put("ciudad", queryValues.get("ciudad"));
        values.put("provincia", queryValues.get("provincia"));
        values.put("fechacr", queryValues.get("fechacr"));
        values.put("fechack", queryValues.get("fechack"));


        //values.put("udpateStatus", "no");
        database.insert("aux_pedido", null, values);
        database.close();
    }




///////////////////**************OBTIENE USUARIO**************///////////OK
    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> get_auxped(String idusuar) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT  * FROM aux_pedido where fkidusuario = "+idusuar+" and finalizado is null  ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("idauxpedido", cursor.getString(0));
                //map.put("idtecnico", cursor.getString(1));
                map.put("cliente", cursor.getString(2));
                map.put("descripcion", cursor.getString(3));
                map.put("idnumsoporte", cursor.getString(4));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ///////////////////////QUERY PARA ACCESO////////////////////////////////////
    public ArrayList<HashMap<String, String>> listdetalle(String idpedido) {
        ArrayList<HashMap<String, String>> detalle;
        detalle = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  cliente,calle,numero,ciudad,provincia,descripcion FROM aux_pedido where idauxpedido = "+idpedido+" ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("cliente", cursor.getString(0));
                map.put("calle", cursor.getString(1));
                map.put("numero", cursor.getString(2));
                map.put("ciudad", cursor.getString(3));
                map.put("provincia", cursor.getString(4));
                map.put("descripcion", cursor.getString(5));
                // map.put("fechacr", cursor.getString(6));
                //  map.put("fechack", cursor.getString(7));

                detalle.add(map);

            }while (cursor.moveToNext());
        }
        database.close();
        return detalle;

    }





      ////////////////////***********OBTENER DISPOSITIVOS***********////////////

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getdisp(String idped) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
      //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";
        String selectQuery = "SELECT  codigoscan,nombre,descripcion FROM dispositivos where fkidauxpedido = "+idped+"";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("codigoscan", cursor.getString(0));
                map.put("nombre", cursor.getString(1));
                map.put("descripcion", cursor.getString(2));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }




    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void inserdips(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        values.put("codigoscan", queryValues.get("codigo"));
        values.put("nombre", queryValues.get("nombre"));
        values.put("descripcion", queryValues.get("descripcion"));
        values.put("latitud", queryValues.get("latitud"));
        values.put("longitud", queryValues.get("longitud"));
        values.put("horasca", queryValues.get("tiempo"));
        values.put("fkidauxpedido", queryValues.get("fkidpedido"));



        //values.put("udpateStatus", "no");
        database.insert("dispositivos", null, values);
        database.close();
    }




    ////////////////*********************ELIMINA DISPOSITIVO**************///////////////

    public void dipsup (String iddisp) {


        SQLiteDatabase database = this.getWritableDatabase();

        database.delete("dispositivos", "codigoscan='"+iddisp+"'", null);

    }



////////////////////***********OBTENER DISPOSITIVOS POR CODIGO***********////////////

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getdispcod(String code) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        String selectQuery = "SELECT  codigoscan,nombre,descripcion FROM dispositivos where codigoscan like '"+code+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("codigoscan", cursor.getString(0));
                map.put("nombre", cursor.getString(1));
                map.put("descripcion", cursor.getString(2));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


//////////////////////************************MODIFICAR DISPOSITIVOS*********/////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void updips(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("codigoscan", queryValues.get("codigo"));
        values.put("nombre", queryValues.get("nombre"));
        values.put("descripcion", queryValues.get("descripcion"));

        String id =  "'"+queryValues.get("codigo")+"'";

        database.update("dispositivos", values ,"codigoscan"+"="+id, null);
        database.close();
    }


////////////////*****************INSERTAR ARCHIVOS***********////////////////

    public void upfoto(Bitmap imagen,HashMap<String, String> queryValues) {
        {
            SQLiteDatabase database = this.getWritableDatabase();




            ContentValues values = new ContentValues();
            values.put("fkidauxpedido", queryValues.get("idpedido"));
            values.put("observaciones", queryValues.get("observaciones"));
            values.put("horafinalizado", queryValues.get("horafinal"));



            ByteArrayOutputStream out = new ByteArrayOutputStream();

            imagen.compress(Bitmap.CompressFormat.PNG, 100, out);
            //values.put("codigoscan", queryValues.get("codigo"));
            values.put("firma", out.toByteArray());
            //values.put("udpateStatus", "no");
            database.insert("remito", null,values );
            database.close();

        }

    }



    //////////////////////************************MODIFICAR DISPOSITIVOS*********/////////////////

    /**
     * Inserts User into SQLite DB
     * //@param queryValues
     */
    public void upload_aux(String idped) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));

        values.put("finalizado", "1" );

        database.update("aux_pedido", values ,"idauxpedido"+"="+idped, null);
        database.close();
    }


}
