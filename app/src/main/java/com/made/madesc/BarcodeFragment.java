package com.made.madesc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.made.madesc.model.KioskViewModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BarcodeFragment extends Fragment {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    private String lastText;
    private KioskViewModel mKioskViewModel;

    private Cart mCart;

    public BarcodeFragment() {

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                return;
            }

            lastText = result.getText();
            beepManager.playBeepSoundAndVibrate();
            Product product = Catalog.findProductWithBarcode(lastText);
            if (product != null) {
                mCart.addProductToCart(product);
            } else {
                // Product not found
                String resourceString = getResources().getString(R.string.barcode_product_not_found);
                Toast.makeText(getActivity(), String.format(resourceString, lastText), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kiosk_barcode, container, false);

        // Get a reference to the viewmodel so we can get the cart
        mKioskViewModel = ViewModelProviders.of(getActivity()).get(KioskViewModel.class);
        mKioskViewModel.getCart().observe(getActivity(), new Observer<Cart>() {
            @Override
            public void onChanged(@Nullable Cart cart) {
                mCart = cart;
            }
        });

        // Barcode stuff
        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_39,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_93,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.UPC_A,
                BarcodeFormat.AZTEC,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION,
                BarcodeFormat.CODE_128,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.ITF,
                BarcodeFormat.MAXICODE
        );
        barcodeView.setStatusText(getResources().getString(R.string.barcode_instructions));
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        barcodeView.resume();
        barcodeView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        barcodeView.pause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        barcodeView.pause();
    }
}
