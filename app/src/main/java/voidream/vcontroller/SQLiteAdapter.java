package voidream.vcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Ryvaldie on 21/10/15.
 */

public class SQLiteAdapter extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String database_name = "VcontrollerDB";
    private static final int database_version = 3;//naikin setiap ada perubahan

    public SQLiteAdapter(Context context) {
        super(context, database_name, null, database_version);
    }

    private static final String timestamp = "timestamp";

    private static final String key_id = "id", tabel_controller = "controller"
            , tabel_timer = "timer", tabel_log = "log", tabel_setting_mqtt = "mqtt_settings"
            , tabel_setting_tcp = "tcp_settings", tabel_io_command = "io_command";

    private static final String nama = "nama", posisi = "posisi", power = "power"
            , timer = "timer", id_image = "id_image", status = "status", number = "number";
    private static final String create_tabel_controller =
            "create table " + tabel_controller + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + nama + " TEXT," + posisi + " TEXT,"
                    + power + " TEXT," + status + " TEXT," + timestamp + " TEXT," + id_image + " TEXT" + ")";

    private static final String create_tabel_log =
            "create table " + tabel_log + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + number + " TEXT," + nama + " TEXT," + posisi + " TEXT,"
                    + power + " TEXT," + status + " TEXT," + timestamp + " TEXT," + id_image + " TEXT" + ")";

    private static final String id_controller = "id_controller", action = "action", time = "time";
    private static final String create_tabel_timer =
            "create table " + tabel_timer + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + id_controller + " TEXT," + action + " TEXT," + time + " TEXT" + ")";

    private static final String broker_url = "broker_url", port = "port", username = "username"
            , password = "password", topic = "topic";
    private static final String create_tabel_setting_mqtt =
            "create table " + tabel_setting_mqtt + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + broker_url + " TEXT," + port + " TEXT," + username + " TEXT,"
                    + password + " TEXT,"+ topic + " TEXT" + ")";

    private static final String ip_or_domain = "ip_domain";
    private static final String create_tabel_setting_tcp =
            "create table " + tabel_setting_tcp+ " (" + key_id + " INTEGER PRIMARY KEY,"
                    + ip_or_domain + " TEXT," + port + " TEXT" + ")";

    private static final String on_command = "on_command", off_command = "off_command"
            , timer_command = "timer_command";
    private static final String create_tabel_io_command =
            "create table " + tabel_io_command + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + id_controller + " TEXT," + on_command + " TEXT,"
                    + off_command + " TEXT," + timer_command + " TEXT" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_tabel_controller);
        db.execSQL(create_tabel_io_command);
        db.execSQL(create_tabel_log);
        db.execSQL(create_tabel_setting_mqtt);
        db.execSQL(create_tabel_setting_tcp);
        db.execSQL(create_tabel_timer);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tabel_controller);
        db.execSQL("DROP TABLE IF EXIST " + tabel_io_command);
        db.execSQL("DROP TABLE IF EXIST " + tabel_log);
        db.execSQL("DROP TABLE IF EXIST " + tabel_setting_tcp);
        db.execSQL("DROP TABLE IF EXIST " + tabel_timer);
        db.execSQL("DROP TABLE IF EXIST " + tabel_setting_mqtt);
        onCreate(db);
    }


    public void AddController(String nama_, String posisi_, String power_, int id_image_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(nama_!=null & posisi_!=null & power_!=null & id_image_!= 0){
            values.put(nama, nama_);
            values.put(posisi, posisi_);
            values.put(power, power_);
            values.put(status, "Waiting Response");
            values.put(id_image, id_image_);
            values.put(timestamp, "Waiting Response");
        }

        db.insert(tabel_controller, null, values);
        db.close();
    }

    public String[][] getController(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{nama, posisi, power, status, id_image, timestamp};
        Cursor cursor = db.query(tabel_controller, columns,
                null, null, null, null, null);

        int size = (int)getRowCount(tabel_controller);
        String[][] result = new String[6][size];

        int a = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0][a] = cursor.getString(0);//nama
            result[1][a] = cursor.getString(1);//posisi
            result[2][a] = cursor.getString(2);//power
            result[3][a] = cursor.getString(3);//status
            result[4][a] = cursor.getString(4);//id_image
            result[5][a] = cursor.getString(5);//timestamp
            a++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public boolean getValidController(String nama_){
        db = this.getReadableDatabase();

        String[] columns = new String[]{nama, posisi, power, status, id_image};
        Cursor cursor = db.query(tabel_controller, columns,
                nama + "='" + nama_ + "'", null, null, null, null);

        String check = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            check = cursor.getString(0);//nama
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        assert check != null;
        return StringUtils.isBlank(check);
    }

    public void addControllerStatus(String nama_, String status_, String timestamp_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(status_!=null){
            values.put(status, status_);
            values.put(timestamp, timestamp_);
        }

        db.update(tabel_controller, values, nama + "='" + nama_ + "'" , null);
        db.close();
    }

    public void deleteController(String nama_) {
        db = this.getWritableDatabase();
        db.delete(tabel_controller, nama + "='" + nama_ + "'", null);
        db.close();
    }

    public void addLog(int number_, String nama_, String posisi_, String power_, String status_, int id_image_,
                       String timestamp_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(nama_!=null & posisi_!=null & power_!=null & id_image_!= 0){
            values.put(number, number_);
            values.put(nama, nama_);
            values.put(posisi, posisi_);
            values.put(power, power_);
            values.put(status, status_);
            values.put(id_image, id_image_);
            values.put(timestamp, timestamp_);
        }

        db.insert(tabel_log, null, values);
        db.close();
    }

    public String[][] getLog(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{number, nama, posisi, power, status, id_image, timestamp};
        Cursor cursor = db.query(tabel_log, columns,
                null, null, null, null, null);

        int size = (int)getRowCount(tabel_log);
        String[][] result = new String[7][size];

        int a = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0][a] = cursor.getString(0);
            result[1][a] = cursor.getString(1);//nama
            result[2][a] = cursor.getString(2);//posisi
            result[3][a] = cursor.getString(3);//power
            result[4][a] = cursor.getString(4);//status
            result[5][a] = cursor.getString(5);//id_image
            result[6][a] = cursor.getString(6);//timestamp
            a++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteLog(){
        db = this.getWritableDatabase();
        db.delete(tabel_log, null, null);
        db.close();
    }

    public void addMqttSetting(String broker_url_, String port_, String username_, String password_
            , String topic_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (broker_url_ != null & port_ != null){
            values.put(broker_url, broker_url_);
            values.put(port, port_);
            values.put(username, username_);
            values.put(password, password_);
            values.put(topic, topic_);
        }

        db.insert(tabel_setting_mqtt, null, values);
        db.close();
    }

    public String[] getMqttSetting(){
        db = this.getReadableDatabase();
        String[] columns = new String[]{broker_url, port, username, password, topic};
        Cursor cursor = db.query(tabel_setting_mqtt, columns, null
                , null, null, null, null);

        int size = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            for (int a=0;a<5;a++) {
                if (!StringUtils.isBlank(cursor.getString(a))) {
                    size++;
                }
            }
            cursor.moveToNext();
        }
        String[] result = new String[size];
        if (size < 4) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
                result[2] = cursor.getString(4);
                cursor.moveToNext();
            }
        }else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
                result[2] = cursor.getString(4);
                result[3] = cursor.getString(2);
                result[4] = cursor.getString(3);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteMqttSetting(){
        db = this.getWritableDatabase();
        db.delete(tabel_setting_mqtt, null, null);
        db.close();
    }

    public void addTcpSetting(String ip, String port_tcp){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (ip != null & port_tcp != null){
            values.put(ip_or_domain, ip);
            values.put(port, port_tcp);
        }
        db.insert(tabel_setting_tcp, null, values);
        db.close();
    }

    public String[] getTcpSetting(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{ip_or_domain, port};
        Cursor cursor = db.query(tabel_setting_tcp, columns, null,
                null, null, null, null);
        String[] result = new String[3];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0] = cursor.getString(0);
            result[1] = cursor.getString(1);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteTcpSetting(){
        db = this.getWritableDatabase();
        db.delete(tabel_setting_tcp, null, null);
        db.close();
    }

    public void addIoCommand(String id_controller_, String on_command_, String off_command_,
                             String timer_command_){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(id_controller, id_controller_);
        values.put(on_command, on_command_);
        values.put(off_command, off_command_);
        values.put(timer_command, timer_command_);

        db.insert(tabel_io_command, null, values);
        db.close();
    }

    public void editIoCommand(String id_controller_, String on_command_, String off_command_,
                              String timer_command_){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!StringUtils.isBlank(id_controller_) & !StringUtils.isBlank(on_command_)
                & !StringUtils.isBlank(off_command_) & !StringUtils.isBlank(timer_command_)){
            values.put(id_controller, id_controller_);
            values.put(on_command, on_command_);
            values.put(off_command, off_command_);
            values.put(timer_command, timer_command_);
        }

        db.insert(tabel_io_command, id_controller + "='" + id_controller_ + "'", values);
        db.close();
    }

    public String[][] getIoCommand(){
        db = this.getReadableDatabase();
        String[] columns = new String[]{id_controller, on_command, off_command, timer_command};
        Cursor cursor = db.query(tabel_io_command, columns, null, null, null, null, null);
        String[][] result = new String[4][(int)getRowCount(tabel_io_command)];

        int a = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0][a] = cursor.getString(0);
            result[1][a] = cursor.getString(1);
            result[2][a] = cursor.getString(2);
            result[3][a] = cursor.getString(3);
            a++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteAll(){
        db = this.getWritableDatabase();
        db.delete(tabel_controller, null, null);
        db.delete(tabel_io_command, null, null);
        db.delete(tabel_log, null, null);
        db.delete(tabel_setting_mqtt, null, null);
        db.delete(tabel_setting_tcp, null, null);
        db.delete(tabel_timer, null, null);
        db.close();
    }

    public long getRowCount(String tabel) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, tabel);
    }

}