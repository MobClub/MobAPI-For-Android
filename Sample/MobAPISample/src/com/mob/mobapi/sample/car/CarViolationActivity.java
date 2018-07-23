/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，
 * 我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.mob.mobapi.sample.car;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.CarViolation;
import com.mob.mobapi.sample.R;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarViolationActivity extends Activity implements View.OnClickListener, APICallback {
	private boolean isQuerying = false;

	private EditText etPlateNumber;
	private EditText etEngineNumber;
	private Spinner spPlateType;
	private Spinner spAreaCode;

	private TextView tvResult;
	private List<String> plateTypeList;
	private List<String> areaCodeList;

	private InputMethodManager inputMethodManager;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_violation);
		etPlateNumber = ResHelper.forceCast(findViewById(R.id.etPlateNumber));
		spPlateType = ResHelper.forceCast(findViewById(R.id.spPlateType));
		etEngineNumber = ResHelper.forceCast(findViewById(R.id.etEngineNumber));
		spAreaCode = ResHelper.forceCast(findViewById(R.id.spAreaCode));
		tvResult = ResHelper.forceCast(findViewById(R.id.tvResult));

		etPlateNumber.setText("苏FT218Y");
		etEngineNumber.setText("M92654");

		findViewById(R.id.btnSearch).setOnClickListener(this);

		plateTypeList = new ArrayList<String>();
		List<String> plateTypeValueList = new ArrayList<String>();
		areaCodeList = new ArrayList<String>();
		List<String> areaCodeValueList = new ArrayList<String>();

		try {
			String[] plateTypeArray = getResources().getStringArray(R.array.car_plate_type);
			String[] areaCodeArray = getResources().getStringArray(R.array.car_area_code);
			String[] tmp;
			for (String item : plateTypeArray) {
				tmp = item.split("\\|");
				if (tmp != null && tmp.length == 2) {
					plateTypeList.add(tmp[0]);
					plateTypeValueList.add(tmp[1]);
				}
			}
			for (String item : areaCodeArray) {
				tmp = item.split("\\|");
				if (tmp != null && tmp.length == 2) {
					areaCodeList.add(tmp[0]);
					areaCodeValueList.add(tmp[1]);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		spPlateType.setAdapter(new ArrayAdapter<String>(this, R.layout.view_weather_district, plateTypeValueList));
		spAreaCode.setAdapter(new ArrayAdapter<String>(this, R.layout.view_weather_district, areaCodeValueList));

		spPlateType.setSelection(1);
		spAreaCode.setSelection(34);

	}

	public void onClick(View v) {
		if (isQuerying) {
			return;
		}
		isQuerying = true;
		hideIme(v);
		if (v.getId() == R.id.btnSearch) {
			//根据航线查询
			String plateType = plateTypeList.get(spPlateType.getSelectedItemPosition());
			String areaCode = areaCodeList.get(spAreaCode.getSelectedItemPosition());
			((CarViolation) MobAPI.getAPI(CarViolation.NAME)).queryInfo(etPlateNumber.getText().toString(), plateType,
					etEngineNumber.getText().toString(), areaCode, CarViolationActivity.this);
		}
	}

	public void onSuccess(API api, int action, Map<String, Object> result) {
		try {
			Map<String, Object> res = ResHelper.forceCast(result.get("result"));
			if (res != null && res.size() > 0) {
				switch (action) {
					case CarViolation.ACTION_INFO: {
						StringBuilder sb = new StringBuilder();
						sb.append(getString(R.string.car_violation_api_count_total, ResHelper.forceCast(res.get("total"), null)));
						sb.append("\n");
						sb.append(getString(R.string.car_violation_api_count_local, ResHelper.forceCast(res.get("local"), null)));
						sb.append("\n");
						sb.append(getString(R.string.car_violation_api_count_province_in, ResHelper.forceCast(res.get("province_in"), null)));
						sb.append("\n");
						sb.append(getString(R.string.car_violation_api_count_province_out, ResHelper.forceCast(res.get("province_out"), null)));
						tvResult.setText(sb.toString());
					} break;
				}
			} else {
				tvResult.setText(R.string.car_violation_api_res_null);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		isQuerying = false;
	}

	public void onError(API api, int action, Throwable details) {
		details.printStackTrace();
		Toast.makeText(this, R.string.error_raise, Toast.LENGTH_SHORT).show();
		isQuerying = false;
	}

	private void hideIme(View view) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
