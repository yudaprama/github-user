package com.yudapramadjunaedi.githubuser.views;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.shapes.RectShape;

public class TranslucentDrawable extends PaintDrawable {

    public TranslucentDrawable() {
        setShape(new RectShape());
        createShader();
    }

    private void createShader() {
        ShaderFactory sf = new ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                double angleInRadians = Math.toRadians(90);

                int x1 = (int) (Math.cos(angleInRadians) * width);
                int y1 = (int) (Math.sin(angleInRadians) * height);
                int[] colors = new int[]{Color.argb(90, 0, 0, 0), Color.argb(15, 0, 0, 0), Color.TRANSPARENT};
                float[] positions = {0, 0.78f, 1};
                LinearGradient lg = new LinearGradient(0, 0, x1, y1,
                        colors,
                        positions,
                        Shader.TileMode.REPEAT);
                return lg;
            }
        };
        setShaderFactory(sf);
    }
}
