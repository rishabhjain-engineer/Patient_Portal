package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hs.userportal.Directory;
import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravimathpal on 18/04/17.
 */

public class RepositoryDialogAdapter extends RecyclerView.Adapter<RepositoryDialogAdapter.ViewHolder> {

    private Directory directory;
    private List<Directory> listOfDirectories;
    private onDirectorySelected listener;

    public RepositoryDialogAdapter(Directory directory, onDirectorySelected listener) {

        this.directory = directory;
        this.listener = listener;

        listOfDirectories = new ArrayList<>();
        for (Directory subDirectory : directory.getListOfDirectories()) {
            listOfDirectories.add(subDirectory);
        }
    }

    public void setDirectory(Directory directory){
        this.directory = directory;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_singlerow_dialog_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(listOfDirectories.get(position).getDirectoryName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directory = listOfDirectories.get(position);
                notifyDataSetChanged();
                listener.onDirectorySelected(listOfDirectories.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfDirectories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.folder_name);
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public interface onDirectorySelected {
        void onDirectorySelected(Directory directory);
    }

}
