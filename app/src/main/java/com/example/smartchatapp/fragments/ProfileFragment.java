package com.example.smartchatapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartchatapp.R;
import com.example.smartchatapp.model.Utils;
import com.example.smartchatapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    CircleImageView image_profile;
    MaterialEditText username;

    DatabaseReference reference;
    FirebaseUser fUser;

    StorageReference storageReference;
    SharedPreferences sharedPreferences;
    public static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;

    Spinner spnLan1,spnLan2;

    Button btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        image_profile=view.findViewById(R.id.profile_image);
        username=view.findViewById(R.id.username);
        spnLan1=view.findViewById(R.id.spnLan1);
        spnLan2=view.findViewById(R.id.spnLan2);
        btn_save=view.findViewById(R.id.btn_save);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        sharedPreferences=getContext().getSharedPreferences("myPreference", Context.MODE_PRIVATE);

        initSpinner();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.user);
                }
                else{
                    Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserName();
            }
        });

        return view;
    }

    private void updateUserName() {
        String txt_username=username.getText().toString().trim();
        if(!TextUtils.isEmpty(txt_username)){
            reference=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
            HashMap<String,Object> map=new HashMap<>();
            map.put("username",txt_username);
            reference.updateChildren(map);
        }
        else{
            Toast.makeText(getContext(), "username can not be empty..!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSpinner() {
        String str[]=Utils.lan;

        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,str);
        spnLan1.setAdapter(adapter1);

        spnLan1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveLanguage("fromLang",spnLan1.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapter2=new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,str);
        spnLan2.setAdapter(adapter2);

        spnLan2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveLanguage("toLang",spnLan2.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String fromLang = sharedPreferences.getString("fromLang", "").equals("")?"en:English":sharedPreferences.getString("fromLang", "");
        String toLang = sharedPreferences.getString("toLang", "").equals("")?"en:English":sharedPreferences.getString("toLang", "");

        int a=14,b=14;
        for(int i=0;i<str.length;i++){
            if(str[i].equals(fromLang)){
                a=i;
                break;
            }
        }
        for(int i=0;i<str.length;i++){
            if(str[i].equals(toLang)){
                b=i;
                break;
            }
        }
        
        spnLan1.setSelection(a);
        spnLan2.setSelection(b);
    }

    private void saveLanguage(String lanCap,String lan){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(lanCap,lan);
        editor.commit();
    }

    private void openImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if(imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        reference=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imageURL",imageUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(getContext(), "Upload Failed..!!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else{
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Uploading in progress..!!!", Toast.LENGTH_SHORT).show();
            }
            else{
                uploadImage();
            }
        }
    }
}
