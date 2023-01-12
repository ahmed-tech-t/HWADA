package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.hwada.Model.Report;
import com.example.hwada.repository.ReportRepository;

public class ReportViewModel extends AndroidViewModel {

    ReportRepository repository;
    public ReportViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
    }
    public void addReport(Report report){
        repository.addReport(report);
    }
}
