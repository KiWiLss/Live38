package com.magicsoft.live38.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactProvider {
    private static final String TAG = ContactProvider.class.getSimpleName();
    private QueryCallback mQueryCallback;
    private List<ContactModel> mContactModels;
    private Context mContext;
    private boolean isDetailInfo;//是否查询详细信息：true查询详细信息,false不需要
    private boolean isMerge;//是否合并：true合并(一对多),false不需要(一对一)

    private String[] DETAIL_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,//id
            ContactsContract.Contacts.DISPLAY_NAME,//姓名
            ContactsContract.CommonDataKinds.Phone.NUMBER,//手机号
            ContactsContract.Data.MIMETYPE,//类型
            ContactsContract.CommonDataKinds.StructuredPostal.DATA,//通讯地址
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,//地址类型
            ContactsContract.CommonDataKinds.Note.NOTE,//备注
    };
    private String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,//id
            ContactsContract.Contacts.DISPLAY_NAME,//姓名
            ContactsContract.CommonDataKinds.Phone.NUMBER,//手机号
            ContactsContract.Data.MIMETYPE,//类型
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_NUMBER = 2;
    private static final int COLUMN_MIME_TYPE = 3;
    private static final int COLUMN_ADDRESS = 4;
    private static final int COLUMN_ADDRESS_TYPE = 5;
    private static final int COLUMN_NOTE = 6;

    public ContactProvider() {

    }

    public static ContactProvider getInstance() {
        return new ContactProvider();
    }
    /**
     * 查询系统通讯录数据
     * 注：需要权限：Manifest.permission.READ_CONTACTS
     *
     * @param context       Context
     * @param queryCallback 查询结果回调
     */
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    public void query(@NonNull Context context, QueryCallback queryCallback) {
        this.mContext = context;
        this.mQueryCallback = queryCallback;
        if (null != mQueryCallback) mQueryCallback.startQuery();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mContactModels = query(mContext);
                if (null != mQueryCallback)
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mQueryCallback.queryResult(mContactModels);
                        }
                    });
            }
        }).start();
    }
    /**
     * 查询系统通讯录数据
     * 注：需要权限：Manifest.permission.READ_CONTACTS
     *
     * @param context       Context
     * @param detailInfo    是否查询详细信息：true查询详细信息,false只查询姓名和电话号码
     * @param queryCallback 查询结果回调
     */
//    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    public void query(@NonNull Context context, boolean detailInfo, QueryCallback queryCallback) {
        this.mContext = context;
        this.mQueryCallback = queryCallback;
        this.isDetailInfo = detailInfo;
        if (null != mQueryCallback) mQueryCallback.startQuery();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mContactModels = query(mContext);
                //mContactModels = isMerge ? queryWithMerge(mContext, isDetailInfo) : query(mContext, isDetailInfo);
                if (null != mQueryCallback)
                  /*  AppManager.getInstance().getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            mQueryCallback.queryResult(mContactModels);
                        }
                    });*/
                  new Handler().post(new Runnable() {
                      @Override
                      public void run() {
                          mQueryCallback.queryResult(mContactModels);
                      }
                  });


            }
        }).start();
    }

    private List<ContactModel> query(@NonNull Context context) {
        long currentDate = System.currentTimeMillis();

        LongSparseArray<ContactModel> contactModelArray = new LongSparseArray<>();
        List<ContactModel> contactModels = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection;
        projection = isDetailInfo ? DETAIL_PROJECTION : PROJECTION;
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
        if (null == cursor) return contactModels;
        ContactModel contactModel;
        while (cursor.moveToNext()) {
            long id = cursor.getInt(COLUMN_ID);
            String name = cursor.getString(COLUMN_NAME);
            contactModel = contactModelArray.get(id);
            if (null == contactModel) {
                contactModel = new ContactModel(id, name);
                contactModelArray.put(id, contactModel);
                contactModels.add(contactModel);
            }
            switch (cursor.getString(COLUMN_MIME_TYPE)) {
                case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE://备注
                    contactModel.setNote(cursor.getString(COLUMN_NOTE));
                    break;
                case ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE://事件
                    queryEvent(cursor, contactModel);
                    break;
                case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE://昵称
                    String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    contactModel.setNickName(nickName);
                    break;
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE://email
                    String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    contactModel.setEmail(email);
                    break;
                case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE://组织
                    String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                    contactModel.setCompany(company);
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE://通讯地址
                    analyzeAddress(contactModel, cursor.getString(COLUMN_ADDRESS), cursor.getInt(COLUMN_ADDRESS_TYPE));
                    break;
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE://电话
                    contactModel.addPhoneNumber(cursor.getString(COLUMN_NUMBER));
                    break;
                default:
                    String data = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
            }
        }
        cursor.close();
        //LogUtils.i(TAG, "查询耗时：" + (System.currentTimeMillis() - currentDate));

//        removeUselessData(contactModels);
//        separateNumberData(contactModels);
        handle(contactModels);
        Collections.sort(contactModels);//按拼音排序

        return contactModels;
    }

    private void analyzeAddress(ContactModel contactModel, String address, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME://住宅通讯地址
                contactModel.setHomeAddress(address);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK://公司通讯地址
                contactModel.setWorkAddress(address);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER://其他通讯地址
                contactModel.addOtherAddress(address);
                break;
            default:
                //LogUtils.i(TAG, "default queryAddress: address=" + address + "--addressType=" + type);
                break;
        }
    }

    private void removeUselessData(List<ContactModel> contactModels) {
        //LogUtils.i(TAG, "移除垃圾数据(没有姓名或电话号码的数据)-移除前数量：" + contactModels.size());
        long startTime = System.currentTimeMillis();
        ContactModel contactModel;
        for (int i = 0; i < contactModels.size(); i++) {
            contactModel = contactModels.get(i);
            if (TextUtils.isEmpty(contactModel.getName())
                    || null == contactModel.getPhoneNumbers()
                    || contactModel.getPhoneNumbers().isEmpty()) {
                contactModels.remove(contactModels.get(i));
                i--;
            }
        }
        //LogUtils.i(TAG, "移除垃圾数据(没有姓名或电话号码的数据)-移除后数量：" + contactModels.size() + " 耗时:" + (System.currentTimeMillis() - startTime));
    }

    /**
     * 将联系人数据中电话号码一对多转换为一对一
     *
     * @param contactModels 转换前数据
     */
    private void separateNumberData(List<ContactModel> contactModels) {
        //LogUtils.i(TAG, "将联系人数据中电话号码一对多转换为一对一，转换前数量：" + contactModels.size());
        long startTime = System.currentTimeMillis();
        ContactModel contactModel;
        List<String> numbers;
        List<ContactModel> tempContactModels = new ArrayList<>();
        for (int i = 0; i < contactModels.size(); i++) {
            contactModel = contactModels.get(i);
            numbers = contactModel.getPhoneNumbers();
            if (null == numbers || numbers.size() == 1) continue;
            for (int j = 0; j < numbers.size(); j++) {
                tempContactModels.add(contactModel.copy(j));
            }
            contactModels.remove(i);
            i--;
        }
        if (!tempContactModels.isEmpty()) contactModels.addAll(tempContactModels);
        //LogUtils.i(TAG, "将联系人数据中电话号码一对多转换为一对一，转换后数量：" + contactModels.size() + " 耗时：" + (System.currentTimeMillis() - startTime));
    }

    private void handle(List<ContactModel> contactModels) {
        //LogUtils.i(TAG, "联系人数据处理(移除没有姓名或电话号码的数据、将联系人数据中电话号码一对多转换为一对一)，处理前数量：" + contactModels.size());
        long startTime = System.currentTimeMillis();
        ContactModel contactModel;
        List<String> numbers;
        List<ContactModel> tempContactModels = new ArrayList<>();
        int totalSize = contactModels.size();
        int numberCount;
        for (int i = 0; i < totalSize; i++) {
            contactModel = contactModels.get(i);
            numbers = contactModel.getPhoneNumbers();
            if (TextUtils.isEmpty(contactModel.getName())
                    || null == numbers
                    || numbers.isEmpty()) {
                contactModels.remove(contactModels.get(i));
                i--;
                totalSize--;
                continue;
            }
            numberCount = numbers.size();
            if (numberCount == 1) continue;
            for (int j = 0; j < numbers.size(); j++) {
                tempContactModels.add(contactModel.copy(j));
            }
            contactModels.remove(i);
            i--;
            totalSize--;
        }
        if (!tempContactModels.isEmpty()) contactModels.addAll(tempContactModels);
        //LogUtils.i(TAG, "联系人数据处理(移除没有姓名或电话号码的数据、将联系人数据中电话号码一对多转换为一对一)，处理前数量：" + contactModels.size() + " 耗时：" + (System.currentTimeMillis() - startTime));
    }

    /**
     * 获取其他详细信息
     * 详细信息：https://www.cnblogs.com/zhujiabin/p/6645551.html
     * 通话记录： http://www.jb51.net/article/94309.htm
     *
     * @param contentResolver 解析器
     * @param contactId       联系人id
     * @param contactModel    联系人实体
     */
    private void otherInfo(ContentResolver contentResolver, String contactId, ContactModel contactModel) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI
                    , null
                    , ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);
            if (null == cursor) return;
            while (cursor.moveToNext()) {
                String mimetype = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面
                switch (mimetype) {
                    case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE://备注
                        String note = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        contactModel.setNote(note);
                        break;
                    case ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE://事件
                        queryEvent(cursor, contactModel);
                        break;
                    case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE://昵称
                        String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                        contactModel.setNickName(nickName);
                        break;
                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE://email
                        String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        contactModel.setEmail(email);
                        break;
                    case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                        String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                        contactModel.setCompany(company);
                        break;
                    case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE://通讯地址
                        queryAddress(cursor, contactModel);
                        break;
                    default:
                        String data = cursor.getString(cursor.getColumnIndex("data1"));
                        //LogUtils.i(TAG, "查询联系人信息mimetype：" + mimetype + "--data=" + data);
                        break;
                }
            }
            cursor.close();
        } catch (Exception e) {
           // LogUtils.i(TAG, "查询其他详细信息失败：" + contactModel.getName() + "\n" + e.toString());
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    private void queryAddress(Cursor cursor, ContactModel contactModel) {
        String address = cursor.getString(
                cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        if (TextUtils.isEmpty(address)) return;
        int addressType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
        switch (addressType) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME://住宅通讯地址
                contactModel.setHomeAddress(address);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK://公司通讯地址
                contactModel.setWorkAddress(address);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER://其他通讯地址
                contactModel.addOtherAddress(address);
                break;
            default:
                //LogUtils.i(TAG, "default queryAddress: address=" + address + "--addressType=" + addressType);
                break;
        }
    }

    private void queryEvent(Cursor cursor, ContactModel contactModel) {
        String date = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
        int eventType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));
        switch (eventType) {
            case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY://生日
                contactModel.setBirthday(date);
                break;
            case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY://周年纪念日
                contactModel.addAnniversary(date);
                break;
            default:
                //LogUtils.i(TAG, "查询联系人信息：date=" + date + "--eventType=" + eventType);
                break;
        }
    }

    /**
     * 查询通话记录
     * 注：需要权限：Manifest.permission.READ_CALL_LOG
     *
     * @param context Context
     */
    @SuppressLint({"MissingPermission", "SupportAnnotationUsage"})
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    private void queryCallLog(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (null == cursor) return;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String date = DateUtils.millisecond2Str(dateLong);
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String strCallType = CallLog.Calls.INCOMING_TYPE == callType ? "打入"
                        : CallLog.Calls.OUTGOING_TYPE == callType ? "打出"
                        : CallLog.Calls.MISSED_TYPE == callType ? "未接"
                        : "";

                //LogUtils.i(TAG, "name：" + name + "--number：" + number + "--date：" + date + "--duration：" + duration + "--strCallType：" + strCallType);
            }
        } catch (Exception e) {
            //LogUtils.i(TAG, "查询通话记录失败：" + e.toString());
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    public class ContactModel implements Comparable<ContactModel> {
        private long id;
        private String name;
        private List<String> phoneNumbers;
        private String pinyin;

        private String note;//备注
        private String email;//邮箱
        private String birthday;//生日
        private List<String> anniversarys;//周年纪念日
        private String nickName;//昵称
        private String photo;//照片
        private String company;//公司
        private String workAddress;
        private String homeAddress;
        private List<String> otherAddresss;

        public ContactModel(String name) {
            this.name = name;
            pinyin = new PinYinUtils().character2PinYin(name);
            phoneNumbers = new ArrayList<>();
        }

        public ContactModel(long id, String name) {
            this.id = id;
            this.name = name;
            pinyin = new PinYinUtils().character2PinYin(name);
            phoneNumbers = new ArrayList<>();
        }

        public ContactModel addPhoneNumber(String phoneNumber) {
            if (!android.text.TextUtils.isEmpty(phoneNumber)) phoneNumbers.add(phoneNumber);
            return this;
        }

        public ContactModel setPhoneNumber(String phoneNumber) {
            phoneNumbers.clear();
            addPhoneNumber(phoneNumber);
            return this;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getPhoneNumbers() {
            return phoneNumbers;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public List<String> getAnniversary() {
            return anniversarys;
        }

        public void addAnniversary(String anniversary) {
            if (null == anniversarys) anniversarys = new ArrayList<>();
            anniversarys.add(anniversary);
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getWorkAddress() {
            return workAddress;
        }

        public void setWorkAddress(String workAddress) {
            this.workAddress = workAddress;
        }

        public String getHomeAddress() {
            return homeAddress;
        }

        public void setHomeAddress(String homeAddress) {
            this.homeAddress = homeAddress;
        }

        public List<String> getOtherAddress() {
            return otherAddresss;
        }

        public void addOtherAddress(String otherAddress) {
            if (null == otherAddresss) otherAddresss = new ArrayList<>();
            otherAddresss.add(otherAddress);
        }

        public String getPinyin() {
            return pinyin;
        }

        public ContactModel copy(int phoneNumberIndex) {
            ContactModel contactModel = new ContactModel(this.id, this.name);
            contactModel.pinyin = this.pinyin;
            contactModel.note = this.note;
            contactModel.homeAddress = this.homeAddress;
            contactModel.workAddress = this.workAddress;
            contactModel.otherAddresss = this.otherAddresss;
            contactModel.company = this.company;
            contactModel.email = this.email;
            contactModel.nickName = this.nickName;
            contactModel.birthday = this.birthday;
            contactModel.photo = this.photo;
            contactModel.anniversarys = this.anniversarys;
            contactModel.addPhoneNumber(this.phoneNumbers.get(phoneNumberIndex));
            return contactModel;
        }

        @Override
        public String toString() {
            return "ContactModel{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", phoneNumbers=" + phoneNumbers +
                    ", pinyin='" + pinyin + '\'' +
                    ", note='" + note + '\'' +
                    ", email='" + email + '\'' +
                    ", birthday='" + birthday + '\'' +
                    ", anniversarys=" + anniversarys +
                    ", nickName='" + nickName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", company='" + company + '\'' +
                    ", workAddress='" + workAddress + '\'' +
                    ", homeAddress='" + homeAddress + '\'' +
                    ", otherAddresss=" + otherAddresss +
                    '}' + "\n";
        }

        @Override
        public int compareTo(@NonNull ContactModel o) {
            return pinyin.compareToIgnoreCase(o.getPinyin());
        }
    }

    /**
     * 系统联系人数据查询回调
     */
    public interface QueryCallback {
        /**
         * 开始查询
         */
        void startQuery();

        /**
         * 查询结果(已排序)
         *
         * @param contactModels 系统联系人数据
         */
        void queryResult(List<ContactModel> contactModels);
    }

}
