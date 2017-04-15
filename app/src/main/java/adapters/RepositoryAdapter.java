package adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hs.userportal.Directory;
import com.hs.userportal.DirectoryFile;
import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import utils.AppConstant;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryAdapter extends BaseAdapter {

    private Directory directory;
    private List<Object> listOfObjects;
    private ArrayList<Object> arraylist;
    private Activity context;
    private onDirectoryAction listener;

    public RepositoryAdapter(Activity context, Directory directory, onDirectoryAction listener) {
        this.directory = directory;
        this.context = context;
        this.listener = listener;
        listOfObjects = new ArrayList<>();

        if (this.directory.getParentDirectory() != null) {
            listOfObjects.add(0);
        }
        if (!this.directory.listOfDirectories.isEmpty()) {
            for (Directory d : this.directory.getListOfDirectories()) {
                listOfObjects.add(d);
            }
        }
        if (!this.directory.getListOfDirectoryFiles().isEmpty()) {
            for (DirectoryFile file : this.directory.getListOfDirectoryFiles()) {
                listOfObjects.add(file);
            }
        }

    }

    @Override
    public int getCount() {
        Log.e("Rishabh", "List of object size := " + listOfObjects.size());
        return listOfObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.repository_singlerow_layout, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.lastModified = (TextView) convertView.findViewById(R.id.lastModified);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.row = (LinearLayout) convertView.findViewById(R.id.row);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (listOfObjects.get(position) instanceof Integer) {
            holder.image.setImageResource(R.drawable.ic_folder);
            holder.name.setText("...");
            holder.lastModified.setText("--");
            holder.size.setText("--");
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDirectoryTouched(directory.getParentDirectory());
                }
            });

        } else if (listOfObjects.get(position) instanceof Directory) {
            if (((Directory) listOfObjects.get(position)).isLocked()) {
                holder.image.setImageResource(R.drawable.ic_folder_protected);
            } else {
                holder.image.setImageResource(R.drawable.ic_folder);
            }
            holder.name.setText(((Directory) listOfObjects.get(position)).getDirectoryName());
            holder.lastModified.setText("--");
            holder.size.setText("--");
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDirectoryTouched((Directory) listOfObjects.get(position));
                }
            });

        } else if (listOfObjects.get(position) instanceof DirectoryFile) {
            Glide.with(context)
                    .load(AppConstant.AMAZON_URL + ((DirectoryFile) listOfObjects.get(position)).getThumb())
                    .crossFade()
                    .into(holder.image);
            holder.name.setText(((DirectoryFile) listOfObjects.get(position)).getName());
            holder.lastModified.setText(((DirectoryFile) listOfObjects.get(position)).getLastModified().substring(0, 10));
            holder.size.setText("" + (((DirectoryFile) listOfObjects.get(position)).getSize() / 1000) + " kb");
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageTouched((DirectoryFile) listOfObjects.get(position));
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name, lastModified, size;
        ImageView image;
        LinearLayout row;
    }

    public Directory getParentDirectory(){
        return directory.getParentDirectory();
    }

    public interface onDirectoryAction {
        void onDirectoryTouched(Directory directory);

        void onImageTouched(DirectoryFile file);
    }

    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(listOfObjects);
        }
        else
        {
            for (arraylist al : listOfObjects)
            {
                if (al.getCountry().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    worldpopulationlist.add(al);
                }
            }
        }
        notifyDataSetChanged();
    }*/

}
