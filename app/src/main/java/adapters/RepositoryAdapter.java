package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hs.userportal.Directory;
import com.hs.userportal.DirectoryFile;
import com.hs.userportal.R;
import com.hs.userportal.SelectableObject;

import java.util.List;

import utils.AppConstant;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.ViewHolder> {

    private Directory directory;
    private List<SelectableObject> listOfObjects;
    private Context context;
    private onDirectoryAction listener;
    private boolean selectionMode = false, mCalledFromGallery ;

    public RepositoryAdapter(Activity context, Directory directory, List<SelectableObject> listOfObjects, onDirectoryAction listener, boolean fromGallery) {
        this.context = context;
        this.directory = directory;
        this.listener = listener;
        this.listOfObjects = listOfObjects;
        mCalledFromGallery = fromGallery ;

    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (SelectableObject recycled : listOfObjects) {
            recycled.setSelected(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_singlerow_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (listOfObjects.get(position).getObject() instanceof Directory) {
            if (((Directory) listOfObjects.get(position).getObject()).isLocked()) {
                holder.image.setImageResource(R.drawable.ic_folder_protected);
            } else {
                holder.image.setImageResource(R.drawable.ic_folder);
            }
            holder.name.setText(((Directory) listOfObjects.get(position).getObject()).getDirectoryName());
            if(mCalledFromGallery){
                holder.lastModified.setVisibility(View.GONE);
                holder.size.setVisibility(View.GONE);
            }else{
                holder.lastModified.setText("--");
                holder.size.setText("--");
            }

            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode) {
                        if (!((Directory) listOfObjects.get(position).getObject()).isLocked()) {
                            if (holder.checkBox.isChecked()) {
                                holder.checkBox.setChecked(false);
                                listOfObjects.get(position).setSelected(false);
                            } else {
                                holder.checkBox.setChecked(true);
                                listOfObjects.get(position).setSelected(true);
                            }
                        } else {
                            holder.checkBox.setChecked(false);
                            listOfObjects.get(position).setSelected(false);
                        }


                    } else {
                        listener.onDirectoryTouched((Directory) listOfObjects.get(position).getObject());
                    }
                }
            });
            holder.row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClicked(position);
                    if (!((Directory) listOfObjects.get(position).getObject()).isLocked())
                        listOfObjects.get(position).setSelected(true);
                    return true;
                }
            });

        } else if (listOfObjects.get(position).getObject() instanceof DirectoryFile) {
            if(((DirectoryFile) listOfObjects.get(position).getObject()).getOtherExtension()){

                if(( ((DirectoryFile) listOfObjects.get(position).getObject()).getExtensionType().endsWith("pdf"))){
                    holder.image.setImageResource(R.drawable.pdf_ico);
                }else if(( ((DirectoryFile) listOfObjects.get(position).getObject()).getExtensionType().endsWith("doc"))){
                    holder.image.setImageResource(R.drawable.ic_doc);
                }else if(( ((DirectoryFile) listOfObjects.get(position).getObject()).getExtensionType().endsWith("xls"))){
                    holder.image.setImageResource(R.drawable.ic_excel);
                }
            }else{
                Glide.with(context)
                        .load(AppConstant.AMAZON_URL + ((DirectoryFile) listOfObjects.get(position).getObject()).getThumb())
                        .crossFade()
                        .into(holder.image);
            }

            holder.name.setText(((DirectoryFile) listOfObjects.get(position).getObject()).getName());
            if(mCalledFromGallery){
                holder.name.setTextColor(Color.parseColor("#919499"));
                holder.lastModified.setVisibility(View.GONE);
                holder.size.setVisibility(View.GONE);
                holder.image.setAlpha(.3f);
            }else{
                if(((DirectoryFile) listOfObjects.get(position).getObject()).getLastModified() != null) {
                    holder.lastModified.setText(((DirectoryFile) listOfObjects.get(position).getObject()).getLastModified().toString());
                }
                holder.size.setText("" + (((DirectoryFile) listOfObjects.get(position).getObject()).getSize()) + " kb");
            }

            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode) {
                        if (holder.checkBox.isChecked()) {
                            holder.checkBox.setChecked(false);
                            listOfObjects.get(position).setSelected(false);
                        } else {
                            holder.checkBox.setChecked(true);
                            listOfObjects.get(position).setSelected(true);
                        }
                    } else {
                        listener.onImageTouched((DirectoryFile) listOfObjects.get(position).getObject());
                    }

                }
            });
            holder.row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClicked(position);
                    listOfObjects.get(position).setSelected(true);
                    return true;
                }
            });
        }


        if (selectionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (listOfObjects.get(position).isSelected()) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            listOfObjects.get(position).setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return listOfObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, lastModified, size;
        private ImageView image;
        private LinearLayout row;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            lastModified = (TextView) itemView.findViewById(R.id.lastModified);
            size = (TextView) itemView.findViewById(R.id.size);
            image = (ImageView) itemView.findViewById(R.id.image);

            row = (LinearLayout) itemView.findViewById(R.id.row);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    public interface onDirectoryAction {
        void onDirectoryTouched(Directory directory);

        void onImageTouched(DirectoryFile file);

        void onItemLongClicked(int position);
    }


    public Directory getDirectory() {
        return directory;
    }

    public boolean isInSelectionMode() {
        return selectionMode;
    }

}
