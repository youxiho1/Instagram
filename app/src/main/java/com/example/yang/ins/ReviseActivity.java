package com.example.yang.ins;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.lljjcoder.style.citypickerview.CityPickerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citypickerview.CityPickerView;

/*import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



import static android.os.Build.TYPE;*/

public class ReviseActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{

    private CircleImageView iv_head;
    private Button  btn_head;
    private TextView tv_birth, tv_location, tv_gender;
    private ImageButton ib_back, ib_finish;
    private BGAPhotoHelper bgaPhotoHelper;
    CityPickerView mPicker = new CityPickerView();
    private static final int REQUEST_CODE_PERMISSION_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CROP = 3;

    /*private String imagepath = null;

    public static final String TAG = "ReviseActivity";
    CityPickerView mPicker = new CityPickerView();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "InsTakePhoto");
        bgaPhotoHelper = new BGAPhotoHelper(takePhotoDir);
        mPicker.init(this);
        btn_head = (Button) findViewById(R.id.revise_image);
        iv_head = (CircleImageView) findViewById(R.id.head_revise);
        tv_birth = (TextView) findViewById(R.id.revise_birth);
        tv_gender = (TextView) findViewById(R.id.revise_gender);
        tv_location = (TextView) findViewById(R.id.revise_location);
        ib_back = (ImageButton) findViewById(R.id.ib_revise_back);
        ib_finish = (ImageButton) findViewById(R.id.ib_revise_finish);
        btn_head.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_gender.setOnClickListener(this);
        tv_birth.setOnClickListener(this);
        ib_finish.setOnClickListener(this);
        ib_back.setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        BGAPhotoHelper.onSaveInstanceState(bgaPhotoHelper, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BGAPhotoHelper.onRestoreInstanceState(bgaPhotoHelper, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.revise_image || view.getId() == R.id.head_revise) {
            registerForContextMenu(view);
            openContextMenu(view);
            unregisterForContextMenu(view);
        }
        else if(view.getId() == R.id.revise_birth) {
            Calendar c = Calendar.getInstance();
            String birthday = null;
            new BirthActivity(ReviseActivity.this, 0, new BirthActivity.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker DatePicker, int Year, int MonthOfYear,
                                      int DayOfMonth) {
                    String birthday = String.format("%d-%d-%d", Year, MonthOfYear + 1,DayOfMonth);
                    tv_birth.setText(birthday);
                    //调用接口
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), false).show();

        }
        else if(view.getId() == R.id.revise_gender) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //定义一个AlertDialog
            String[] strarr = {"男","女","保密"};
            builder.setItems(strarr, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface arg0, int arg1)
                {
                    String sex = "保密";
                    if (arg1 == 0) {
                        sex = "男";
                    }else if(arg1 == 1){
                        sex = "女";
                    }
                    else {
                        sex = "保密";
                    }
                    tv_gender.setText(sex);
                    //调用接口
                }
            });
            builder.show();
        }
        else if(view.getId() == R.id.revise_location) {
            CityConfig cityConfig = new CityConfig.Builder()
                    .title("选择城市")//标题
                    .titleTextSize(18)//标题文字大小
                    .titleTextColor("#585858")//标题文字颜  色
                    .titleBackgroundColor("#E9E9E9")//标题栏背景色
                    .confirTextColor("#585858")//确认按钮文字颜色
                    .confirmText("确定")//确认按钮文字
                    .confirmTextSize(16)//确认按钮文字大小
                    .cancelTextColor("#585858")//取消按钮文字颜色
                    .cancelText("取消")//取消按钮文字
                    .cancelTextSize(16)//取消按钮文字大小
                    .setCityWheelType(CityConfig.WheelType.PRO_CITY)//显示类，只显示省份一级，显示省市两级还是显示省市区三级
                    .showBackground(true)//是否显示半透明背景
                    .visibleItemsCount(7)//显示item的数量
                    .province("山东省")//默认显示的省份
                    .city("青岛市")//默认显示省份下面的城市
                    //.district("崂山区")//默认显示省市下面的区县数据
                    .provinceCyclic(false)//省份滚轮是否可以循环滚动
                    .cityCyclic(false)//城市滚轮是否可以循环滚动
                    .districtCyclic(false)//区县滚轮是否循环滚动
                    //.setCustomItemLayout(R.layout.item_city)//自定义item的布局
                    //.setCustomItemTextViewId(R.id.item_city_name_tv)//自定义item布局里面的textViewid
                    .drawShadows(false)//滚轮不显示模糊效果
                    .setLineColor("#03a9f4")//中间横线的颜色
                    .setLineHeigh(5)//中间横线的高度
                    .setShowGAT(true)//是否显示港澳台数据，默认不显示
                    .build();
            mPicker.setConfig(cityConfig);
            mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @Override
                public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                    String Province = null;
                    String City = null;
                    if (province != null) {
                        Province = province.getName();
                    }
                    if (city != null) {
                        City = city.getName();
                    }
                    if (district != null) {
                    }
                    String location = Province + "-" + City;
                    if (Province.equals(City))
                        location = Province;
                    tv_location.setText(location);
                    //调用接口
                }
            });
            mPicker.showCityPicker( );
        }

        else if(view.getId() == R.id.ib_revise_back) {
            finish();
        }
        else if(view.getId() == R.id.ib_revise_finish) {
            //调用接口
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_album) {
            choosePhoto();
        } else if (id == R.id.menu_take_photo) {
            takePhoto();
        }
        return true;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_CHOOSE_PHOTO)
    public void choosePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivityForResult(bgaPhotoHelper.getChooseSystemGalleryIntent(), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "请开启存储空间权限，以正常使用Instagram", REQUEST_CODE_PERMISSION_CHOOSE_PHOTO, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    public void takePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                startActivityForResult(bgaPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
            } catch (Exception e) {
                BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
            }
        } else {
            EasyPermissions.requestPermissions(this, "请开启存储空间和相机权限，以正常使用Instagram", REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                try {
                    startActivityForResult(bgaPhotoHelper.getCropIntent(bgaPhotoHelper.getFilePathFromUri(data.getData()), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    bgaPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                try {
                    startActivityForResult(bgaPhotoHelper.getCropIntent(bgaPhotoHelper.getCameraFilePath(), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    bgaPhotoHelper.deleteCameraFile();
                    bgaPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CROP) {
                BGAImage.display(iv_head, R.mipmap.bga_pp_ic_holder_light, bgaPhotoHelper.getCropFilePath(), BGABaseAdapterUtil.dp2px(200));
            }
        } else {
            if (requestCode == REQUEST_CODE_CROP) {
                bgaPhotoHelper.deleteCameraFile();
                bgaPhotoHelper.deleteCropFile();
            }
        }
    }

    private void showToast(String s) {
        Toast.makeText(ReviseActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}

/*
        FlowerHttp flowerHttp = new FlowerHttp("http://118.25.40.220/api/getInfo/");
        Map<String, Object> map = new HashMap<>();
        String response = flowerHttp.post(map);
        int rsNum = 10;
        String birthday = null, gender = null, address = null, email = null, src = null, username = null;
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            birthday = jsonObject.getString("birthday");
            gender = jsonObject.getString("gender");
            if(gender.equals("M") || gender.equals("m"))
                gender = "男";
            else if(gender.equals("F") || gender.equals("f"))
                gender = "女";
            else if(gender.equals("S") || gender.equals("s"))
                gender = "保密";
            address = jsonObject.getString("address");
            email = jsonObject.getString("email");
            username = jsonObject.getString("username");
            src = "http://118.25.40.220/" + jsonObject.getString( "src");
            jsonObject = jsonArray.getJSONObject(1);
            rsNum = jsonObject.getInt("rsNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(rsNum == 0) {
            showToast("出现未知错误");
        }
        else if(rsNum == -1) {
            showToast("用户不存在");
        }
        else if(rsNum == 1) {
            tv_birth.setText(birthday);
            tv_location.setText(address);
            tv_email.setText(email);
            tv_gender.setText(gender);
            Glide.with(this).load(src).into(iv_head);
        }
        ll_revise_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviseActivity.this, ChangeCodeActivity.class);
                startActivity(intent);
            }
        });


    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagepath = getImagePath(uri, null);
        displayImage();
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(){
        if(imagepath != null) {
            final File file = new File(imagepath);
            if (!file.exists()) {
                showToast("文件不存在");
                return;
            }
            String endName = null;
            int dot = file.getName().lastIndexOf('.');
            if ((dot >-1) && (dot < (file.getName().length() - 1))) {
                endName =  file.getName().substring(dot + 1);
            }
            try {
                if(new FileInputStream(file).available() / 1024 / 1024 > 2) {
                    showToast("文件大小超过2M，请选择低于2M的图片");
                    return;
                }
                else if(!endName.equals("jpg")) {
                    showToast("仅支持jpg类型的文件上传，您选择的文件非jpg格式");
                    return;
                }
                else {
                    Glide.with(ReviseActivity.this).load(imagepath).into(iv_head);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int result = 10;
                                SharedPreferences mShared;
                                mShared = MainApplication.getContext().getSharedPreferences("share", MODE_PRIVATE);
                                String csrfmiddlewaretoken = null;
                                String cookie = null;
                                Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
                                for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
                                    String key = item_map.getKey();
                                    Object value = item_map.getValue();
                                    if(key.equals("Cookie")) {
                                        cookie = value.toString();
                                    }
                                    else if(key.equals("csrfmiddlewaretoken")) {
                                        csrfmiddlewaretoken = value.toString();
                                    }
                                }
                                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                                String filename = file.getName();
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("image", filename, fileBody)
                                        .addFormDataPart("csrfmiddlewaretoken", csrfmiddlewaretoken)
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://118.25.40.220/api/changeHeadImage/")
                                        .header("Cookie", cookie)
                                        .post(requestBody)
                                        .build();
                                Response response;
                                OkHttpClient okHttpClient = new OkHttpClient();
                                response = okHttpClient.newCall(request).execute();
                                String responseData = response.body().string();
                                try {
                                    result = new JSONObject(responseData).getInt("rsNum");
                                    Looper.prepare();
                                    if(result == 1) {
                                        showToast("修改头像成功");
                                    }
                                    else if(result == 0) {
                                        showToast("未知错误");
                                    }
                                    else if(result == -1) {
                                        showToast("文件太大");
                                    }
                                    else if(result == -2) {
                                        showToast("没有检测到登录");
                                    }
                                    else if(result == 10) {
                                        showToast("服务器未响应");
                                    }
                                    Looper.loop();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            showToast("没有找到图片");
        }
    }
}*/
