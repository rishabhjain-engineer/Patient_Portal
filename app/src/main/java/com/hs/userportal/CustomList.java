package com.hs.userportal;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	private final Activity context;

	private final ArrayList<String> description;
	private final ArrayList<String> sample;
	private final ArrayList<String> labnumber;
	private final ArrayList<String> ispublished;
	private final ArrayList<String> testcomplete;
	private ImageView imageView;

	public CustomList(Activity context, ArrayList<String> description,
			ArrayList<String> sample, ArrayList<String> testcomplete,
			ArrayList<String> ispublished, ArrayList<String> labnumber,
			ImageView imageView) {

		super(context,R.layout.list_item, description);

		this.context = context;
		this.description = description;
		this.testcomplete = testcomplete;
		this.labnumber = labnumber;
		this.imageView = imageView;
		this.sample = sample;
		this.ispublished = ispublished;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		imageView = (ImageView) rowView.findViewById(R.id.img);

		txtTitle.setText(description.get(i));
		
		if (testcomplete.get(i).equals("true") && sample.get(i).equals("true")) {

			if (ispublished.get(i).equals("truePAID"))
				imageView.setImageResource(R.drawable.image1);
			else
				imageView.setImageResource(R.drawable.image3);

		} else if (testcomplete.get(i).equals("null")
				&& sample.get(i).equals("true"))
			imageView.setImageResource(R.drawable.image3);
		else
			imageView.setImageResource(R.drawable.image2);
		return rowView;
	}
}
