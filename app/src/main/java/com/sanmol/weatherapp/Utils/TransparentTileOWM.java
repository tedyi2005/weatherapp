package com.sanmol.weatherapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Teddy Kidanne on 10/17/2017.
 */

public class TransparentTileOWM implements TileProvider {
    //private String url;
    private Paint opacityPaint = new Paint();
    private String tileType;

    private static final String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=%s";

    public TransparentTileOWM(String tileType) {
        this.tileType = tileType;
        setOpacity(50);
    }

    public void setOpacity(int opacity) {
        int alpha = (int) Math.round(opacity * 2.55);    // 2.55 = 255 * 0.01
        opacityPaint.setAlpha(alpha);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        URL tileUrl = getTileUrl(x, y, zoom);

        Tile tile = null;
        ByteArrayOutputStream stream = null;

        try {
            Bitmap image = BitmapFactory.decodeStream(tileUrl.openConnection().getInputStream());
            image = adjustOpacity(image);

            stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            tile = new Tile(256, 256, byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }

        return tile;
    }

    private URL getTileUrl(int x, int y, int zoom) {
        String tileUrl = String.format(OWM_TILE_URL, tileType, zoom, x, y, Constants.API_KEY_VALUE);
        try {
            return new URL(tileUrl);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    private Bitmap adjustOpacity(Bitmap bitmap) {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);

        return adjustedBitmap;
    }

}

