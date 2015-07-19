package com.duan.nghenhac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ListChartActivity extends Activity implements OnClickListener {
	ImageView imgVN, imgAM, imgHQ;
	private static String idVN = "IWZ9Z08I";
	private static String idHQ = "IWZ9Z0BO";
	private static String idAM = "IWZ9Z0BW";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_chart);
		imgVN = (ImageView) findViewById(R.id.imageVN);
		imgAM = (ImageView) findViewById(R.id.imageAM);
		imgHQ = (ImageView) findViewById(R.id.imageHQ);
		getActionBar().hide();
		imgVN.setOnClickListener(this);
		imgAM.setOnClickListener(this);
		imgHQ.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(ListChartActivity.this, DetailActivity.class);
		switch (v.getId()) {
		case R.id.imageVN:
			intent.putExtra("id", idVN);
			startActivity(intent);
			break;
		case R.id.imageHQ:
			intent.putExtra("id", idHQ);
			startActivity(intent);
			break;
		case R.id.imageAM:
			intent.putExtra("id", idAM);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

}
