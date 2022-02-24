package com.example.getimages;

public class GalleryEntity {
   private int id ;
    private  String image_path ;
    private int bucket_id ;
    private String bucket_name ;
    private  int count ;

    public GalleryEntity(int id, String image_path, int bucket_id, String bucket_name, int count) {
        this.id = id;
        this.image_path = image_path;
        this.bucket_id =bucket_id;
        this.bucket_name=bucket_name;
        this.count = count;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String image_path) {
        this.image_path = image_path;
    }

    public void setGallery_id(int bucket_id) {
        this.bucket_id =bucket_id;
    }

    public void setGallery_name(String bucket_name) {
        this.bucket_name=bucket_name;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getId() {
        return id;
    }

    public String getPath() {
        return image_path;
    }

    public int getGallery_id() {
       return  bucket_id;
    }

    public String   getGallery_name() {
        return  bucket_name;
    }

    public int getCount() {
        return count;
    }

}
