package fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.applozic.audiovideo.activity.VideoActivity;
import com.hs.userportal.AppAplication;
import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapters.ConsultFragmentAdapter;
import adapters.VaccineAdapter;
import models.DoctorDetails;
import ui.DoctorDetailsActivity;
import ui.VaccineActivity;
import ui.VaccineEditActivity;

/**
 * Created by ayaz on 2/6/17.
 */

public class ConsultFragment extends Fragment {
    private ListView mListView;
    private ConsultFragmentAdapter mConsultFragmentAdapter;
    private List<DoctorDetails> mDoctorDetailsList = new ArrayList<>();
    private Button mConsultNow;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consult, null);
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        mListView = (ListView) view.findViewById(R.id.consult_doctor_list);
        mConsultNow = (Button) view.findViewById(R.id.consult_now);
        mConsultNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoCallIntent = new Intent(getActivity(), VideoActivity.class);
                videoCallIntent.putExtra("CONTACT_ID", "0ac5fc1d-39aa-4636-b3b5-530d5b570fdc");
                startActivity(videoCallIntent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        mConsultFragmentAdapter = new ConsultFragmentAdapter(getActivity());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                DoctorDetails doctorDetails = (DoctorDetails) mListView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
                intent.putExtra("doctorDetail", doctorDetails);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        hederTitle.setText("Find a doctor");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DoctorDetails doctorDetails3 = new DoctorDetails();
        doctorDetails3.setDoctorName("Sajat");
        doctorDetails3.setLocation("Sector 22, Noida");
        doctorDetails3.setMedicineType("Family Medicine");
        doctorDetails3.setDoctorImage(R.drawable.update);
        doctorDetails3.setAboutDoctor("\n" +
                "Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails3);

        DoctorDetails doctorDetails1 = new DoctorDetails();
        doctorDetails1.setDoctorName("Ayaz");
        doctorDetails1.setLocation("Aminabad, Lucknow");
        doctorDetails1.setMedicineType("Family Medicine");
        doctorDetails1.setDoctorImage(R.drawable.ayaz);
        doctorDetails1.setAboutDoctor("\n" +
                "Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails1);

        DoctorDetails doctorDetails2 = new DoctorDetails();
        doctorDetails2.setDoctorName("Rishabh");
        doctorDetails2.setLocation("GTB Nagar, Delhi");
        doctorDetails2.setMedicineType("Family Medicine");
        doctorDetails2.setDoctorImage(R.drawable.update);
        doctorDetails2.setAboutDoctor("\n" +
                "Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails2);
        mConsultFragmentAdapter.setData(mDoctorDetailsList);
        mListView.setAdapter(mConsultFragmentAdapter);


    }
}
