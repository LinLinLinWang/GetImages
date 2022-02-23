package com.example.getimages;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class PhotoFolder {
   String folderId ;

    String folder ;

   long fileId ;

   String finaName ;

    String path ;

    int count ;//该文件夹下一共有多少张图片



    public PhotoFolder(String folderId, String folder,  long fileId , String finaName, String path,int count){
        this.folderId = folderId ;

        this.folder= folder;

        this.fileId =fileId;

        this.finaName= finaName;

        this.path =path;

        this.count =count;



    }
}
