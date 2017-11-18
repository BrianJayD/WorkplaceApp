package ca.uoit.csci4100u.workplace_app.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Company;

public class LocalDbHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Workplace";
    static final String TABLE_USERS = "Users";
    static final String TABLE_COMPANIES = "Companies";
    static final String TABLE_USER_COMPANY = "UserCompany";

    static final String CREATE_USERS_TABLE = "CREATE TABLE Users (\n" +
            "   userId VARCHAR(255) PRIMARY KEY,\n" +
            "   userName VARCHAR(255) NOT NULL\n" +
            ")\n";

    static final String CREATE_COMPANIES_TABLE = "CREATE TABLE Companies (\n" +
            "   companyId VARCHAR(255) PRIMARY KEY,\n" +
            "   companyName VARCHAR(255) NOT NULL\n" +
            ")\n";

    static final String CREATE_USER_COMPANY_TABLE = "CREATE TABLE UserCompany (\n" +
            "   userId VARCHAR(255) NOT NULL,\n" +
            "   companyId VARCHAR(255) NOT NULL,\n" +
            "   PRIMARY KEY (userId, companyId)\n" +
            ")\n";

    public LocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPANIES_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_COMPANY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersionNum, int newVersionNum) {
        /**
         * TODO: Fill this in
         */
    }

    public void createUser(String userId, String userName) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newUser = new ContentValues();
        newUser.put("userId", userId);
        newUser.put("userName", userName);
        database.insert(TABLE_USERS, null, newUser);
        database.close();
    }

    public boolean checkUserExists(String userId) {
        boolean userExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_USERS + " WHERE userId =?";
        Cursor cursor = database.rawQuery(query, new String[] {userId});
        if (cursor.moveToFirst()) {
            userExists = true;
        }
        cursor.close();
        database.close();
        return userExists;
    }

    public void createCompany(String companyId, String companyName) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newCompany = new ContentValues();
        newCompany.put("companyId", companyId);
        newCompany.put("companyName", companyName);
        database.insert(TABLE_COMPANIES, null, newCompany);
        database.close();
    }

    public boolean checkCompanyExists(String companyId) {
        boolean companyExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_COMPANIES + " WHERE companyId =?";
        Cursor cursor = database.rawQuery(query, new String[] {companyId});
        if (cursor.moveToFirst()) {
            companyExists = true;
        }
        cursor.close();
        database.close();
        return companyExists;
    }

    public void createUserCompany(String userId, String companyId) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newUserCompany = new ContentValues();
        newUserCompany.put("userId", userId);
        newUserCompany.put("companyId", companyId);
        database.insert(TABLE_USER_COMPANY, null, newUserCompany);
        database.close();
    }

    public boolean checkUserCompanyExists(String userId, String companyId) {
        boolean userCompanyExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_USER_COMPANY + " WHERE userId =? AND companyId =?";
        Cursor cursor = database.rawQuery(query, new String[] {userId, companyId});
        if (cursor.moveToFirst()) {
            userCompanyExists = true;
        }
        cursor.close();
        database.close();
        return userCompanyExists;
    }

    public List<Company> getCompanyListForCurrUser(String userId) {
        List<Company> companyList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String userCompanyQuery = "Select * from " + TABLE_USER_COMPANY + " WHERE userId =?";
        Cursor cursor = database.rawQuery(userCompanyQuery, new String[] {userId});
        List<String> companyIds = new ArrayList<>();
        cursor.moveToFirst();
        do {
            String companyId = cursor.getString(1);
            companyIds.add(companyId);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());

        String companyQuery = "Select * from " + TABLE_COMPANIES + " WHERE companyId =?";
        for (String companyId : companyIds) {
            cursor = database.rawQuery(companyQuery, new String[]{companyId});
            cursor.moveToFirst();
            do {
                String companyName = cursor.getString(1);
                Company newCompany = new Company(companyId, companyName);
                companyList.add(newCompany);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        database.close();
        return companyList;
    }
}
