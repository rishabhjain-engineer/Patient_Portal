package utils;

import com.cloudchowk.patient.Filevault;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashish on 2/15/2016.
 */
public class NavFolder {
    private String folder_name;
    private String hash_key;
    private ArrayList<HashMap<String, String>> navlist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> data_objects;

    public NavFolder(String folder_name, String hash_key) {
        this.folder_name = folder_name;
        this.hash_key = hash_key;
        navlist = Filevault.originalVaultlist;

    }

    public ArrayList<HashMap<String, String>> onFolderClickListener() {
        data_objects = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        int firsttimeval = 0;
        for (int i = 0; i < navlist.size(); i++) {
            int hashsize = navlist.get(i).size();
            String[] split = hash_key.split("Personal");
            int haskeynumber = Integer.parseInt(split[split.length - 1]);
            boolean check = false;
            int s3length = data_objects.size();
            if (navlist.get(i).get(hash_key) != null) {
                if (haskeynumber < hashsize && navlist.get(i).get(hash_key).equals(folder_name)) {
                    hmap = new HashMap<String, String>();
                    if (firsttimeval == 0) {
                        hmap = new HashMap<String, String>();
                        int number = haskeynumber + 1;
                        hmap.put("folder_name", navlist.get(i).get("Personal" + number));
                        hmap.put("hash_keyvalue", "Personal" + number);
                        hmap.put("LastModified", navlist.get(i).get("LastModified"));
                        hmap.put("Size", navlist.get(i).get("Size"));
                        data_objects.add(hmap);
                        firsttimeval = 1;
                    } else {
                        for (int j = 0; j < s3length; j++) {
                            if (data_objects.get(j).get("folder_name").equals(folder_name)) {
                                //origin_list.remove(i);
                                check = true;
                            }
                        }
                        if (check == false) {
                            int number = haskeynumber + 1;
                            hmap = new HashMap<String, String>();
                            hmap.put("folder_name", navlist.get(i).get("Personal" + number));
                            hmap.put("hash_keyvalue", "Personal" + number);
                            hmap.put("LastModified", navlist.get(i).get("LastModified"));
                            hmap.put("Size", navlist.get(i).get("Size"));
                            data_objects.add(hmap);

                        }
                    }
                }
           /* String foldername = navlist.get(i).get("FileVault2");*/
           /* int s3length = data_objects.size();
            boolean check = false;
            if (i == 0) {
                hmap = new HashMap<String, String>();
                hmap.put("folder_name", folder_name);
                hmap.put("hash_keyvalue", hash_key);
                hmap.put("LastModified", navlist.get(i).get("LastModified"));
                hmap.put("Size", navlist.get(i).get("Size"));
                data_objects.add(hmap);
            } else {
                for (int j = 0; j < s3length; j++) {
                    if (data_objects.get(j).get("folder_name").equalsIgnoreCase(folder_name)) {
                        //origin_list.remove(i);
                        check = true;
                    }
                }
                if (check == false) {

                    hmap = new HashMap<String, String>();
                    hmap.put("folder_name", folder_name);
                    hmap.put("hash_keyvalue", hash_key);
                    hmap.put("LastModified", navlist.get(i).get("LastModified"));
                    hmap.put("Size", navlist.get(i).get("Size"));
                    data_objects.add(hmap);
                }
            }*/
            }
        }

        return data_objects;
    }
}

