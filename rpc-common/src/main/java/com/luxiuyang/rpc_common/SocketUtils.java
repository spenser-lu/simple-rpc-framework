package com.luxiuyang.rpc_common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SocketUtils {

    public static byte[] int2Bytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static int bytes2Int(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << ((3 - i) * 8);
        }
        return res;

    }

    public byte[] mergeBytes(byte[] ... bytes){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for(byte[] b:bytes){
                baos.write(b);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        int a = 0;
        byte[] bytes = int2Bytes(a);
        int b = bytes2Int(bytes);
        System.out.println(b);
    }
}
