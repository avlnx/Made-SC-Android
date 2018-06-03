package com.made.madesc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import stone.application.StoneStart;
import stone.application.interfaces.StoneCallbackInterface;
import stone.environment.Environment;
import stone.providers.ActiveApplicationProvider;
import stone.user.UserModel;
import stone.utils.Stone;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static com.made.madesc.ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck;

@RuntimePermissions
public class ValidationActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mMessageTextView;
    public static final String STONE_APPLICATION_CODE = "185346049";
    public static final String TAG = ValidationActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_SETTINGS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        // Set up initial interface
        mMessageTextView = findViewById(R.id.tv_message);
        mMessageTextView.setText("Configurando o ambiente");
        mProgressBar = findViewById(R.id.pb_progress);

//        initiateApp();
        initiateAppWithPermissionCheck(this);

//        initiateAppWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initiateApp() {

        /**
         * Este deve ser, obrigatoriamente, o primeiro metodo
         * a ser chamado. E um metodo que trabalha com sessao.
         */
        List<UserModel> user = StoneStart.init(this);

        Stone.setEnvironment(Environment.SANDBOX);

        // se retornar nulo, voce provavelmente nao ativou a SDK
        // ou as informacoes da Stone SDK foram excluidas
        if (user != null) {
            /* caso ja tenha as informacoes da SDK e chamado o ActiveApplicationProvider anteriormente
               sua aplicacao podera seguir o fluxo normal */
            continueApp();
        }

        // user null, load provider
        List<String> stoneCodeList = new ArrayList<>();
        // Adicione seu Stonecode abaixo, como string.
        stoneCodeList.add(STONE_APPLICATION_CODE);

        final ActiveApplicationProvider provider = new ActiveApplicationProvider(this);
        mMessageTextView.setText("Ativando o aplicativo...");
//        provider.setDialogMessage("Ativando o aplicativo...");
//        provider.setDialogTitle("Aguarde");
        provider.useDefaultUI(false);
        provider.setConnectionCallback(new StoneCallbackInterface() {
            /* Metodo chamado se for executado sem erros */
            public void onSuccess() {
                Toast.makeText(ValidationActivity.this, "Ativado com sucesso, iniciando...", Toast.LENGTH_SHORT).show();
                continueApp();
            }

            /* metodo chamado caso ocorra alguma excecao */
            public void onError() {
                Toast.makeText(ValidationActivity.this, "Erro na ativacao do aplicativo, verifique a lista de erros do provider", Toast.LENGTH_SHORT).show();

                /* Chame o metodo abaixo para verificar a lista de erros. Para mais detalhes, leia a documentacao: */
                Log.e(TAG, "onError: " + provider.getListOfErrors().toString());

            }
        });
        provider.activate(stoneCodeList);
    }

    private void continueApp() {
        Intent intent = new Intent(this, PinpadActivity.class);
        startActivity(intent);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
        buildPermissionDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.proceed();
            }
        });
    }

    private void buildPermissionDialog(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Android 6.0")
                .setCancelable(false)
                .setMessage("Com a versão do android igual ou superior ao Android 6.0," +
                        " é necessário que você aceite as permissões para o funcionamento do app.\n\n")
                .setPositiveButton("OK", listener)
                .create().show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDenied() {
        buildPermissionDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initiateAppWithPermissionCheck(ValidationActivity.this);
            }
        });
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskAgain() {
        buildPermissionDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            initiateAppWithPermissionCheck(this);
        }
        ValidationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
