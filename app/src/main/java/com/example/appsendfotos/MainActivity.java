package com.example.appsendfotos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
public class MainActivity extends AppCompatActivity {
    // Se definen los objetos a necesitar
    ImageView Ver_Foto;
    Button Tomar_foto, Enviar_C, Enviar_W;
    String name;
    private static final int REQUEST_CODI_CAM = 100;
    private static final int REQUEST_CODI_CAP_FOTO= 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Se referencian los objetos
        Ver_Foto = findViewById(R.id.Mostrar_Foto);
        Tomar_foto = findViewById(R.id.Tomar_Foto);
        Enviar_C = findViewById(R.id.Correo);
        Enviar_W = findViewById(R.id.Whatsapp);
        // Función del botón al tomar la foto
        Tomar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se llama el método creado y se incluye el método
                // que almacena los datos al tomar la foto.
                Cargando_foto();
            }
        });
        // Botón para enviar por Whatsapp
        Enviar_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se llama al método que almacena como
                // enviar la foto por whatsapp.
                EnviarAWhatApp();
            }
        });
        // Botón para enviar por correo.
        Enviar_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se llama al método que almacena como
                // enviar la foto por correo.
                EnviarACorreo();
            }
        });
    }
    // Método de la verificación de los permisos.
    public void Cargando_foto(){
        // Verificaciópn de los permisos
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            // Tomará la foto.
            Foto();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CODI_CAM);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODI_CAM){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Foto();
            }else {
                Toast.makeText(MainActivity.this, "Acceso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // Tomar la foto.
    public void  Foto(){
        Intent Cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Cam.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Cam, REQUEST_CODI_CAP_FOTO);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODI_CAP_FOTO && resultCode == RESULT_OK){
            // Se busca la imagen con la función Bundle.
            Bundle BuscarImagen = data.getExtras();
            // Bitmap se utiliza para cargar laimgen de cualquier archivo.
            Bitmap Buscar_Foto = (Bitmap) BuscarImagen.get("data");
            Ver_Foto.setImageBitmap(Buscar_Foto);
            // se obtiene la información de la imagen.
            name = "MiImage.jpg";
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(name, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Buscar_Foto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    // Método para enviar la foto por whatsapp.
    public void EnviarAWhatApp(){
        // Se observa la foto una vez tomada con la cámara.
        Ver_Foto = findViewById(R.id.Mostrar_Foto);
        // Se crea el cache de la fotografía tomada con la camara.
        Ver_Foto.buildDrawingCache();
        // Se obtiene el cache de la fotografía tomada.
        Bitmap nuevo2 = Ver_Foto.getDrawingCache();
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.setPackage("com.whatsapp");
        // Se escribe la imagen en formato JPEG.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Se le da calidad a la fotografía tomada.
        nuevo2.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), nuevo2, "Titulo de la imagen", null);
        Uri uri = Uri.parse(path);
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(whatsappIntent);
    }
    // Método que almacena enviar la foto por correo electrónico.
    public void EnviarACorreo(){
        Ver_Foto = findViewById(R.id.Mostrar_Foto);
        Ver_Foto.buildDrawingCache();
        Bitmap nuevo = Ver_Foto.getDrawingCache();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mi Fotografía De Mi App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        nuevo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), nuevo, "Práctica android", null);
        Uri uri = Uri.parse(path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Enviar"));
    }
}