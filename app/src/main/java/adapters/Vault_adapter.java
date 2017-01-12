package adapters;

import android.app.Activity;
import android.content.Context;
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
public class Vault_adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> data_list;
    private ImageLoader mImageLoader;
    private String patientId;
    private String path_buffer;
    private boolean islist;

    public Vault_adapter(Activity activity, ArrayList<HashMap<String, String>> SortList,
                         boolean list_view, String patientId, String path_buffer) {
        this.activity = activity;
        this.data_list = SortList;
        this.patientId = patientId;
        this.islist = list_view;
        this.path_buffer = path_buffer;
        mImageLoader = MyVolleySingleton.getInstance(activity).getImageLoader();
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object getItem(int location) {
        return data_list.get(location);
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
            convertView = inflater.inflate(R.layout.vault_list_item, null);

        NetworkImageView image_vault = (NetworkImageView) convertView.findViewById(R.id.image_vault);
        TextView img_name = (TextView) convertView.findViewById(R.id.img_name);
        TextView img_date = (TextView) convertView.findViewById(R.id.img_date);
        TextView img_size = (TextView) convertView.findViewById(R.id.img_size);
        CheckBox delete = (CheckBox) convertView.findViewById(R.id.delete);

        try {
            if (data_list.size() != 0) {
                // image_vault.setImageUrl("https://files.healthscion.com/" + data_list.get(position).get("FileVault2"), mImageLoader);
            }

            //String pdf_name = data_list.get(position).get("FileVault2").replace(patientId + "/FileVault/", "");
            String[] pdf_name = data_list.get(position).get("Personal3").split("/");
            int length = pdf_name.length;
            String time = data_list.get(position).get("LastModified");
            String[] parts = time.split("T");
            String part1 = parts[0];
            //  String part2 = parts[1];
            if (part1.equalsIgnoreCase("null")) {
                part1 = "";
                img_date.setText(part1);
            } else {
                String[] date_format = part1.split("-");
                img_date.setText(date_format[2] + "-" + date_format[1] + "-" + date_format[0]);
            }
            img_name.setText(pdf_name[length - 1].toString());




            /*if (islist) {
                delete.setVisibility(View.VISIBLE);
            }*/

            // img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if (!data_list.get(position).get("Personal3").contains(".PNG") && !data_list.get(position).get("Personal3").contains(".png") &&
                    !data_list.get(position).get("Personal3").contains(".jpg") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".JPG") &&
                    !data_list.get(position).get("Personal3").contains(".xls") &&
                    !data_list.get(position).get("Personal3").contains(".doc")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                if(data_list.get(position).get("Personal3").equalsIgnoreCase("Prescription")||
                        data_list.get(position).get("Personal3").equalsIgnoreCase("Insurance")||
                        data_list.get(position).get("Personal3").equalsIgnoreCase("Bills")||data_list.get(position).get("Personal3").equalsIgnoreCase("Reports") ) {
                    image_vault.setDefaultImageResId(R.drawable.ic_folder_protected);
                }else{
                    image_vault.setDefaultImageResId(R.drawable.ic_folder);
                }
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                img_size.setText("         -");
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

                    if (!data_list.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
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
                   /* if (data_list.get(position).get("FileVault2").contains(".png")) {
                        if (data_list.get(position).get("FileVault2").startsWith(patientId + "/FileVault/" + path_buffer)) {
                            thumbimg = data_list.get(position).get("FileVault2").replaceAll("\\.png", "_thumb.png");
                        } else if (data_list.get(i).get("FileVault2").contains(patientId + "/FileVault/")) {
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
                img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if (data_list.get(position).get("Personal3").contains(".pdf") &&
                    !data_list.get(position).get("Personal3").contains(".txt") &&
                    !data_list.get(position).get("Personal3").contains(".xls") &&
                    !data_list.get(position).get("Personal3").contains(".doc")) {
                image_vault.setDefaultImageResId(R.drawable.pdfimg);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if ((data_list.get(position).get("Personal3").contains(".xls") || data_list.get(position).get("Personal3").contains(".xlsx")) &&
                    !data_list.get(position).get("Personal3").contains(".pdf") && !data_list.get(position).get("Personal3").contains(".doc")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                image_vault.setDefaultImageResId(R.drawable.ic_excel);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if ((data_list.get(position).get("Personal3").contains(".doc") || data_list.get(position).get("Personal3").contains(".docx")) &&
                    !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".txt")) {
                image_vault.setDefaultImageResId(R.drawable.ic_doc);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else if (data_list.get(position).get("Personal3").contains(".txt") &&
                    !data_list.get(position).get("Personal3").contains(".xls") && !data_list.get(position).get("Personal3").contains(".pdf")
                    && !data_list.get(position).get("Personal3").contains(".doc")) {
                image_vault.setDefaultImageResId(R.drawable.ic_text);
                image_vault.setAdjustViewBounds(true);
                image_vault.setImageUrl(null, null);
                img_size.setText(readableFileSize(Long.valueOf(data_list.get(position).get("Size"))));
            } else {

            }

        } catch (ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
        }

        return convertView;
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "Kb", "Mb", "Gb", "Tb"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
