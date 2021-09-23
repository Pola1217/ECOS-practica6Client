package com.example.ecos_practica6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecos_practica6.model.Bola;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button upBtn, rightBtn, leftBtn, downBtn, colorBtn;
    private BufferedReader bfr;
    private BufferedWriter bfw;
    private int posX=250, posY=200, r, g ,b ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //boton direcciones
        upBtn = findViewById(R.id.upBtn);
        downBtn = findViewById(R.id.downBtn);
        rightBtn = findViewById(R.id.leftBtn);
        leftBtn = findViewById(R.id.rightBtn);

        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);

        //boton colores
        colorBtn = findViewById(R.id.colorBtn);

        colorBtn.setOnClickListener(this);

        new Thread(
                ()-> {
                    // Ponemos la IP del server y el puerto donde el servidor escucha
                    try {
                        Socket socket = new Socket("10.0.2.2", 9000);

                        InputStream is = socket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        bfr = new BufferedReader(isr);

                        OutputStream os = socket.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        bfw = new BufferedWriter(osw);

                        //recibe msgs (lectura)
                        while(true) {
                            try {

                                String msg = bfr.readLine(); //para hasta que haya msg
                                Log.d("Mensaje recibido: ", msg);

                                json();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //getMsg();

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.upBtn:

                posY-=10;
                Toast.makeText(this, "arriba", Toast.LENGTH_LONG).show();
                json();

                break;

            case R.id.downBtn:

                posY+=10;
                Toast.makeText(this, "abajo", Toast.LENGTH_LONG).show();
                json();

                break;

            case R.id.rightBtn:

                posX+=10;
                Toast.makeText(this, "derecha", Toast.LENGTH_LONG).show();
                json();

                break;

            case R.id.leftBtn:

                posX-=10;
                Toast.makeText(this, "izquierda", Toast.LENGTH_LONG).show();
                json();

                break;

            case R.id.colorBtn:

                r = new Random().nextInt(255);
                g = new Random().nextInt(255);
                b = new Random().nextInt(255);
                Toast.makeText(this, "color", Toast.LENGTH_LONG).show();
                json();

                break;

        }
    }

    public void json() {
        Gson gson = new Gson();
        Bola bola = new Bola(posX, posY,r,g,b);

        String msgs = gson.toJson(bola);
        sentMsgs(msgs);

    }


    public void sentMsgs(String msg) {

        new Thread(

                () -> {
                    try {

                        bfw.write(msg+"\n");
                        bfw.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();
    }

}