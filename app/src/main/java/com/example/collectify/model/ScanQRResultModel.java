package com.example.collectify.model;

public class ScanQRResultModel {
    public final static int CODE_STAMP_NOT_FOUND = 0;
    public final static int CODE_STAMP_FOUND = 1;
    public final static int CODE_STAMP_ALREADY_SCANNED = 2;
    public final static int CODE_STAMP_SCAN_SUCCESS = 3;

    public int code;
    public StampModel stamp;

    public ScanQRResultModel(int code, StampModel stamp) {
        this.code = code;
        this.stamp = stamp;
    }

    public String getMessage() {
        String msg = "";
        switch (this.code) {
            case CODE_STAMP_NOT_FOUND:
                msg = "Stempel tidak valid!";
                break;
            case CODE_STAMP_FOUND:
                msg = "Stempel valid";
                break;
            case CODE_STAMP_ALREADY_SCANNED:
                msg = "Stempel " + this.stamp.name + " sudah anda koleksi!";
                break;
            case CODE_STAMP_SCAN_SUCCESS:
                msg = "Stempel " + this.stamp.name +  " berhasil dikoleksi";
                break;
        }
        return msg;
    }
}
