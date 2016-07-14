package swipelist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hs.userportal.IndividualLabTest;
import com.hs.userportal.R;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemRow> {

	List<ItemRow>   data; 
	Context context;
	int layoutResID;

public ItemAdapter(Context context, int layoutResourceId,List<ItemRow> data) {
	super(context, layoutResourceId, data);
	
	this.data=data;
	this.context=context;
	this.layoutResID=layoutResourceId;

	// TODO Auto-generated constructor stub
}
 
@Override
public View getView(final int position, View convertView, ViewGroup parent) {
	
	NewsHolder holder = null;
	   View row = convertView;
	    holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);
            
            holder = new NewsHolder();
           
            holder.tvTestName = (TextView)row.findViewById(R.id.tvTestName);
            holder.tvTestPrice = (TextView)row.findViewById(R.id.tvTestPrice);
            holder.tvPayable = (TextView)row.findViewById(R.id.tvPayable);
           
           /* holder.button1=(Button)row.findViewById(R.id.swipe_button1);*/
            holder.delete=(Button)row.findViewById(R.id.swipe_button3);
            holder.cancel_id=(Button)row.findViewById(R.id.swipe_button2);
            row.setTag(holder);
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }
        
        final ItemRow itemdata = data.get(position);
        holder.tvTestName.setText(itemdata.getTestname());
        holder.tvTestPrice.setText(itemdata.getPriceactual());
        holder.tvPayable.setText(itemdata.getDiscountprice());
      
      
       /* holder.button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Button 1 Clicked",Toast.LENGTH_SHORT).show();
			}
		});*/
        
        holder.delete.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();


					
						String name=itemdata.getTestname();
						
						
		            	customListner.onDeleteList(name, position);
		            	

						//((IndividualLabTest) context).setPrices(name);

					}
				});
		 
		 holder.cancel_id.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					IndividualLabTest.swipelistview.closeAnimate(position);
				}
			});
        
        
        return row;
	
}



static class NewsHolder{
	
	TextView tvTestName,tvTestPrice,tvPayable;
	
	Button delete,cancel_id;
	
	}
	DeleteListener customListner;

	public interface DeleteListener {

		public void onDeleteList(String text, int pos);

		//public void onPkg_DetailsButtonClickListner(int position, String value);
	}
	public void setdelete_list_Listener(DeleteListener listner){
		this.customListner = listner;
	}

}




