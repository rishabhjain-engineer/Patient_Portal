package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hs.userportal.MyVolleySingleton;
import com.hs.userportal.R;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashish on 2/5/2016.
 */
public class Vault_delete_adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> data_list;
    private ImageLoader mImageLoader;
    private String patientId;
    private boolean islist;
    private boolean[] thumbnailsselection1;
    private String path_buffer;

    public Vault_delete_adapter(Activity activity, ArrayList<HashMap<String, String>> SortList,
                                boolean list_view, String patientId, boolean[] thumbnailsselection, String path_buffer) {
        this.activity = activity;
        this.data_list = SortList;
        this.islist = list_view;
        this.patientId = patientId;
        this.path_buffer = path_buffer;
        // thumbnailsselection1 = new boolean[data_list.size()];
        this.thumbnailsselection1 = thumbnailsselection;
        mImageLoader = MyVolleySingleton.getInstance(activity).getImageLoader();
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object getItem(int location) {
        /*return data_list.get(location);*/
        return data_list.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.vault_listitem_delete, null);
            holder.image_vault = (NetworkImageView) convertView.findViewById(R.id.image_vault);
            holder.img_name = (TextView) convertView.findViewById(R.id.img_name);
            holder.img_size = (TextView) convertView.findViewById(R.id.img_size);
            holder.delete = (CheckBox) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       /* NetworkImageView image_vault = (NetworkImageView) convertView.findViewById(R.id.image_vault);*/
      /*  TextView img_name = (TextView) convertView.findViewById(R.id.img_name);
        TextView img_size = (TextView) convertView.findViewById(R.id.img_size);
        CheckBox delete = (CheckBox) convertView.findViewById(R.id.delete);*/
      /*  delete.setId(position);
        delete.setChecked(thumbnailsselection1[position]);
        delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (thumbnailsselection1[id]) {
                    cb.setChecked(false);
                    thumbnailsselection1[id] = false;
                } else {
                    cb.setChecked(true);
                    thumbnailsselection1[id] = true;
                }
            }
        });*/
        holder.delete.setId(position);
        holder.image_vault.setId(position);

        holder.delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (!data_list.get(id).get("Personal3").contains(".PNG") && !data_list.get(id).get("Personal3").contains(".png") &&
                        !data_list.get(id).get("Personal3").contains(".jpg")
                        && !data_list.get(id).get("Personal3").contains(".JPG") && !data_list.get(id).get("Personal3").contains(".pdf")
                        && !data_list.get(id).get("Personal3").contains(".xls") && !data_list.get(id).get("Personal3").contains(".doc")
                        && !data_list.get(id).get("Personal3").contains(".txt") && (data_list.get(id).get("Personal3").equals("Prescription") ||
                        data_list.get(id).get("Personal3").equals("Insurance")
                        || data_list.get(id).get("Personal3").equals("Bills")
                        || data_list.get(id).get("Personal3").equals("Reports"))) {
                    cb.setChecked(false);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    // set title
                    alertDialogBuilder.setTitle("Alert");
                    // set dialog message
                    alertDialogBuilder.setMessage("Operation not allowed on locked folders.")
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    if (thumbnailsselection1[id]) {
                        cb.setChecked(false);
                        thumbnailsselection1[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection1[id] = true;
                    }
                }
            }
        });

        try {

            if (data_list.size() != 0) {
                //   holder.image_vault.setImageUrl("https://files.healthscion.com/" + data_list.get(position).get("FileVault2"), mImageLoader);
            }
            // String pdf_name = data_list.get(position).get("FileVault2").replace(patientId + "/FileVault/", "");
            String[] pdf_name = data_list.get(position).get("Personal3").split("/");
            int length = pdf_name.length;
            String time = data_list.get(position).get("LastModified");
            String[] parts = time.split("T");
            String part1 = parts[0];
            //  String part2 = parts[1];
            if (part1.equalsIgnoreCase("null")) {
                part1 = "";
            }
            holder.img_name.setText(pdf_name[length - 1].toString());
            /*if (islist) {
                delete.setVisibility(View.VISIBLE);
            }*/
            holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if (!data_list.get(position).get("Personal3").contains(".PNG") && !data_list.get(position).get("Personal3").contains(".png") &&
                    !data_list.get(position).get("Personal3").contains(".jpg") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".doc")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                if (data_list.get(position).get("Personal3").equalsIgnoreCase("Prescription") ||
                        data_list.get(position).get("Personal3").equalsIgnoreCase("Insurance") ||
                        data_list.get(position).get("Personal3").equalsIgnoreCase("Bills") ||
                        data_list.get(position).get("Personal3").equalsIgnoreCase("Reports")) {
                    holder.image_vault.setDefaultImageResId(R.drawable.ic_folder_protected);
                } else {
                    holder.image_vault.setDefaultImageResId(R.drawable.ic_folder);
                }
                holder.image_vault.setAdjustViewBounds(true);
                holder.image_vault.setImageUrl(null, null);
                holder.img_size.setText("-");
            } else if (!data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".doc") &&
                    !data_list.get(position).get("Personal3").contains(".pdf") && !data_list.get(position).get("Personal3").contains(".txt")) {

                String thumbimg = "";
                if (data_list.get(position).get("Personal3").contains(".png")) {
                    thumbimg = data_list.get(position).get("Personal3").replaceAll("\\.png", "_thumb.png");
                } else if (data_list.get(position).get("Personal3").contains(".PNG")) {
                    thumbimg = data_list.get(position).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                } else if (data_list.get(position).get("Personal3").contains(".jpg")) {
                    thumbimg = data_list.get(position).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                } else if (data_list.get(position).get("Personal3").contains(".JPG")) {
                    thumbimg = data_list.get(position).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                }

                if (path_buffer.equalsIgnoreCase("")) {
                    if (!data_list.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        holder.image_vault.setDefaultImageResId(R.drawable.box);
                        holder.image_vault.setErrorImageResId(R.drawable.ic_error);
                        holder.image_vault.setAdjustViewBounds(true);
                        holder.image_vault.setImageUrl("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    } else {
                        holder.image_vault.setDefaultImageResId(R.drawable.box);
                        holder.image_vault.setErrorImageResId(R.drawable.ic_error);
                        holder.image_vault.setAdjustViewBounds(true);
                        holder.image_vault.setImageUrl("https://files.healthscion.com/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    }
                } else {

                    if (!data_list.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        holder.image_vault.setDefaultImageResId(R.drawable.box);
                        holder.image_vault.setErrorImageResId(R.drawable.ic_error);
                        holder.image_vault.setAdjustViewBounds(true);
                        holder.image_vault.setImageUrl("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer + "/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    } else {
                        holder.image_vault.setDefaultImageResId(R.drawable.box);
                        holder.image_vault.setErrorImageResId(R.drawable.ic_error);
                        holder.image_vault.setAdjustViewBounds(true);
                        holder.image_vault.setImageUrl("https://files.healthscion.com/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    }
                }
                holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if (data_list.get(position).get("Personal3").contains(".pdf") &&
                    !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".doc")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                holder.image_vault.setDefaultImageResId(R.drawable.pdfimg);
                holder.image_vault.setAdjustViewBounds(true);
                holder.image_vault.setImageUrl(null, null);
                holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if ((data_list.get(position).get("Personal3").contains(".xls") || data_list.get(position).get("Personal3").contains(".xlsx")) &&
                    !data_list.get(position).get("Personal3").contains(".pdf") && !data_list.get(position).get("Personal3").contains(".doc")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                holder.image_vault.setDefaultImageResId(R.drawable.ic_excel);
                holder.image_vault.setAdjustViewBounds(true);
                holder.image_vault.setImageUrl(null, null);
                holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if ((data_list.get(position).get("Personal3").contains(".doc") || data_list.get(position).get("Personal3").contains(".docx")) &&
                    !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                holder.image_vault.setDefaultImageResId(R.drawable.ic_doc);
                holder.image_vault.setAdjustViewBounds(true);
                holder.image_vault.setImageUrl(null, null);
                holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if (data_list.get(position).get("Personal3").contains(".txt") &&
                    !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".doc")) {
                holder.image_vault.setDefaultImageResId(R.drawable.ic_text);
                holder.image_vault.setAdjustViewBounds(true);
                holder.image_vault.setImageUrl(null, null);
                holder.img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else {

            }

        } catch (ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.delete.setChecked(thumbnailsselection1[position]);
        holder.id = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!data_list.get(position).get("Personal3").contains(".PNG") && !data_list.get(position).get("Personal3").contains(".png") &&
                        !data_list.get(position).get("Personal3").contains(".jpg")
                        && !data_list.get(position).get("Personal3").contains(".JPG") && !data_list.get(position).get("Personal3").contains(".pdf")
                        && !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".doc")
                        && !data_list.get(position).get("Personal3").contains(".txt") && (data_list.get(position).get("Personal3").equals("Prescription") ||
                        data_list.get(position).get("Personal3").equals("Insurance")
                        || data_list.get(position).get("Personal3").equals("Bills")
                        || data_list.get(position).get("Personal3").equals("Reports"))) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    // set title
                    alertDialogBuilder.setTitle("Alert");
                    // set dialog message
                    alertDialogBuilder.setMessage("Operation not allowed on locked folders.")
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    thumbnailsselection1[position] = !thumbnailsselection1[position];
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        NetworkImageView image_vault;
        TextView img_name;
        TextView img_size;
        CheckBox delete;
        int id;
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "Kb", "Mb", "Gb", "Tb"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
