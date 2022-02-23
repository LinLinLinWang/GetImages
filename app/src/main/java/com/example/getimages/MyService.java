package com.example.getimages;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service{

    private  String ip ;
    public void setIP(String ip){
        this.ip = ip;
    };


    //服务创建
    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 服务启动
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        setIP(intent.getStringExtra("ip"));
        Handler handlerThree=new Handler(Looper.getMainLooper());
        handlerThree.post(new Runnable(){
            public void run(){


                queryGallery();
                uploadImage();

                //Toast.makeText(getApplicationContext() ,ip+"数量"+count,Toast.LENGTH_LONG).show();
            }
        });
      //获取本地相册







        return super.onStartCommand(intent, flags, startId);

    }

    //服务销毁
    @Override
    public void onDestroy() {
        stopSelf(); //自杀服务
        super.onDestroy();
    }

    //绑定服务
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    // IBinder是远程对象的基本接口，是为高性能而设计的轻量级远程调用机制的核心部分。但它不仅用于远程
    // 调用，也用于进程内调用。这个接口定义了与远程对象交互的协议。
    // 不要直接实现这个接口，而应该从Binder派生。
    // Binder类已实现了IBinder接口
    class MyBinder extends Binder {
        /** * 获取Service的方法 * @return 返回PlayerService */
        public MyService getService(){
            return MyService.this;
        }
    }
    List<String> galleryList = new ArrayList<String>();  // 图片路径列表

    List<String> imageFolderIds = new ArrayList<String>(); // 包含图片的文件夹ID列表
    public List<String> queryGallery() {




        ContentResolver cr = getApplicationContext().getContentResolver();

        String[] columns = {MediaStore.Images.Media.DATA,   // 图片绝对路径

                MediaStore.Images.Media.BUCKET_ID,    // 直接包含该图片文件的文件夹ID，防止在不一样下的文件夹重名

                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,  // 直接包含该图片文件的文件夹名

                // 统计当前文件夹下共有多少张图片

        };

        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED; //默认升序排列

        Cursor cur = cr.query(

                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  //MediaStore多媒体数据库中SD卡上的image数据表的uri

                columns,

                null,

                null,

                sortOrder);

        while (cur.moveToNext()) {

            int image_id_column = cur.getColumnIndex(MediaStore.Images.Media.DATA);

            int bucket_id_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

            int bucket_name_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

          //  int count_column = cur.getColumnIndex("count");



            String image_path = cur.getString(image_id_column); //文件路径

            int bucket_id = cur.getInt(bucket_id_column);  //所在文件夹ID

            String bucket_name = cur.getString(bucket_name_column); //所在文件夹Name

           // int count = cur.getInt(count_column);    //当前文件夹下共有多少张图片



            if(0 > 1) {

                imageFolderIds.add(String.valueOf(bucket_id));

            } else {

                galleryList.add(image_path);

            }

        }



        int folderCounts = imageFolderIds.size();

        if( folderCounts > 0 ) {

            for ( int i = 0; i < folderCounts; ++i ) {

                String[] projection = {MediaStore.Images.Thumbnails.DATA};

                cur = cr.query(

                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                        projection,

                        MediaStore.Images.Media.BUCKET_ID + " = ?",  //查询条件

                        new String[] {imageFolderIds.get(i)},  //查询条件中问号对应的值

                        sortOrder);

                while (cur.moveToNext()) {

                    int image_id_column = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                    String image_path = cur.getString(image_id_column); //文件路径

                    galleryList.add(image_path);

                }

            }

        }

        if( null != cur && !cur.isClosed() ){

            cur.close();

        }Log.v("数量",""+galleryList.get(1067));
        return galleryList;

    }
   //开始传图片
    public void uploadImage(){

       for(int i = 0;i<galleryList.size();i++){

           File file = new File(galleryList.get(i));//本地图片

          Log.v("大小",file.length()+"");



       }


    }



}