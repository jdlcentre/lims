/*
 * Copyright Curtin University, 2015.
 */
// DO NOT MODIFY THIS FILE BY HAND. IT WAS GENERATED BY generate_orm_files.py
package au.edu.curtin.lims.db.hibernate4;

import javax.inject.Inject;

import au.edu.curtin.lims.db.IAnalysisRequestStateRepository;
import au.edu.curtin.lims.domain.AnalysisRequestState;

import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;

@Repository
public class HibernateAnalysisRequestStateRepository extends BaseHibernateRepository<AnalysisRequestState> implements IAnalysisRequestStateRepository {
    @Inject
    public HibernateAnalysisRequestStateRepository(SessionFactory sessionFactory) {
        super(AnalysisRequestState.class, sessionFactory);
    }

    public AnalysisRequestState save(AnalysisRequestState analysisRequestState) {
        this.currentSession().save(analysisRequestState);
        return analysisRequestState;
//        return new AnalysisRequestState(
//            (Integer) id,
//            analysisRequestState.getAnalysisRequestStateName()
//);
    }
}
