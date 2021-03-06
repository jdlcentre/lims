/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "analysis_request")
@SequenceGenerator(name = "analysis_request_analysis_request_id_seq", sequenceName = "analysis_request_analysis_request_id_seq", allocationSize = 1)
public class AnalysisRequest implements Comparable<AnalysisRequest> {
    private int analysisRequestId;

    private AnalysisRequestState analysisRequestState;

    private AnalysisCapability analysisCapability;

    private Person person;

    private Date requestDate;

    private Set<Geosample> geosamples = new HashSet<Geosample>();

    private List<Result> results;

    public AnalysisRequest() { }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_request_analysis_request_id_seq")
    @Column(name = "analysis_request_id", nullable = false)
    public Integer getAnalysisRequestId() {
        return this.analysisRequestId;
    }
    
    @SuppressWarnings("unused")
    private void setAnalysisRequestId(Integer analysisRequestId) {
        this.analysisRequestId = analysisRequestId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_request_state_id")
    public AnalysisRequestState getAnalysisRequestState() {
        return analysisRequestState;
    }
    
    public void setAnalysisRequestState(AnalysisRequestState analysisRequestState) {
        this.analysisRequestState = analysisRequestState;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_capability_id")
    public AnalysisCapability getAnalysisCapability() {
        return analysisCapability;
    }
    
    public void setAnalysisCapability(AnalysisCapability analysisCapability) {
        this.analysisCapability = analysisCapability;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date", nullable = false)
    public Date getRequestDate() {
        return this.requestDate;
    }
    
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "analysis_request_geosample_mm",
        joinColumns = { @JoinColumn(name = "analysis_request_id") },
        inverseJoinColumns = { @JoinColumn(name = "geosample_id") })
    public Set<Geosample> getGeosamples() {
        return this.geosamples;
    }
    
    public void setGeosamples(Set<Geosample> geosamples) {
        this.geosamples = geosamples;
    }

    @OneToMany(
        mappedBy = "analysisRequest",
        fetch = FetchType.LAZY)
    @OrderBy("result_id")
    public List<Result> getResults() {
        return this.results;
    }
    
    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int compareTo(AnalysisRequest o) {
        return this.getAnalysisRequestId().compareTo(o.getAnalysisRequestId());
    }
}
