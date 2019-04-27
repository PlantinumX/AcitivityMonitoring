package com.example.activitymonitoring;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.nio.charset.Charset.forName;


public class RawDataReader {
	public String path_to_raw = "WISDM_ar_v1.1_raw.txt";
	public File file;
	private Activity activity;
	private StringBuilder text = new StringBuilder();
	private List<Float> xValues = new ArrayList<>();
	private List<Float> yValues = new ArrayList<>();
	private List<Float> zValues = new ArrayList<>();
	private List<String> labels = new ArrayList<>();
	RawDataReader(Activity activity) {
		try {
			this.activity = activity;
			this.file = new File(this.activity.getFilesDir(), path_to_raw);

		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	void loadData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replace(';',' ');
				String[] data = line.split(",");

				if (data.length > 5) {
					labels.add(data[1]);
					xValues.add(Float.parseFloat(data[3]));
					yValues.add(Float.parseFloat(data[4]));
					zValues.add(Float.parseFloat(data[5].split(" ")[0]));
				}

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void prepareData(Matrix matrix) {
		Log.e("TEST","TENKSD\n");

		for(int i = 0; i < labels.size();i++) {
			String currentLabel = labels.get(i);
			int j = i;
			int sub_i = 0;
			while (labels.get(j).compareTo(currentLabel) == 0 && sub_i < 10) {
				j++;
				sub_i++;
			}

			i = j;
		}
	}
}
