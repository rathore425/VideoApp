package com.example.videaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Main2Activity extends AppCompatActivity {

    private Uri videoUri;
    private EditText mPostTitle;
    private static final int REQUEST_CODE =101;
    private StorageReference videoRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        mPostTitle = (EditText) findViewById(R.id.titleField);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserVideos").child("videos" + uid);

        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();

        videoRef = storageRef.child("/videos/" + uid + "userIntro.3gp");
    }

    public void upload(View view){
        if (videoUri != null){
            UploadTask uploadTask = videoRef.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Main2Activity.this,
                            "Upload Failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(

                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

//                        final String title_val = mPostTitle.getText().toString().trim();

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri uri = null;
                            assert false;
                            String download_url = uri.toString();
                            DatabaseReference newPost = mDatabase.push();
                            newPost.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override

                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {

//                                        mProgressDialog.dismiss();
                                        Toast.makeText(Main2Activity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();

                                    }else {
                                        Toast.makeText(Main2Activity.this, "Error happened during the upload process", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });



//                            Uri downloadurl = taskSnapshot.getMetadata().getDownloadUrl();
//                            DatabaseReference newPost = mDatabase.push();
//                            newPost.child("title").setValue(title_val);
//                            newPost.child("video").setValue(downloadurl.toString());
//
//                            Toast.makeText(Main2Activity.this, "Video successfuly sent!",
//                                    Toast.LENGTH_LONG).show();


                            //checking if the value is provided
//                            if (!TextUtils.isEmpty(title_val)) {
//
//                                mPostTitle.setText("");
//
//
//                            } else {
//                                //if the value is not given displaying a toast
//
//                            }
                        }
                    }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);
                        }
                    });


        }else {
            Toast.makeText(Main2Activity.this, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot){

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();


        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress =(100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }

    public void record(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void download(View view){

        try{
            final File localFile = File.createTempFile("userIntro", "3gp");

            videoRef.getFile(localFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess
                                (FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Main2Activity.this, "Playing....",
                                    Toast.LENGTH_LONG).show();

                            final VideoView videoView=
                                    (VideoView) findViewById(R.id.videoView);
                            videoView.setVideoURI(Uri.fromFile(localFile));
                            videoView.start();

                        }

                    }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Main2Activity.this,
                            "Download Failed:" + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(Main2Activity.this,
                    "Failed to create temp  file;" + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        videoUri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video Saved to:\n" +
                        videoUri, Toast.LENGTH_LONG).show();

            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording Cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record Video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }




}
