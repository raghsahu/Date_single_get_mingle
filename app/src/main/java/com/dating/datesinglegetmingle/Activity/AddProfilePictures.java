package com.dating.datesinglegetmingle.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Constant.Constants;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.cropper.CropImage;
import com.dating.datesinglegetmingle.cropper.CropImageView;
import com.dating.datesinglegetmingle.cropper.ImagePickerPack.ImagePicker;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddProfilePictures extends AppCompatActivity {

    private RelativeLayout profile_pic_back_lay;
    private String ldata = "", user_id = "", int_ids = "";
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private RoundedImageView user_image1, user_image2, user_image3, user_image4,
            user_image5, user_image6;
    private String path1 = "", path2 = "", path3 = "", path4 = "", path5 = "", path6 = "";
    private Button image_upload_btn;
    private MultipartBody.Part body1, body2, body3, body4, body5, body6;
    private String image1 = "", image2 = "", image3 = "", image4 = "", image5 = "", image6 = "";

    private Bitmap bitmap;
    File imgFile;
    private Session session;
    int compressionRatio = 90;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_pictures);
        progressDialog = new ProgressDialog(AddProfilePictures.this);

        session = new Session(this);
        user_id = session.getUser().user_id;
        CheckInternet();
        //GetLoginId();
        Init();
        getIntentvalue();
        Onclick();

    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void Init() {

        profile_pic_back_lay = findViewById(R.id.profile_pic_back_lay);
        user_image1 = findViewById(R.id.user_image1);
        user_image2 = findViewById(R.id.user_image2);
        user_image3 = findViewById(R.id.user_image3);
        user_image4 = findViewById(R.id.user_image4);
        user_image5 = findViewById(R.id.user_image5);
        user_image6 = findViewById(R.id.user_image6);
        image_upload_btn = findViewById(R.id.image_upload_btn);

    }

    private void getIntentvalue() {

        Intent intent = getIntent();

        if (intent != null) {

            image1 = intent.getStringExtra("image1");
            image2 = intent.getStringExtra("image2");
            image3 = intent.getStringExtra("image3");
            image4 = intent.getStringExtra("image4");
            image5 = intent.getStringExtra("image5");
            image6 = intent.getStringExtra("image6");

            Glide.with(getApplicationContext()).load(Config.Image_Url + image1).error(R.drawable.add_image_bg).into(user_image1);
            Glide.with(getApplicationContext()).load(Config.Image_Url + image2).error(R.drawable.add_image_bg).into(user_image2);
            Glide.with(getApplicationContext()).load(Config.Image_Url + image3).error(R.drawable.add_image_bg).into(user_image3);
            Glide.with(getApplicationContext()).load(Config.Image_Url + image4).error(R.drawable.add_image_bg).into(user_image4);
            Glide.with(getApplicationContext()).load(Config.Image_Url + image5).error(R.drawable.add_image_bg).into(user_image5);
            Glide.with(getApplicationContext()).load(Config.Image_Url + image6).error(R.drawable.add_image_bg).into(user_image6);

        }
    }

    private void Onclick() {

        profile_pic_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        user_image1.setOnClickListener(view -> {

            final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
            dialog.setOnPickCancel(new IPickCancel() {
                @Override
                public void onCancelClick() {
                    dialog.dismiss();
                }
            }).setOnPickResult(new IPickResult() {
                @Override
                public void onPickResult(PickResult r) {

                    if (r.getError() == null) {
                        path1 = r.getPath();

                        Log.e("path1 = ", path1);
                        Glide.with(getApplicationContext()).load(path1).error(R.drawable.add_image_bg).into(user_image1);
                    } else {
                    }
                }

            }).show(AddProfilePictures.this);
        });

        user_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Utils.hideSoftKeyboard(view);
                getPermissionAndPicImage();*/


                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {
                            path2 = r.getPath();
                            Glide.with(getApplicationContext()).load(path2).error(R.drawable.add_image_bg).into(user_image2);
                        } else {
                            //Handle possible errors
                        }
                    }

                }).show(AddProfilePictures.this);
            }

        });

        user_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {
                            path3 = r.getPath();
                            Glide.with(getApplicationContext()).load(path3).error(R.drawable.add_image_bg).into(user_image3);
                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }).show(AddProfilePictures.this);
            }
        });

        user_image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {
                            path4 = r.getPath();
                            Glide.with(getApplicationContext()).load(path4).error(R.drawable.add_image_bg).into(user_image4);
                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }).show(AddProfilePictures.this);

            }
        });

        user_image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {
                            path5 = r.getPath();
                            Glide.with(getApplicationContext()).load(path5).error(R.drawable.add_image_bg).into(user_image5);
                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }).show(AddProfilePictures.this);

            }
        });

        user_image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {
                            path6 = r.getPath();
                            Glide.with(getApplicationContext()).load(path6).error(R.drawable.add_image_bg).into(user_image6);
                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }).show(AddProfilePictures.this);

            }
        });

        image_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("path1", path1);

                if (path1.equalsIgnoreCase("") && image1.equalsIgnoreCase("")) {
                    Toast.makeText(AddProfilePictures.this, "Please select first image", Toast.LENGTH_SHORT).show();
                } else {

                    if (path1 != null && path1.equalsIgnoreCase("")) {
                        body1 = MultipartBody.Part.createFormData("image1", image1);
                        Log.e("image1 ka path", image1);
                    } else {
                        Log.e("path1 ka path", path1);
                        // String path11 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path1, "profile_image");
                        File file = new File(path1);

//                        try {
//                            Bitmap compressedImgBitmap = new Compressor(AddProfilePictures.this)
//                                    .compressToBitmap(file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }


                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body1 = MultipartBody.Part.createFormData("image1", file.getName(), requestFile);
                    }

                    if (path2 != null && path2.equalsIgnoreCase("")) {
                        body2 = MultipartBody.Part.createFormData("image2", image2);
                    } else {

                        //path2 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path2, "profile_image2");
                        File file = new File(path2);

                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }


                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body2 = MultipartBody.Part.createFormData("image2", file.getName(), requestFile);
                    }

                    if (path3 != null && path3.equalsIgnoreCase("")) {
                        body3 = MultipartBody.Part.createFormData("image3", image3);
                    } else {
                        // path3 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path3, "profile_image3");
                        File file = new File(path3);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }


                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body3 = MultipartBody.Part.createFormData("image3", file.getName(), requestFile);
                    }

                    if (path4 != null && path4.equalsIgnoreCase("")) {
                        body4 = MultipartBody.Part.createFormData("image4", image4);
                    } else {
                        // path4 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path4, "profile_image4");
                        File file = new File(path4);

                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }

                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body4 = MultipartBody.Part.createFormData("image4", file.getName(), requestFile);
                    }

                    if (path5 != null && path5.equalsIgnoreCase("")) {
                        body5 = MultipartBody.Part.createFormData("image5", image5);
                    } else {
                        // path5 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path5, "profile_image5");
                        File file = new File(path5);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }


                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body5 = MultipartBody.Part.createFormData("image5", file.getName(), requestFile);
                    }

                    if (path6 != null && path6.equalsIgnoreCase("")) {
                        body6 = MultipartBody.Part.createFormData("image6", image6);
                    } else {
                        // path6 = resizeAndCompressImageBeforeSend(AddProfilePictures.this, path6, "profile_image6");
                        File file = new File(path6);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
                            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));
                        }
                        catch (Throwable t) {
                            Log.e("ERROR", "Error compressing file." + t.toString ());
                            t.printStackTrace ();
                        }


                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body6 = MultipartBody.Part.createFormData("image6", file.getName(), requestFile);
                    }


                    if (isInternetPresent) {
                        UploadProfilePictures();
                    } else {
                        AlertConnection.showAlertDialog(AddProfilePictures.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }
                }
            }
        });
    }


    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (AddProfilePictures.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(AddProfilePictures.this);
            }
        } else {
            ImagePicker.pickImage(AddProfilePictures.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.SELECT_FILE);
                } else {
                    Toast.makeText(AddProfilePictures.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constants.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constants.REQUEST_CAMERA);
                } else {
                    Toast.makeText(AddProfilePictures.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constants.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(AddProfilePictures.this);
                } else {
                    Toast.makeText(AddProfilePictures.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    /*................................onActivityResult...................................*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 234) {    // Image Picker
                Uri imageUri = ImagePicker.getImageURIFromResult(AddProfilePictures.this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setRequestedSize(600, 600, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                            //.setAspectRatio(3, 2)
                            .setAspectRatio(4, 3)
                            .start(this);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {   // Image Cropper
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                        imgFile = bitmapToFile(bitmap);
                        Log.e("FIle Img = ", "" + imgFile);
                        Log.e("uri = ", "" + result.getUri());
                        Log.e("bitmap = ", "" + bitmap);

                        //ToastClass.showToast(this, "" + img);
                    }
                    user_image2.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //convert bitmap to file
    private File bitmapToFile(Bitmap bitmap) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //------------------------------------ UploadProfilePictures -----------------------------------

    private void UploadProfilePictures() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface()
                .Images_Updated(user_id, body1, body2, body3, body4, body5, body6);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("upload _pro response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Toast.makeText(AddProfilePictures.this, "Profile Upload Successfully", Toast.LENGTH_SHORT).show();

                            //json obj parsing
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<UserInfoData>>() {
                            }.getType();

                            //json array
                            //List<UserInfoData> data = gson.fromJson(jsonObject.getString("data"), listType);

                            //json object
                            UserInfoData user = gson.fromJson(object.getJSONObject("data").toString(), UserInfoData.class);
                            session.createSession(user);
                            Intent i = new Intent(AddProfilePictures.this, Account.class);
                            startActivity(i);
                            finish();
                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("error", "some error");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 512 * 512; // max final file size in kilobytes

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength / 1024 + " kb");
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap", "cacheDir: " + context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag, "image height: " + height + "---image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag, "inSampleSize: " + inSampleSize);
        return inSampleSize;
    }


}
