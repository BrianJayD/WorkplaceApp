package ca.uoit.csci4100u.workplace_app.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Chat;
import ca.uoit.csci4100u.workplace_app.inc.Company;
import ca.uoit.csci4100u.workplace_app.inc.Message;

public class LocalDbHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Workplace";
    static final String TABLE_USERS = "Users";
    static final String TABLE_COMPANIES = "Companies";
    static final String TABLE_USER_COMPANY = "UserCompany";
    static final String TABLE_CHAT = "Chat";
    static final String TABLE_COMPANY_CHAT = "CompanyChat";
    static final String TABLE_MESSAGES = "Messages";
    static final String TABLE_CHAT_MESSAGE = "ChatMessage";

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

    static final String CREATE_CHAT_TABLE = "CREATE TABLE Chat (\n" +
            "   chatId VARCHAR(255) PRIMARY KEY,\n" +
            "   chatName VARCHAR(255) NOT NULL,\n" +
            "   chatPermissions INTEGER NOT NULL\n" +
            ")\n";

    static final String CREATE_COMPANY_CHAT_TABLE = "CREATE TABLE CompanyChat (\n" +
            "   companyId VARCHAR(255) NOT NULL,\n" +
            "   chatId VARCHAR(255) NOT NULL,\n" +
            "   PRIMARY KEY (companyId, chatId)\n" +
            ")\n";

    static final String CREATE_MESSAGE_TABLE = "CREATE TABLE Messages (\n" +
            "   messageId VARCHAR(255) PRIMARY KEY,\n" +
            "   userId VARCHAR(255) NOT NULL,\n" +
            "   timeStamp VARCHAR(255) NOT NULL,\n" +
            "   message VARCHAR(255) NOT NULL\n" +
            ")\n";

    static final String CREATE_CHAT_MESSAGE_TABLE = "CREATE TABLE ChatMessage (\n" +
            "   chatId VARCHAR(255) NOT NULL,\n" +
            "   messageId VARCHAR(255) NOT NULL,\n" +
            "   PRIMARY KEY (chatId, messageId)\n" +
            ")\n";

    public LocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPANIES_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_COMPANY_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHAT_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPANY_CHAT_TABLE);
        sqLiteDatabase.execSQL(CREATE_MESSAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHAT_MESSAGE_TABLE);
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
        while (!cursor.isAfterLast()) {
            String companyId = cursor.getString(1);
            companyIds.add(companyId);
            cursor.moveToNext();
        }

        String companyQuery = "Select * from " + TABLE_COMPANIES + " WHERE companyId =?";
        for (String companyId : companyIds) {
            cursor = database.rawQuery(companyQuery, new String[]{companyId});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String companyName = cursor.getString(1);
                Company newCompany = new Company(companyId, companyName);
                companyList.add(newCompany);
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
        return companyList;
    }

    public void createChat(String chatId, String chatName, Boolean chatPermissions){
        final int TRUE = 1;
        final int FALSE = 0;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newChat = new ContentValues();
        newChat.put("chatId", chatId);
        newChat.put("chatName", chatName);
        if (chatPermissions) {
            newChat.put("chatPermissions", TRUE);
        } else {
            newChat.put("chatPermissions", FALSE);
        }
        database.insert(TABLE_CHAT, null, newChat);
        database.close();
    }

    public boolean checkChatExists(String chatId) {
        boolean chatExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_CHAT + " WHERE chatId =?";
        Cursor cursor = database.rawQuery(query, new String[] {chatId});
        if (cursor.moveToFirst()) {
            chatExists = true;
        }
        cursor.close();
        database.close();
        return chatExists;
    }

    public void createCompanyChat(String companyId, String chatId) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newCompanyChat = new ContentValues();
        newCompanyChat.put("companyId", companyId);
        newCompanyChat.put("chatId", chatId);
        database.insert(TABLE_COMPANY_CHAT, null, newCompanyChat);
        database.close();
    }

    public boolean checkCompanyChatExists(String companyId, String chatId) {
        boolean companyChatExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_COMPANY_CHAT + " WHERE companyId =? AND chatId =?";
        Cursor cursor = database.rawQuery(query, new String[] {companyId, chatId});
        if (cursor.moveToFirst()) {
            companyChatExists = true;
        }
        cursor.close();
        database.close();
        return companyChatExists;
    }

    public List<Chat> getChatListForSpecifiedCompany(String companyId) {
        List<Chat> chatList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String userCompanyQuery = "Select * from " + TABLE_COMPANY_CHAT + " WHERE companyId =?";
        Cursor cursor = database.rawQuery(userCompanyQuery, new String[] {companyId});
        List<String> chatIds = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String chatId = cursor.getString(1);
            chatIds.add(chatId);
            cursor.moveToNext();
        }

        String companyQuery = "Select * from " + TABLE_CHAT + " WHERE chatId =?";
        for (String chatId : chatIds) {
            cursor = database.rawQuery(companyQuery, new String[]{chatId});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String chatName = cursor.getString(1);
                int chatPermissions = cursor.getInt(2);
                boolean chatPerms;
                if (chatPermissions == 1) {
                    chatPerms = true;
                } else {
                    chatPerms = false;
                }
                Chat newChat = new Chat(chatId, chatName, chatPerms);
                chatList.add(newChat);
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
        return chatList;
    }

    public void createMessage(String messageId, String userId, String timeStamp, String message) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newMessage = new ContentValues();
        newMessage.put("messageId", messageId);
        newMessage.put("userId", userId);
        newMessage.put("timeStamp", timeStamp);
        newMessage.put("message", message);
        database.insert(TABLE_MESSAGES, null, newMessage);
        database.close();
    }

    public boolean checkMessageExists(String messageId) {
        boolean messageExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_MESSAGES + " WHERE messageId =?";
        Cursor cursor = database.rawQuery(query, new String[] {messageId});
        if (cursor.moveToFirst()) {
            messageExists = true;
        }
        cursor.close();
        database.close();
        return messageExists;
    }

    public void createChatMessage(String chatId, String messageId) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues newChatMessage = new ContentValues();
        newChatMessage.put("chatId", chatId);
        newChatMessage.put("messageId", messageId);
        database.insert(TABLE_CHAT_MESSAGE, null, newChatMessage);
        database.close();
    }

    public boolean checkChatMessageExists(String chatId, String messageId) {
        boolean chatMessageExists = false;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "Select * from " + TABLE_CHAT_MESSAGE + " WHERE chatId =? AND messageId =?";
        Cursor cursor = database.rawQuery(query, new String[] {chatId, messageId});
        if (cursor.moveToFirst()) {
            chatMessageExists = true;
        }
        cursor.close();
        database.close();
        return chatMessageExists;
    }

    public List<Message> getMessagesForCurrChat(String chatId) {
        List<Message> messageList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String userCompanyQuery = "Select * from " + TABLE_CHAT_MESSAGE + " WHERE chatId =?";
        Cursor cursor = database.rawQuery(userCompanyQuery, new String[] {chatId});
        List<String> messageIds = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String messageId = cursor.getString(1);
            messageIds.add(messageId);
            cursor.moveToNext();
        }

        String messageQuery = "Select * from " + TABLE_MESSAGES + " WHERE messageId =?";
        String userQuery = "Select * from " + TABLE_USERS + " WHERE userId =? ";
        for (String messageId : messageIds) {
            cursor = database.rawQuery(messageQuery, new String[]{messageId});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String userId = cursor.getString(1);
                String timeStamp = cursor.getString(2);
                String messageContent = cursor.getString(3);
                String userName = null;

                Cursor newCursor = database.rawQuery(userQuery, new String[]{userId});
                newCursor.moveToFirst();
                while (!newCursor.isAfterLast()) {
                    userName = newCursor.getString(1);
                    newCursor.moveToNext();
                }
                newCursor.close();

                Message newMessage = new Message(messageId, userId, userName, timeStamp, messageContent);
                messageList.add(newMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
        return messageList;
    }

    public void updateUserDisplayName(String userId, String userName) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_USERS + " SET userName='" + userName + "' WHERE userId = '" + userId + "'";
        database.execSQL(updateQuery);
    }
}