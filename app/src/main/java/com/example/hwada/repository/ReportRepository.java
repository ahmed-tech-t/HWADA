package com.example.hwada.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Report;
import com.example.hwada.database.DbHandler;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportRepository {


    private Application application ;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    public void addReport(Report report){
        DocumentReference reportRef = rootRef.collection(DbHandler.reportCollection).document(report.getType()).collection(report.getType()).document();
        report.setId(reportRef.getId());
        reportRef.set(report);
    }

    public ReportRepository(Application application) {
        this.application = application;
    }
}
