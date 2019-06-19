package com.example.activitymonitoring;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.Collections;

public class LocalizationActivity extends BaseActivity {

    private Classifier classifier;
    public SensorHandler sensorHandler;
    public SensorManager sensorManager;
    public ParticleFilter particleFilter;
    private ParticleThread particleThread;
    private GuiUpdateThread guiUpdateThread;
    private Map map;
    private Motion motion = new Motion();
    private double mean_orientation = 0;
    private double median_orientation = 0;
    private double duration_sec;
    private double step_cnt = 0;
    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_localization_main);
        sensorHandler = new SensorHandler(this);
        try {

            this.particleThread = new ParticleThread();
            classifier = new Classifier(this);
            this.map = new Map(this);
            this.map.prepareMap();
            this.guiUpdateThread = new GuiUpdateThread(this);
            this.particleFilter = new ParticleFilter(this.map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ParticleThread implements Runnable {
        boolean needWait;

        public ParticleThread() {
            this.needWait = false;
        }

        @Override
        public void run() {
            Log.d("PARTICLEUPDATETHREAD ", "RUN METHOD");
            particleFilter.moveParticles(distance, median_orientation);
            this.needWait = true;
        }
    }

    private class GuiUpdateThread implements Runnable {
        LocalizationActivity localizationActivity;
        volatile boolean needWait;

        public GuiUpdateThread(LocalizationActivity localizationActivity) {
            this.localizationActivity = localizationActivity;
            this.needWait = false;
        }

        @Override
        public void run() {
            while (!particleThread.needWait) {
                Log.d("GUIUPDATETHEREAD", "WAITING FOR PARTICLE THREAD TO FINISH");
            }
            ;
            Log.d("GUIUPDATETHREAD ", "RUN METHOD");
            Bitmap map = this.localizationActivity.map.getOriginal_image().copy(this.localizationActivity.map.getOriginal_image().getConfig(), true);

            if (this.localizationActivity.map.estimated_pos.x != 0 && this.localizationActivity.map.estimated_pos.y != 0) {
                this.localizationActivity.map.delete_estimated_postion(map);
            }
            for (Particle particle : particleFilter.particles) {
                if (particle.getPos().x >= 0 && particle.getPos().y >= 0 && particle.getPos().x < map.getWidth() && particle.getPos().y < map.getHeight()) {
                    if (particle.getWeight() != 0.f) {
                        map.setPixel((int) particle.getPos().x, (int) particle.getPos().y, 0xFF0000FF);
                    }

                }

            }
            this.localizationActivity.map.draw_estimated_Position(particleFilter.particles, map);

            ImageView imageView = findViewById(R.id.image1);
            imageView.setImageBitmap(map);
            Log.d("GUIUPDATETHREAD", "FINISHED");
            this.needWait = true;
        }
    }

    public void updateEditView(Record record) {
        //TAKE SMALLER WINDOW

        motion.sample_cnt++;

        float[] result = classifier.predict(record);

        motion.angle.add(record.orientation);

        //sitting = [1]
        //walking = [0]
        //standing = [2]
        if (result[0] > result[1] && result[0] > result[2]) {
            motion.duration += record.duration;
        }


        if (motion.duration > 500) {

            for (int i = 0; i < motion.angle.size(); i++) {
                mean_orientation += motion.angle.get(i);
            }

            mean_orientation /= motion.angle.size();

            Collections.sort(motion.angle);

            median_orientation = (motion.angle.get(motion.angle.size() / 2));

//            Log.d("median", Double.toString(motion.angle.get(motion.angle.size()/2)));


//            Log.d("cycle", Long.toString(time_for_cyrcle));
//            Log.d("duration: ", Long.toString(motion.duration));
//            Toast.makeText(this, "duration: " + Long.toString(motion.duration) + "mean angle: " + Double.toString(orientation), Toast.LENGTH_LONG);

            duration_sec = (double) motion.duration / 1000;
            step_cnt = duration_sec * 2 + 0.5;
            distance = step_cnt * 0.95;
//            Log.d("activity 0 ",Double.toString(result[0]));
//            Log.d("activity 1 ",Double.toString(result[1]));
//            Log.d("activity 2 ",Double.toString(result[2]));
//            Log.d("duration_sec", Double.toString(duration_sec));
//            Log.d("distance", Double.toString(distance));
//            Log.d("steps", Integer.toString(step_cnt));
//
//                Log.d("mean angle", Double.toString(mean_orientation));
//            Log.d("motion angle size", Double.toString(motion.angle.size()));
//            Toast.makeText(this, "duration: " + distance + "mean angle: " + mean_orientation , Toast.LENGTH_LONG).show();
            if (Double.compare(distance, 0.f) != 0) {
//                    Log.d("LOCALIZATIONACTIVITY", "PARTICLE FILTER " + particleFilter.particles.length);
                sensorManager.unregisterListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                sensorManager.unregisterListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
                particleThread.run();
                guiUpdateThread.run();
                while (!particleThread.needWait || !guiUpdateThread.needWait) {
//                        Log.d("LOCALIZATIONACTIVITY", "WAITNING");
                }
                ;

                sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(sensorHandler, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
            }
            motion.duration = (long) 0;
            mean_orientation = 0;
            median_orientation = 0;
            motion.sample_cnt = 0;
            motion.angle.clear();
        }
    }

}
