package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        //user se storage ko acces karne keliye permission mangne wala message pop up karwane ka code
        Dexter.withContext(this)
                //kis chij ki permission chahiye
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                //permission dene pr kya hoga aur nhi dene pr kya hoga uska code
                .withListener(new PermissionListener() {
                    @Override
                    // agar user ne permission de di toh ye hoga
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {



                        //External storag ko read karenge and niche wale arraylist me daal denge
                        ArrayList<File>mysongs= fetchsongs(Environment.getExternalStorageDirectory());
                        String [] items= new String [mysongs.size()];
                        // songs ka naam bhi display ho pr last me .mp3 na dikhe
                        for(int i = 0; i<mysongs.size(); i++){
                            items[i]= mysongs.get(i).getName().replace("mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, items);
                          listView.setAdapter(adapter);
                          //song pr click krne pr new activity open honi chahiye
                          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                              @Override
                              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                  Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                  String currentSong = listView.getItemAtPosition(i).toString();
                                  intent.putExtra("songList", mysongs);
                                  intent.putExtra("currentSong", currentSong);
                                  intent.putExtra("position", i);
                                  startActivity(intent);


                              }
                          });
                    }


                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        //agar user ne permission nhi di toh wapas app open karne pr wapas permission mangni hain
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    //ye ek fuction he jo hamare files me se sare songs ko enlist kr dega
    public ArrayList<File>fetchsongs(File file){// ye ek method hain jisme file ek objct hain
        ArrayList arrayList = new ArrayList();//songs store karne keliye ek Arraylist banali
        File [] songs = file.listFiles();// ye File directory me jitni bhi files hain sabko list kr dega
        if(songs!=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchsongs(myFile));//recursion laga diya
                }else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);// agar file mp3 hain toh Arraylist me add kr do
                    }
                }
            }
        }
        return arrayList;
    }
}