package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hs.userportal.MyVolleySingleton;
import com.hs.userportal.R;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashish on 2/11/2016.
 */
public class Folder_adapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> folder;
    private LayoutInflater inflater;
    private String patientId;
    private String path_buffer;
    private ImageLoader mImageLoader;

    public Folder_adapter(Activity activity, ArrayList<HashMap<String, String>> folder, String patientId, String path_buffer) {
        this.activity = activity;
        this.folder = folder;
        this.patientId = patientId;
        this.path_buffer = path_buffer;
        mImageLoader = MyVolleySingleton.getInstance(activity).getImageLoader();
    }

    @Override
    public int getCount() {
        return folder.size();
    }

    @Override
    public Object getItem(int location) {
        return folder.get(location);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.move_folder_adapter, null);
        NetworkImageView image_vault = (NetworkImageView) convertView.findViewById(R.id.icon);
        TextView folder_name = (TextView) convertView.findViewById(R.id.folder_name);
        ImageView previewImage = (ImageView) convertView.findViewById(R.id.previewImage);
        /*folder_name.setText(folder.get(position).get("folder_name"));*/
        try {
            String[] pdf_name = folder.get(position).get("folder_name").split("/");
            int length = pdf_name.length;
            folder_name.setText(pdf_name[length - 1].toString());

            if (!folder.get(position).get("folder_name").contains(".PNG") && !folder.get(position).get("folder_name").contains(".png") &&
                    !folder.get(position).get("folder_name").contains(".jpg") && !folder.get(position).get("folder_name").contains(".pdf")
                    && !folder.get(position).get("folder_name").contains(".JPG") &&
                    !folder.get(position).get("folder_name").contains(".xls") &&
                    !folder.get(position).get("folder_name").contains(".doc")
                    && !folder.get(position).get("folder_name").contains(".txt")) {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                folder_name.setTextColor(Color.parseColor("#000000"));
                if (folder.get(position).get("folder_name").equalsIgnoreCase("Prescription") ||
                        folder.get(position).get("folder_name").equalsIgnoreCase("Insurance") ||
                        folder.get(position).get("folder_name").equalsIgnoreCase("Bills")||
                        folder.get(position).get("folder_name").equalsIgnoreCase("Reports")) {
                    image_vault.setDefaultImageResId(R.drawable.ic_folder_protected);
                } else {
                   image_vault.setDefaultImageResId(R.drawable.ic_folder);
                }
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                previewImage.setImageResource(0);
            } else if (!folder.get(position).get("folder_name").contains(".xls") && !folder.get(position).get("folder_name").contains(".doc") &&
                    !folder.get(position).get("folder_name").contains(".pdf") && !folder.get(position).get("folder_name").contains(".txt")) {
                convertView.setBackgroundColor(Color.parseColor("#F4F4F4"));
                String thumbimg = "";
                folder_name.setTextColor(Color.parseColor("#C3C3C3"));
                if (folder.get(position).get("folder_name").contains(".png")) {
                    thumbimg = folder.get(position).get("folder_name").replaceAll("\\.png", "_thumb.png");
                } else if (folder.get(position).get("folder_name").contains(".PNG")) {
                    thumbimg = folder.get(position).get("folder_name").replaceAll("\\.PNG", "_thumb.PNG");
                } else if (folder.get(position).get("folder_name").contains(".jpg")) {
                    thumbimg = folder.get(position).get("folder_name").replaceAll("\\.jpg", "_thumb.jpg");
                } else if (folder.get(position).get("folder_name").contains(".JPG")) {
                    thumbimg = folder.get(position).get("folder_name").replaceAll("\\.JPG", "_thumb.JPG");
                }
                if (path_buffer.equalsIgnoreCase("")) {
                    if (!folder.get(position).get("folder_name").contains(patientId + "/FileVault/Personal/")) {
                        image_vault.setDefaultImageResId(R.drawable.box);
                        image_vault.setErrorImageResId(R.drawable.ic_error);
                        image_vault.setAdjustViewBounds(true);
                        image_vault.setImageUrl("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    } else {
                        image_vault.setDefaultImageResId(R.drawable.box);
                        image_vault.setErrorImageResId(R.drawable.ic_error);
                        image_vault.setAdjustViewBounds(true);
                        image_vault.setImageUrl("https://files.healthscion.com/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    }
                } else {

                    if (!folder.get(position).get("folder_name").contains(patientId + "/FileVault/Personal/")) {
                        image_vault.setDefaultImageResId(R.drawable.box);
                        image_vault.setErrorImageResId(R.drawable.ic_error);
                        image_vault.setAdjustViewBounds(true);
                        image_vault.setImageUrl("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.replaceAll(" ", "%20") + "/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    } else {
                        image_vault.setDefaultImageResId(R.drawable.box);
                        image_vault.setErrorImageResId(R.drawable.ic_error);
                        image_vault.setAdjustViewBounds(true);
                        image_vault.setImageUrl("https://files.healthscion.com/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    }
                   /* if (folder.get(position).get("FileVault2").contains(".png")) {
                        if (folder.get(position).get("FileVault2").startsWith(patientId + "/FileVault/" + path_buffer)) {
                            thumbimg = folder.get(position).get("FileVault2").replaceAll("\\.png", "_thumb.png");
                        } else if (folder.get(i).get("FileVault2").contains(patientId + "/FileVault/")) {
                            imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("FileVault2"));
                        } else {
                            imageobject.put("Key", patientId + "/FileVault/" + path_buffer.toString() + "/" + thumbImage.get(i).get("FileVault2"));
                        }
                        imageobject.put("Type", "0");
                        if (thumbimg.startsWith(patientId + "/FileVault/" + path_buffer)) {
                            imageobject.put("ThumbFile", thumbimg);
                        } else if (thumbimg.contains(patientId + "/FileVault/")) {
                            imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                        } else {
                            imageobject.put("ThumbFile", patientId + "/FileVault/" + path_buffer.toString() + "/" + thumbimg);
                        }
                    }*/
                }
                previewImage.setImageResource(R.drawable.transparent_box);
            } else if (folder.get(position).get("folder_name").contains(".pdf") &&
                    !folder.get(position).get("folder_name").contains(".txt") &&
                    !folder.get(position).get("folder_name").contains(".xls") &&
                    !folder.get(position).get("folder_name").contains(".doc")) {
                image_vault.setDefaultImageResId(R.drawable.pdfimg);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                convertView.setBackgroundColor(Color.parseColor("#F4F4F4"));
                folder_name.setTextColor(Color.parseColor("#C3C3C3"));
                previewImage.setImageResource(R.drawable.transparent_box);
            } else if ((folder.get(position).get("folder_name").contains(".xls") || folder.get(position).get("folder_name").contains(".xlsx")) &&
                    !folder.get(position).get("folder_name").contains(".pdf") && !folder.get(position).get("folder_name").contains(".doc")
                    && !folder.get(position).get("folder_name").contains(".txt")) {
                image_vault.setDefaultImageResId(R.drawable.ic_excel);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                convertView.setBackgroundColor(Color.parseColor("#F4F4F4"));
                folder_name.setTextColor(Color.parseColor("#C3C3C3"));
                previewImage.setImageResource(R.drawable.transparent_box);
            } else if ((folder.get(position).get("folder_name").contains(".doc") || folder.get(position).get("folder_name").contains(".docx")) &&
                    !folder.get(position).get("folder_name").contains(".xls") && !folder.get(position).get("folder_name").contains(".pdf")
                    && !folder.get(position).get("folder_name").contains(".txt")) {
                image_vault.setDefaultImageResId(R.drawable.ic_doc);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                convertView.setBackgroundColor(Color.parseColor("#F4F4F4"));
                folder_name.setTextColor(Color.parseColor("#C3C3C3"));
                previewImage.setImageResource(R.drawable.transparent_box);
            } else if (folder.get(position).get("folder_name").contains(".txt") &&
                    !folder.get(position).get("folder_name").contains(".xls") && !folder.get(position).get("folder_name").contains(".pdf")
                    && !folder.get(position).get("folder_name").contains(".doc")) {
                image_vault.setDefaultImageResId(R.drawable.ic_text);
                image_vault.setAdjustViewBounds(true);
                convertView.setBackgroundColor(Color.parseColor("#F4F4F4"));
                folder_name.setTextColor(Color.parseColor("#C3C3C3"));
                image_vault.setImageUrl(null, null);
                previewImage.setImageResource(R.drawable.transparent_box);
            } else {
            }
        } catch (ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
        }

        return convertView;
    }
}
